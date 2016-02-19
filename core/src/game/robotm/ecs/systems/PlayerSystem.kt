package game.robotm.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import game.robotm.ecs.components.AnimationComponent
import game.robotm.ecs.components.PhysicsComponent
import game.robotm.ecs.components.PlayerComponent
import game.robotm.ecs.components.RendererComponent
import game.robotm.gamesys.GM


class PlayerSystem : IteratingSystem(Family.all(PlayerComponent::class.java, PhysicsComponent::class.java, AnimationComponent::class.java, RendererComponent::class.java).get()) {

    val playerM = ComponentMapper.getFor(PlayerComponent::class.java)
    val physicM = ComponentMapper.getFor(PhysicsComponent::class.java)
    val animM = ComponentMapper.getFor(AnimationComponent::class.java)
    val rendererM = ComponentMapper.getFor(RendererComponent::class.java)

    val tmpVec1 = Vector2()
    val tmpVec2 = Vector2()

    var playerCanJump = false
    var playerInAir = false

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val playerComponent = playerM.get(entity)
        val physicComponent = physicM.get(entity)
        val body = physicComponent.body
        val animationComponent = animM.get(entity)
        val rendererComponent = rendererM.get(entity)

        checkPlayerInAirAndCanJump(body)
        playerCanJump = playerCanJump and !playerComponent.hitCeiling

        var playerMoving = false
        if (!GM.gameOver && !GM.getReady) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && playerCanJump) {
                body.applyLinearImpulse(tmpVec1.set(0f, 8f - body.linearVelocity.y).scl(body.mass), body.worldCenter, true)
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                body.applyLinearImpulse(tmpVec1.set(-playerComponent.speed - body.linearVelocity.x, 0f).scl(body.mass), body.worldCenter, true)
                playerMoving = true
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                body.applyLinearImpulse(tmpVec1.set(playerComponent.speed - body.linearVelocity.x, 0f).scl(body.mass), body.worldCenter, true)
                playerMoving = true
            }
        }

        if (body.linearVelocity.x < -0.1f) {
            rendererComponent.sclX = -1f
        } else if (body.linearVelocity.x > 0.1f) {
            rendererComponent.sclX = 1f
        }

        if (playerInAir) {
            animationComponent.currentAnim = "fall"
        } else {
            if (playerMoving) {
                animationComponent.currentAnim = "move"
            } else {
                animationComponent.currentAnim = "idle"
            }
        }

        if (playerComponent.hitCeiling) {
            playerComponent.hitCeilingCountDown -= deltaTime

            body.fixtureList.forEach { fixture ->
                val filterData = fixture.filterData
                filterData.maskBits = GM.MASK_BITS_AFTER_HTTING_CEILING.toShort()
                fixture.filterData = filterData
            }

            if (playerComponent.hitCeilingCountDown <= 0) {
                playerComponent.hitCeilingCountDown = PlayerComponent.HIT_CEILING_COUNT_DOWN
                playerComponent.hitCeiling = false
                body.fixtureList.forEach { fixture ->
                    val filterData = fixture.filterData
                    filterData.maskBits = GM.MASK_BITS_PLAYER.toShort()
                    fixture.filterData = filterData
                }
            }
        }

        if (playerComponent.lethalContactCount > 0) {
            playerComponent.hp -= PlayerComponent.DAMAGE_PER_SECOND * deltaTime

            playerComponent.hp_regeneration_cd = PlayerComponent.HP_REGENERATION_COLD_DURATION
        } else {

            playerComponent.hp_regeneration_cd -= deltaTime
            if (playerComponent.hp_regeneration_cd <= 0 && !GM.gameOver) {
                playerComponent.hp = Math.min(PlayerComponent.FULL_HP, playerComponent.hp + PlayerComponent.HP_REGENERATION_PER_SECOND * deltaTime)
            }
        }

        if (body.position.y < GM.cameraY - GM.SCREEN_HEIGHT / 2f - 1f) {
            playerComponent.hp = 0f
        }

        if (playerComponent.hp <= 0) {
            GM.gameOver = true
        }
    }

    private fun checkPlayerInAirAndCanJump(body: Body) {
        playerCanJump = false
        playerInAir = true

        val world = body.world
        val x = body.position.x
        val y = body.position.y

        for (i in -1..1) {
            tmpVec1.set(x + (0.45f * i * GM.PLAYER_SCALE) , y)
            tmpVec2.set(x + (0.45f * i * GM.PLAYER_SCALE), y - (0.5f * GM.PLAYER_SCALE))

            world.rayCast(
                    {
                        fixture, point, normal, fraction ->

                        if (fixture.body === body) {
                            -1f
                        } else if (fraction <= 1) {
                            playerInAir = false
                            playerCanJump = if (fixture.filterData.categoryBits != GM.CATEGORY_BITS_FLOOR_UNJUMPABLE.toShort()) true else false
                            0f
                        } else {
                            fraction
                        }
                    }, tmpVec1, tmpVec2)
        }
    }

}
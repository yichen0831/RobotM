package game.robotm.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import game.robotm.ecs.components.AnimationComponent
import game.robotm.ecs.components.PhysicsComponent
import game.robotm.ecs.components.PlayerComponent
import game.robotm.ecs.components.RendererComponent


class PlayerSystem : IteratingSystem(Family.all(PlayerComponent::class.java, PhysicsComponent::class.java, AnimationComponent::class.java, RendererComponent::class.java).get()) {

    val playerM = ComponentMapper.getFor(PlayerComponent::class.java)
    val physicM = ComponentMapper.getFor(PhysicsComponent::class.java)
    val animM = ComponentMapper.getFor(AnimationComponent::class.java)
    val rendererM = ComponentMapper.getFor(RendererComponent::class.java)

    val tmpVector2 = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val physicComponent = physicM.get(entity)
        val body = physicComponent.body
        val animationComponent = animM.get(entity)
        val rendererComponent = rendererM.get(entity)

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            body.applyLinearImpulse(tmpVector2.set(0f, 1f), body.worldCenter, true)
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.applyLinearImpulse(tmpVector2.set(-0.1f, 0f), body.worldCenter, true)
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.applyLinearImpulse(tmpVector2.set(0.1f, 0f), body.worldCenter, true)
        }

        if (body.linearVelocity.x < -0.1f) {
            rendererComponent.sclX = -1f
        } else if (body.linearVelocity.x > 0.1f) {
            rendererComponent.sclX = 1f
        }

        if (Math.abs(body.linearVelocity.y) > 0.1f) {
            animationComponent.currentAnim = "fall"
        } else {
            if (Math.abs(body.linearVelocity.x) > 0.1f) {
                animationComponent.currentAnim = "move"
            } else {
                animationComponent.currentAnim = "idle"
            }
        }

    }

}
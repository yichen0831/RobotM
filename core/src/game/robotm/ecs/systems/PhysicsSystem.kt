package game.robotm.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import game.robotm.ecs.components.PhysicsComponent
import game.robotm.ecs.components.TransformComponent
import game.robotm.gamesys.GM


class PhysicsSystem : IteratingSystem(Family.all(TransformComponent::class.java, PhysicsComponent::class.java).get()) {

    val transformM = ComponentMapper.getFor(TransformComponent::class.java)
    val physicM = ComponentMapper.getFor(PhysicsComponent::class.java)

    override fun processEntity(entity: Entity,delta: Float) {
        val transformComponent = transformM.get(entity)
        val physicsComponent = physicM.get(entity)

        val body = physicsComponent.body

        transformComponent.x = body.position.x
        transformComponent.y = body.position.y
        transformComponent.rotation = body.angle * MathUtils.radiansToDegrees

        if (body.position.y > GM.cameraY + GM.SCREEN_HEIGHT / 2f + 0.5f) {
            body.world.destroyBody(body)
            engine.removeEntity(entity)
        }
    }

}
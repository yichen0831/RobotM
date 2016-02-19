package game.robotm.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Camera
import game.robotm.ecs.components.FollowCameraComponent
import game.robotm.ecs.components.PhysicsComponent


class FollowCameraSystem(val camera: Camera) : IteratingSystem(Family.all(PhysicsComponent::class.java, FollowCameraComponent::class.java).get()) {

    val physicsM = ComponentMapper.getFor(PhysicsComponent::class.java)
    val followCamM = ComponentMapper.getFor(FollowCameraComponent::class.java)

    override fun processEntity(entity: Entity, delta: Float) {
        val followCameraComponent = followCamM.get(entity)
        val body = physicsM.get(entity).body

        body.setTransform(camera.position.x + followCameraComponent.xDist, camera.position.y + followCameraComponent.yDist, 0f)

    }
}
package game.robotm.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import game.robotm.ecs.components.AnimationComponent
import game.robotm.ecs.components.InteractionComponent
import game.robotm.ecs.components.InteractionType
import game.robotm.ecs.components.PhysicsComponent


class InteractionSystem : IteratingSystem(Family.all(InteractionComponent::class.java, PhysicsComponent::class.java, AnimationComponent::class.java).get()) {

    val interactionM = ComponentMapper.getFor(InteractionComponent::class.java)
    val physicsM = ComponentMapper.getFor(PhysicsComponent::class.java)
    val animM = ComponentMapper.getFor(AnimationComponent::class.java)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val interactionComponent = interactionM.get(entity)
        val body = physicsM.get(entity).body
        val animationComponent = animM.get(entity)

        when (interactionComponent.type) {
            InteractionType.SPRING -> {
                when (interactionComponent.status) {
                    "normal" -> { animationComponent.currentAnim = "normal" }
                    "hit" -> {
                        animationComponent.currentAnim = "hit"
                        if (animationComponent.animations["hit"]!!.isAnimationFinished(animationComponent.animTime)) {
                            interactionComponent.status = "normal"
                        }
                    }
                }
            }
            InteractionType.ENEMY -> {}
            InteractionType.ITEM -> {}
        }

    }

}
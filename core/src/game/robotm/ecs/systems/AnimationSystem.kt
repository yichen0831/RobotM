package game.robotm.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import game.robotm.ecs.components.AnimationComponent
import game.robotm.ecs.components.RendererComponent

class AnimationSystem : IteratingSystem(Family.all(AnimationComponent::class.java, RendererComponent::class.java).get()) {

    val animationM = ComponentMapper.getFor(AnimationComponent::class.java)
    val rendererM = ComponentMapper.getFor(RendererComponent::class.java)

    override fun processEntity(entity: Entity, delta: Float) {
        val animationComponent = animationM.get(entity)
        val rendererComponent = rendererM.get(entity)

        val currentAnim = animationComponent.currentAnim
        val anim = animationComponent.animations[currentAnim]

        animationComponent.animTime += delta
        rendererComponent.textureRegion.setRegion(anim!!.getKeyFrame(animationComponent.animTime))
    }
}
package game.robotm.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import game.robotm.ecs.components.RendererComponent
import game.robotm.ecs.components.TransformComponent


class RenderSystem(val batch: SpriteBatch) : IteratingSystem(Family.all(RendererComponent::class.java, TransformComponent::class.java).get()) {

    val renderM = ComponentMapper.getFor(RendererComponent::class.java)
    val transformM = ComponentMapper.getFor(TransformComponent::class.java)

    override fun update(deltaTime: Float) {
        batch.begin()
        entities.forEach { processEntity(it, deltaTime) }
        batch.end()
    }

    override fun processEntity(entity: Entity, delta: Float) {
        val transformComponent = transformM.get(entity)
        val rendererComponent = renderM.get(entity)

        val textureRegion = rendererComponent.textureRegion

        batch.draw(textureRegion,
                transformComponent.x - rendererComponent.width / 2f, transformComponent.y - rendererComponent.height / 2f,
                rendererComponent.width / 2f, rendererComponent.height / 2f,
                rendererComponent.width, rendererComponent.height,
                rendererComponent.sclX, rendererComponent.sclY, transformComponent.rotation)
    }

}
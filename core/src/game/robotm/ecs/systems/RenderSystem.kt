package game.robotm.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import game.robotm.ecs.components.RendererComponent
import game.robotm.ecs.components.TransformComponent
import game.robotm.gamesys.GM


class RenderSystem(val batch: SpriteBatch, val frameBuffer: FrameBuffer, val camera: Camera) : IteratingSystem(Family.all(RendererComponent::class.java, TransformComponent::class.java).get()) {

    val renderM = ComponentMapper.getFor(RendererComponent::class.java)
    val transformM = ComponentMapper.getFor(TransformComponent::class.java)

    val shadowOffsetX: Float = -0.15f
    val shadowOffsetY: Float = -0.15f

    override fun update(deltaTime: Float) {

        frameBuffer.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        entities.sortedBy { entity ->
            val rendererComponent = renderM.get(entity)
            rendererComponent.renderOrder
        }.forEach { processEntity(it, deltaTime) }
        batch.end()
        frameBuffer.end()

        val shadowTexture = frameBuffer.colorBufferTexture
        batch.begin()
        // draw shadow
        batch.color = Color(0f, 0f, 0f, 0.2f)
        batch.draw(shadowTexture, -GM.SCREEN_WIDTH / 2f + shadowOffsetX, camera.position.y - GM.SCREEN_HEIGHT / 2f + shadowOffsetY, GM.SCREEN_WIDTH, GM.SCREEN_HEIGHT, 0, 0, shadowTexture.width, shadowTexture.height, false, true)
        batch.color = Color.WHITE
        // draw normal graphics
        batch.draw(shadowTexture, -GM.SCREEN_WIDTH / 2f, camera.position.y - GM.SCREEN_HEIGHT / 2f, GM.SCREEN_WIDTH, GM.SCREEN_HEIGHT, 0, 0, shadowTexture.width, shadowTexture.height, false, true)
        batch.end()
    }

    override fun processEntity(entity: Entity, delta: Float) {
        val transformComponent = transformM.get(entity)
        val rendererComponent = renderM.get(entity)

        val textureRegion = rendererComponent.textureRegion

        batch.draw(textureRegion,
                transformComponent.x - rendererComponent.width / 2f, transformComponent.y - rendererComponent.height / 2f,
                rendererComponent.originX, rendererComponent.originY,
                rendererComponent.width, rendererComponent.height,
                rendererComponent.sclX, rendererComponent.sclY, transformComponent.rotation)
    }

}
package game.robotm.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion


class RendererComponent(
        val textureRegion: TextureRegion,
        var width: Float, var height: Float,
        var sclX: Float = 1f, val sclY: Float = 1f
) : Component
package game.robotm

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.kotcrab.vis.ui.VisUI
import game.robotm.screens.PlayScreen


class RobotM : Game() {
    lateinit var batch: SpriteBatch

    override fun create() {
        VisUI.load()
        batch = SpriteBatch()

        setScreen(PlayScreen(this))
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        batch.dispose()
        VisUI.dispose()
    }
}


package game.robotm.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import game.robotm.ecs.components.PlayerComponent
import game.robotm.gamesys.GM
import game.robotm.screens.PlayScreen


class InfoBoard(val playScreen: PlayScreen) : Disposable {

    val viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    val stage = Stage(viewport, playScreen.batch)
    val screenTop = Gdx.graphics.height.toFloat()

    var barRedImage: Image

    init {
        val assetManager = playScreen.assetManager
        val textureAtlas = assetManager.get("img/gui.atlas", TextureAtlas::class.java)

        val panelPatch = NinePatch(textureAtlas.findRegion("Panel"), 10, 10, 10, 10)

        val panelImage = Image(panelPatch)
        panelImage.setPosition(0f, screenTop - 120f)
        panelImage.setSize(Gdx.graphics.width.toFloat(), 120f)

        val barBackPatch = NinePatch(textureAtlas.findRegion("BarBack"), 9, 9, 5, 5)
        val barRedPatch = NinePatch(textureAtlas.findRegion("BarRed"), 9, 9, 5, 5)
        val barGreenPatch = NinePatch(textureAtlas.findRegion("BarGreen"), 9, 9, 5, 5)
        val barBluePatch = NinePatch(textureAtlas.findRegion("BarBlue"), 9, 9, 5, 5)
        val barYellowPatch = NinePatch(textureAtlas.findRegion("BarYellow"), 9, 9, 5, 5)

        barRedImage = Image(barRedPatch)
        barRedImage.setPosition(12f, screenTop - 62f)
        barRedImage.setSize(100f, 18f)

        val barBackImage = Image(barBackPatch)
        barBackImage.setPosition(12f, screenTop - 62f)
        barBackImage.setSize(100f, 18f)

        stage.addActor(panelImage)
        stage.addActor(barBackImage)
        stage.addActor(barRedImage)
    }

    fun draw() {

        if (GM.player_hp <= 0f) {
            barRedImage.isVisible = false
        } else {
            barRedImage.isVisible = true
            barRedImage.width = 18f + 82f * (GM.player_hp / PlayerComponent.FULL_HP)
        }

        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

}


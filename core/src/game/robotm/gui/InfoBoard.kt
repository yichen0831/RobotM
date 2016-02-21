package game.robotm.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.FitViewport
import game.robotm.ecs.components.PlayerComponent
import game.robotm.gamesys.GM
import game.robotm.gamesys.ItemType
import game.robotm.screens.PlayScreen


class InfoBoard(val playScreen: PlayScreen) : Disposable {

    val viewport = FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    val stage = Stage(viewport)
    val screenTop = Gdx.graphics.height.toFloat()
    val screenRight = Gdx.graphics.width.toFloat()

    var hpBarImage: Image

    var fastFeetImage: Image
    var hardSkinImage: Image
    var quickHealingImage: Image
    var lowGravityImage: Image

    val distanceLabel: Label

    val stringBuilder = StringBuilder()

    init {
        val assetManager = playScreen.assetManager
        val guiTextureAtlas = assetManager.get("img/gui.atlas", TextureAtlas::class.java)

        val robotMTextureRegion = assetManager.get("img/actors.atlas", TextureAtlas::class.java).findRegion("RobotM")
        val robotMImage = Image(TextureRegion(robotMTextureRegion, 64 * 3, 0, 64, 64))
        robotMImage.setPosition(20f, screenTop - 48f)
        robotMImage.setSize(32f, 32f)

        val panelPatch = NinePatch(guiTextureAtlas.findRegion("Panel"), 10, 10, 10, 10)

        val panelImage = Image(panelPatch)
        panelImage.setPosition(0f, screenTop - 90f)
        panelImage.setSize(Gdx.graphics.width.toFloat(), 90f)

        val barBackPatch = NinePatch(guiTextureAtlas.findRegion("BarBack"), 9, 9, 5, 5)
        val barRedPatch = NinePatch(guiTextureAtlas.findRegion("BarRed"), 9, 9, 5, 5)
//        val barGreenPatch = NinePatch(guiTextureAtlas.findRegion("BarGreen"), 9, 9, 5, 5)
//        val barBluePatch = NinePatch(guiTextureAtlas.findRegion("BarBlue"), 9, 9, 5, 5)
//        val barYellowPatch = NinePatch(guiTextureAtlas.findRegion("BarYellow"), 9, 9, 5, 5)

        hpBarImage = Image(barRedPatch)
        hpBarImage.setPosition(60f, screenTop - 38f)
        hpBarImage.setSize(100f, 18f)

        val barBackImage = Image(barBackPatch)
        barBackImage.setPosition(60f, screenTop - 38f)
        barBackImage.setSize(100f, 18f)

        val itemsTextureRegion = assetManager.get("img/actors.atlas", TextureAtlas::class.java).findRegion("Items")
        fastFeetImage = Image(TextureRegion(itemsTextureRegion, 0, 0, 64, 64))
        fastFeetImage.setPosition(28f, screenTop - 80f)
        fastFeetImage.setSize(32f, 32f)
        fastFeetImage.setColor(1f, 1f, 1f, 0.1f)

        hardSkinImage = Image(TextureRegion(itemsTextureRegion, 64, 0, 64, 64))
        hardSkinImage.setPosition(64f, screenTop - 80f)
        hardSkinImage.setSize(32f, 32f)
        hardSkinImage.setColor(1f, 1f, 1f, 0.1f)

        quickHealingImage = Image(TextureRegion(itemsTextureRegion, 128, 0, 64, 64))
        quickHealingImage.setPosition(100f, screenTop - 80f)
        quickHealingImage.setSize(32f, 32f)
        quickHealingImage.setColor(1f, 1f, 1f, 0.1f)

        lowGravityImage = Image(TextureRegion(itemsTextureRegion, 192, 0, 64, 64))
        lowGravityImage.setPosition(136f, screenTop - 80f)
        lowGravityImage.setSize(32f, 32f)
        lowGravityImage.setColor(1f, 1f, 1f, 0.1f)

        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("font/kenvector_future.ttf"))
        val fontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParameter.size = 18
        val font16 = fontGenerator.generateFont(fontParameter)

        fontGenerator.dispose()

        val labelStyle = Label.LabelStyle(font16, Color.WHITE)
        distanceLabel = Label("Distance:", labelStyle)
        distanceLabel.setPosition(screenRight - 140f, screenTop - 40f)
        distanceLabel.setAlignment(Align.right)

        stage.addActor(panelImage)
        stage.addActor(robotMImage)
        stage.addActor(barBackImage)
        stage.addActor(hpBarImage)

        stage.addActor(fastFeetImage)
        stage.addActor(hardSkinImage)
        stage.addActor(lowGravityImage)
        stage.addActor(quickHealingImage)

        stage.addActor(distanceLabel)
    }

    fun draw() {

        hpBarImage.isVisible = GM.playerHp > 0
        hpBarImage.width = 18f + 82f * (GM.playerHp / PlayerComponent.FULL_HP)

        if (GM.playerPowerUpStatusMap[ItemType.FastFeet]!! <= 0f) {
            fastFeetImage.setColor(1.0f, 1.0f, 1.0f, 0.1f)
        }
        else {
            fastFeetImage.setColor(1.0f, 1.0f, 1.0f, 1f)
        }

        if (GM.playerPowerUpStatusMap[ItemType.HardSkin]!! <= 0f) {
            hardSkinImage.setColor(1.0f, 1.0f, 1.0f, 0.1f)
        }
        else {
            hardSkinImage.setColor(1.0f, 1.0f, 1.0f, 1f)
        }

        if (GM.playerPowerUpStatusMap[ItemType.QuickHealing]!! <= 0f) {
            quickHealingImage.setColor(1.0f, 1.0f, 1.0f, 0.1f)
        }
        else {
            quickHealingImage.setColor(1.0f, 1.0f, 1.0f, 1f)
        }

        if (GM.playerPowerUpStatusMap[ItemType.LowGravity]!! <= 0f) {
            lowGravityImage.setColor(1.0f, 1.0f, 1.0f, 0.1f)
        }
        else {
            lowGravityImage.setColor(1.0f, 1.0f, 1.0f, 1f)
        }

        GM.highestDistance = Math.max(GM.highestDistance, Math.abs(GM.cameraY.toInt()))

        stringBuilder.setLength(0)
        stringBuilder.append("Highest: %04d\nDistance: %04d".format(GM.highestDistance, Math.abs(GM.cameraY.toInt())))
        distanceLabel.setText(stringBuilder)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

}


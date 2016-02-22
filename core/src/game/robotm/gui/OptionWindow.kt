package game.robotm.gui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisSlider
import com.kotcrab.vis.ui.widget.VisWindow
import game.robotm.gamesys.GM


class OptionWindow(title: String, val assetManager: AssetManager) : VisWindow(title) {

    val sfxVolumeSlider: VisSlider
    val bgmVolumeSlider: VisSlider


    init {
        val textureAtlas = assetManager.get("img/gui.atlas", TextureAtlas::class.java)
        val patch = NinePatch(textureAtlas.findRegion("Panel"), 10, 10, 10, 10)

        val freeTypeFontGenerator = FreeTypeFontGenerator(Gdx.files.internal("font/kenvector_future.ttf"))
        val freeTypeFontParameter = FreeTypeFontGenerator.FreeTypeFontParameter()

        freeTypeFontParameter.size = 12
        val font12: BitmapFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter)

        freeTypeFontParameter.size = 16
        val font16: BitmapFont = freeTypeFontGenerator.generateFont(freeTypeFontParameter)

        freeTypeFontGenerator.dispose()


        style = WindowStyle(font16, Color.WHITE, NinePatchDrawable(patch))

        val labelStyle = Label.LabelStyle(font12, Color.WHITE)

        val sfxVolumeLabel = VisLabel("SFX Volume:", labelStyle)
        sfxVolumeSlider = VisSlider(0f, 1f, 0.1f, false)
        sfxVolumeSlider.value = GM.sfxVolume
        sfxVolumeSlider.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                GM.sfxVolume = (event.target as VisSlider).value
            }
        })

        val bgmVolumeLabel = VisLabel("BGM Volume:", labelStyle)
        bgmVolumeSlider = VisSlider(0f, 1f, 0.1f, false)
        bgmVolumeSlider.value = GM.bgmVolume
        bgmVolumeSlider.addListener(object: ChangeListener() {
            override fun changed(event: ChangeEvent, actor: Actor?) {
                GM.bgmVolume = (event.target as VisSlider).value
            }
        })

        padTop(32f)
        add(sfxVolumeLabel)
        row()
        add(sfxVolumeSlider)
        row()
        add(bgmVolumeLabel).padTop(20f)
        row()
        add(bgmVolumeSlider)
    }
}
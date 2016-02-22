package game.robotm.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import game.robotm.RobotM
import game.robotm.ecs.systems.*
import game.robotm.gamesys.GM
import game.robotm.gamesys.ObjBuilder
import game.robotm.gamesys.SoundPlayer
import game.robotm.gamesys.WorldContactListener
import game.robotm.gui.InfoBoard
import game.robotm.gui.OptionWindow

class PlayScreen(val mainGame: RobotM): ScreenAdapter() {

    val WIDTH = GM.SCREEN_WIDTH
    val HEIGHT = GM.SCREEN_HEIGHT

    val READY_COUNT_DOWN = 2f

    val batch = mainGame.batch
    lateinit var camera: OrthographicCamera
    lateinit var viewport: FitViewport

    var cameraSpeed = 3.6f
    var cameraCurrentSpeed = cameraSpeed
    var nextFloorsAndWallGeneratingY = 0f
    val generatingInterval = 40

    val assetManager = AssetManager()

    lateinit var world: World
    lateinit var engine: Engine

    lateinit var backgroundSprite: Sprite

    lateinit var stage: Stage
    lateinit var gameOverImage: Image
    lateinit var getReadyImage: Image

    lateinit var optionWindow: OptionWindow
    var showOptionWindow = false

    lateinit var infoBoard: InfoBoard

    var readyCountDown = READY_COUNT_DOWN

    var readySoundPlayed = false
    var goSoundPlayed = false
    var gameOverSoundPlayed = false

    lateinit var backgroundMusic: Music

    val box2DDebugRenderer = Box2DDebugRenderer()
    var showBox2DDebugRenderer = false

    override fun show() {

        assetManager.load("img/actors.atlas", TextureAtlas::class.java)
        assetManager.load("img/backgrounds/blue_grass.png", Texture::class.java)
        assetManager.load("img/textGameOver.png", Texture::class.java)
        assetManager.load("img/textGetReady.png", Texture::class.java)
        assetManager.load("img/gui.atlas", TextureAtlas::class.java)
        assetManager.load("sounds/ready.ogg", Sound::class.java)
        assetManager.load("sounds/go.ogg", Sound::class.java)
        assetManager.load("sounds/game_over.ogg", Sound::class.java)
        assetManager.load("sounds/damaged.ogg", Sound::class.java)
        assetManager.load("sounds/explode.ogg", Sound::class.java)
        assetManager.load("sounds/jump.ogg", Sound::class.java)
        assetManager.load("sounds/spring.ogg", Sound::class.java)
        assetManager.load("sounds/cant_jump.ogg", Sound::class.java)
        assetManager.load("sounds/engine.ogg", Sound::class.java)
        assetManager.load("sounds/power_up.ogg", Sound::class.java)
        assetManager.load("music/S31-Undercover Operative.ogg", Music::class.java)
        assetManager.finishLoading()

        SoundPlayer.load(assetManager)

        backgroundMusic = assetManager.get("music/S31-Undercover Operative.ogg", Music::class.java)

        camera = OrthographicCamera()
        viewport = FitViewport(WIDTH, HEIGHT, camera)

        backgroundSprite = Sprite(assetManager.get("img/backgrounds/blue_grass.png", Texture::class.java))
        backgroundSprite.setBounds(-HEIGHT / 2f, -HEIGHT / 2f, HEIGHT, HEIGHT)

        stage = Stage()
        gameOverImage = Image(assetManager.get("img/textGameOver.png", Texture::class.java))
        gameOverImage.setSize(250f, 50f)
        gameOverImage.setPosition((Gdx.graphics.width - gameOverImage.width) / 2f, (Gdx.graphics.height - gameOverImage.height) / 2f)
        gameOverImage.isVisible = GM.gameOver

        getReadyImage = Image(assetManager.get("img/textGetReady.png", Texture::class.java))
        getReadyImage.setSize(250f, 45.5f)
        getReadyImage.setPosition((Gdx.graphics.width - getReadyImage.width) / 2f, (Gdx.graphics.height - getReadyImage.height) / 2f)
        getReadyImage.isVisible = GM.getReady

        optionWindow = OptionWindow("Option", assetManager)
        optionWindow.setSize(240f, 200f)
        optionWindow.centerWindow()

        stage.addActor(gameOverImage)
        stage.addActor(getReadyImage)
        stage.addActor(optionWindow)

        Gdx.input.inputProcessor = stage

        world = World(Vector2(0f, -16f), true)
        world.setContactListener(WorldContactListener())
        engine = Engine()

        engine.addSystem(PlayerSystem())
        engine.addSystem(FollowCameraSystem(camera))
        engine.addSystem(InteractionSystem())
        engine.addSystem(PhysicsSystem())
        engine.addSystem(AnimationSystem())
        engine.addSystem(RenderSystem(batch))

        infoBoard = InfoBoard(this)

        resetGame()

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)

    }

    private fun resetGame() {
        val bodies = Array<Body>()
        world.getBodies(bodies)
        bodies.forEach { world.destroyBody(it) }
        engine.removeAllEntities()

        ObjBuilder.assetManager = assetManager
        ObjBuilder.world = world
        ObjBuilder.engine = engine

        ObjBuilder.createPlayer(0f, -3.45f)
        // start location
        ObjBuilder.createFloor(-1.5f, -4.5f, 4)
        // fill initial wall holes
        ObjBuilder.createWall(-MathUtils.floor(WIDTH / 2f).toFloat() + 0.5f, MathUtils.floor(WIDTH / 2f).toFloat() - 0.5f, 9.5f, 14)

        cameraSpeed = 3.6f
        camera.position.y = 0f
        nextFloorsAndWallGeneratingY = -4.5f

        generateFloorsAndWalls()

        ObjBuilder.generateRingSaws(-MathUtils.floor(WIDTH / 2f).toFloat() + 0.5f, 6.5f, 16)
        ObjBuilder.generateCeilings(-MathUtils.floor(WIDTH / 2f).toFloat() + 0.5f, 7.5f, 16)

        readyCountDown = READY_COUNT_DOWN

        GM.getReady = true
        GM.gameOver = false

        readySoundPlayed = false
        goSoundPlayed = false
        gameOverSoundPlayed = false
    }

    private fun generateFloorsAndWalls() {
        ObjBuilder.generateFloorsAndWalls(start = nextFloorsAndWallGeneratingY, height = generatingInterval)
        nextFloorsAndWallGeneratingY -= generatingInterval
    }

    private fun inputHandler() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebugRenderer = !showBox2DDebugRenderer
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (gameOverSoundPlayed) {
                resetGame()
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame()
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            showOptionWindow = !showOptionWindow
        }

    }

    fun update(delta: Float) {
        camera.position.y -= cameraCurrentSpeed * delta

        if (camera.position.y < nextFloorsAndWallGeneratingY + HEIGHT) {
            generateFloorsAndWalls()
        }

    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        inputHandler()

        if (GM.getReady && !showOptionWindow) {
            readyCountDown -= delta

            if (readyCountDown < READY_COUNT_DOWN - 0.5f) {
                if (!readySoundPlayed) {
                    SoundPlayer.play("ready")
                    readySoundPlayed = true
                }
            }

            if (readyCountDown < 0.5f) {
                if (!goSoundPlayed) {
                    SoundPlayer.play("go")
                    goSoundPlayed = true
                }
            }

            if (readyCountDown <= 0) {
                GM.getReady = false
                backgroundMusic.isLooping = true
                backgroundMusic.volume = 0.5f
                backgroundMusic.play()
            }
        }

        if (!GM.gameOver && !GM.getReady && !showOptionWindow) {
            update(delta)
            world.step(Math.min(delta, 1 / 60f), 8, 3)
        }

        cameraCurrentSpeed = if (GM.playerIsDead) 0f else cameraSpeed

        camera.update()
        GM.cameraY = camera.position.y
        backgroundSprite.y = camera.position.y - HEIGHT / 2f

        batch.projectionMatrix = camera.combined
        batch.begin()
        backgroundSprite.draw(batch)
        batch.end()

        if (showOptionWindow) {
            engine.systems.filterIsInstance<RenderSystem>().forEach {
                renderSystem ->
                if (renderSystem.checkProcessing()) {
                    renderSystem.update(delta)
                }
            }
        } else {
            engine.update(delta)
        }

        if (showBox2DDebugRenderer) {
            box2DDebugRenderer.render(world, camera.combined)
        }

        infoBoard.draw()

        optionWindow.isVisible = showOptionWindow

        getReadyImage.isVisible = if (showOptionWindow) false else GM.getReady
        gameOverImage.isVisible = if (showOptionWindow) false else GM.gameOver

        stage.draw()

        backgroundMusic.volume = GM.bgmVolume
        if (GM.gameOver) {
            backgroundMusic.stop()
            if (!gameOverSoundPlayed) {
                SoundPlayer.play("game_over")
                gameOverSoundPlayed = true
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
        viewport.update(width, height)
    }

    override fun dispose() {
        infoBoard.dispose()
        stage.dispose()
        world.dispose()
        box2DDebugRenderer.dispose()
        assetManager.dispose()
    }

}
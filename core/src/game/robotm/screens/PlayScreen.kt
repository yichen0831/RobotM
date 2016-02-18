package game.robotm.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
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
import game.robotm.ecs.systems.AnimationSystem
import game.robotm.ecs.systems.PhysicsSystem
import game.robotm.ecs.systems.PlayerSystem
import game.robotm.ecs.systems.RenderSystem
import game.robotm.gamesys.GM
import game.robotm.gamesys.ObjBuilder

class PlayScreen(val mainGame: RobotM): ScreenAdapter() {

    val WIDTH = GM.SCREEN_WIDTH
    val HEIGHT = GM.SCREEN_HEIGHT

    val batch = mainGame.batch
    lateinit var camera: OrthographicCamera
    lateinit var viewport: FitViewport

    var cameraSpeed = 3.6f
    var nextFloorsAndWallGeneratingY = 0f
    val generatingInterval = 40

    val assetManager = AssetManager()

    lateinit var world: World
    lateinit var engine: Engine

    lateinit var backgroundSprite: Sprite

    lateinit var stage: Stage
    lateinit var gameOverImage: Image
    lateinit var getReadyImage: Image

    var readyCountDown = 3f

    val box2DDebugRenderer = Box2DDebugRenderer()
    var showBox2DDebugRenderer = true

    override fun show() {

        assetManager.load("img/actors.atlas", TextureAtlas::class.java)
        assetManager.load("img/backgrounds/blue_grass.png", Texture::class.java)
        assetManager.load("img/textGameOver.png", Texture::class.java)
        assetManager.load("img/textGetReady.png", Texture::class.java)
        assetManager.finishLoading()

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

        stage.addActor(gameOverImage)
        stage.addActor(getReadyImage)

        world = World(Vector2(0f, -16f), true)
        engine = Engine()

        engine.addSystem(PlayerSystem())
        engine.addSystem(PhysicsSystem())
        engine.addSystem(AnimationSystem())
        engine.addSystem(RenderSystem(batch))

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

        camera.position.y = 0f
        nextFloorsAndWallGeneratingY = -4.5f

        generateFloorsAndWalls()

        readyCountDown = 3f

        GM.getReady = true
        GM.gameOver = false
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
            if (GM.gameOver) {
                resetGame()
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame()
        }

    }

    fun update(delta: Float) {
        camera.position.y -= cameraSpeed * delta

        if (camera.position.y < nextFloorsAndWallGeneratingY + HEIGHT) {
            generateFloorsAndWalls()
        }

    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        inputHandler()

        if (GM.getReady) {
            readyCountDown -= delta

            if (readyCountDown <= 0) {
                GM.getReady = false
            }
        }

        if (!GM.gameOver && !GM.getReady) {
            update(delta)
            world.step(Math.min(delta, 1 / 60f), 8, 3)
        }

        camera.update()
        GM.cameraY = camera.position.y
        backgroundSprite.y = camera.position.y - HEIGHT / 2f

        batch.projectionMatrix = camera.combined
        batch.begin()
        backgroundSprite.draw(batch)
        batch.end()

        engine.update(delta)

        if (showBox2DDebugRenderer) {
            box2DDebugRenderer.render(world, camera.combined)
        }

        getReadyImage.isVisible = GM.getReady
        gameOverImage.isVisible = GM.gameOver
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
        world.dispose()
        box2DDebugRenderer.dispose()
        assetManager.dispose()
    }

}
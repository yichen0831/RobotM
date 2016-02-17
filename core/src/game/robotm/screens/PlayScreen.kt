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

    var cameraSpeed = 2.4f
    var nextFloorsAndWallGeneratingY = 0f
    val generatingInterval = 40

    val assetManager = AssetManager()

    lateinit var world: World
    lateinit var engine: Engine

    lateinit var backgroundSprite: Sprite

    val box2DDebugRenderer = Box2DDebugRenderer()
    var showBox2DDebugRenderer = true

    override fun show() {

        assetManager.load("img/actors.atlas", TextureAtlas::class.java)
        assetManager.load("img/backgrounds/blue_grass.png", Texture::class.java)
        assetManager.finishLoading()

        camera = OrthographicCamera()
        viewport = FitViewport(WIDTH, HEIGHT, camera)

        backgroundSprite = Sprite(assetManager.get("img/backgrounds/blue_grass.png", Texture::class.java))
        backgroundSprite.setBounds(-HEIGHT / 2f, -HEIGHT / 2f, HEIGHT, HEIGHT)

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

        ObjBuilder.createPlayer(0f, 1.5f)
        // start location
        ObjBuilder.createFloor(-1.5f, 0.5f, 4)

        camera.position.y = 0f
        nextFloorsAndWallGeneratingY = 0f

        GM.gameOver = false
    }

    private fun generateFloorsAndWalls() {
        ObjBuilder.generateFloors(start = nextFloorsAndWallGeneratingY, height = generatingInterval)
        ObjBuilder.createWall(-MathUtils.floor(WIDTH / 2f).toFloat() + 0.5f, MathUtils.floor(WIDTH / 2f).toFloat() - 0.5f, nextFloorsAndWallGeneratingY + 9.5f, generatingInterval)

        nextFloorsAndWallGeneratingY -= generatingInterval
    }

    private fun inputHandler() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebugRenderer = !showBox2DDebugRenderer
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame()
        }

    }

    fun update(delta: Float) {
        camera.position.y -= cameraSpeed * delta
        GM.cameraY = camera.position.y

        if (camera.position.y < nextFloorsAndWallGeneratingY + HEIGHT) {
            generateFloorsAndWalls()
        }
        camera.update()

        backgroundSprite.y = camera.position.y - HEIGHT / 2f
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        inputHandler()

        if (!GM.gameOver) {
            update(delta)
            world.step(Math.min(delta, 1 / 60f), 8, 3)
        }

        batch.projectionMatrix = camera.combined
        batch.begin()
        backgroundSprite.draw(batch)
        batch.end()

        engine.update(delta)

        if (showBox2DDebugRenderer) {
            box2DDebugRenderer.render(world, camera.combined)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    override fun dispose() {
        world.dispose()
        box2DDebugRenderer.dispose()
        assetManager.dispose()
    }

}
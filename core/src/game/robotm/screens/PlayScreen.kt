package game.robotm.screens

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.viewport.FitViewport
import game.robotm.RobotM
import game.robotm.ecs.systems.AnimationSystem
import game.robotm.ecs.systems.PhysicsSystem
import game.robotm.ecs.systems.PlayerSystem
import game.robotm.ecs.systems.RenderSystem
import game.robotm.gamesys.ObjBuilder

class PlayScreen(val mainGame: RobotM): ScreenAdapter() {

    val WIDTH = 15f
    val HEIGHT = 20f

    val batch = mainGame.batch
    lateinit var camera: OrthographicCamera
    lateinit var viewport: FitViewport

    val assetManager = AssetManager()

    lateinit var world: World
    lateinit var engine: Engine

    val box2DDebugRenderer = Box2DDebugRenderer()
    var showBox2DDebugRenderer = true

    override fun show() {

        assetManager.load("img/actors.atlas", TextureAtlas::class.java)
        assetManager.finishLoading()

        camera = OrthographicCamera()
        viewport = FitViewport(WIDTH, HEIGHT, camera)

        world = World(Vector2(0f, -20f), true)
        engine = Engine()

        engine.addSystem(PlayerSystem())
        engine.addSystem(PhysicsSystem())
        engine.addSystem(AnimationSystem())
        engine.addSystem(RenderSystem(batch))

        ObjBuilder.assetManager = assetManager
        ObjBuilder.world = world
        ObjBuilder.engine = engine

        /* tmp code */
        ObjBuilder.createPlayer(0f, 0f)
        ObjBuilder.createFloor(0f, 0f, 4)
        ObjBuilder.createFloor(0f, 0f, 4)
        ObjBuilder.createFloor(0f, -4f, 4)
        ObjBuilder.createFloor(0f, -8f, 4)


        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)

    }

    private fun inputHandler() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showBox2DDebugRenderer = !showBox2DDebugRenderer
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        inputHandler()

        camera.update()
        batch.projectionMatrix = camera.combined

        world.step(Math.min(delta, 1 / 60f), 8, 3)

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
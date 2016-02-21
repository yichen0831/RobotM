package game.robotm.gamesys

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import game.robotm.ecs.components.*
import java.util.*


object ObjBuilder {
    /* Render Order
     * Floor: 2 (first / bottom)
     * Player: 3
     * Spike, Spring, Item: 4
     * Wall: 5
     * Ceiling & Ring-saw: 6 (last / top)
     * Player explosion pieces: 7, 8, 9
     */

    var assetManager: AssetManager? = null
    var world: World? = null
    var engine: Engine? = null

    val tmpVec1 = Vector2()
    val tmpVec2 = Vector2()

    fun createPlayer(x: Float, y: Float) {

        val scale = GM.PLAYER_SCALE

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(x, y)
        bodyDef.fixedRotation = true

        val body = world!!.createBody(bodyDef)

        val boxShape = PolygonShape()
        boxShape.setAsBox(0.3f * scale, 0.3f * scale)
        val fixtureDef = FixtureDef()
        fixtureDef.shape = boxShape
        fixtureDef.density = 0.5f
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_PLAYER.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_PLAYER.toShort()
        fixtureDef.friction = 0f

        body.createFixture(fixtureDef)
        boxShape.dispose()

        val edgeShape = EdgeShape()
        // sides
        edgeShape.set(tmpVec1.set(-0.45f, 0.3f).scl(scale), tmpVec2.set(-0.45f, -0.36f).scl(scale))
        fixtureDef.shape = edgeShape
        fixtureDef.friction = 0f
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_PLAYER.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_PLAYER.toShort()
        body.createFixture(fixtureDef)

        edgeShape.set(tmpVec1.set(0.45f, 0.3f).scl(scale), tmpVec2.set(0.45f, -0.36f).scl(scale))
        fixtureDef.shape = edgeShape
        body.createFixture(fixtureDef)

        // feet
        edgeShape.set(tmpVec1.set(-0.45f, -0.375f).scl(scale), tmpVec2.set(0.45f, -0.375f).scl(scale))
        fixtureDef.shape = edgeShape
        fixtureDef.friction = 0.8f
        body.createFixture(fixtureDef)
        edgeShape.dispose()

        val textureRegion = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java).findRegion("RobotM")

        val animations = HashMap<String, Animation>()
        var anim: Animation

        var keyFrames = Array<TextureRegion>()

        // idle animation
        keyFrames.add(TextureRegion(textureRegion, 64 * 3, 0, 64, 48))
        anim = Animation(0.1f, keyFrames, Animation.PlayMode.LOOP)
        animations.put("idle", anim)

        keyFrames.clear()

        // move animation
        for (i in 0..1) {
            keyFrames.add(TextureRegion(textureRegion, 64 * (3 + i), 0, 64, 48))
        }
        anim = Animation(0.1f, keyFrames, Animation.PlayMode.LOOP)
        animations.put("move", anim)

        keyFrames.clear()

        // fall animation
        keyFrames.add(TextureRegion(textureRegion, 64 * 6, 0, 64, 48))
        anim = Animation(0.1f, keyFrames, Animation.PlayMode.LOOP)
        animations.put("fall", anim)

        keyFrames.clear()

        // idle animation (damaged)
        keyFrames.add(TextureRegion(textureRegion, 64 * 3, 0, 64, 48))
        keyFrames.add(TextureRegion(textureRegion, 64 * 11, 0, 64, 48))
        anim = Animation(0.05f, keyFrames, Animation.PlayMode.LOOP)
        animations.put("idle_damaged", anim)

        // move animation (damaged)
        keyFrames.add(TextureRegion(textureRegion, 64 * 3, 0, 64, 48))
        keyFrames.add(TextureRegion(textureRegion, 64 * 11, 0, 64, 48))
        keyFrames.add(TextureRegion(textureRegion, 64 * 4, 0, 64, 48))
        keyFrames.add(TextureRegion(textureRegion, 64 * 12, 0, 64, 48))

        anim = Animation(0.05f, keyFrames, Animation.PlayMode.LOOP)
        animations.put("move_damaged", anim)

        keyFrames.clear()

        // fall animation (damaged)
        keyFrames.add(TextureRegion(textureRegion, 64 * 6, 0, 64, 48))
        keyFrames.add(TextureRegion(textureRegion, 64 * 13, 0, 64, 48))
        anim = Animation(0.05f, keyFrames, Animation.PlayMode.LOOP)
        animations.put("fall_damaged", anim)

        val entity = Entity()
        entity.add(PlayerComponent())
        entity.add(PhysicsComponent(body))
        entity.add(RendererComponent(TextureRegion(textureRegion, 64 * 3, 0, 64, 48), 64 / GM.PPM * scale, 48 / GM.PPM * scale, renderOrder = 3))
        entity.add(AnimationComponent(animations, "idle"))
        entity.add(TransformComponent(x, y))

        engine!!.addEntity(entity)

        body.userData = entity
    }

    fun createPlayerExplosionEffect(x: Float, y: Float) {
        val scale = GM.PLAYER_SCALE

        // main body
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(x, y)

        val mainBody = world!!.createBody(bodyDef)

        val polygonShape = PolygonShape()
        polygonShape.setAsBox(0.45f * scale, 0.375f * scale)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = polygonShape
        fixtureDef.density = 0.5f
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_NOTHING.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_NOTHING.toShort()

        mainBody.createFixture(fixtureDef)

        // left wheels
        bodyDef.position.set(x + 0.15f * scale, y - 0.2f * scale)

        val leftWheelsBody = world!!.createBody(bodyDef)

        polygonShape.setAsBox(0.3f * scale, 0.2f * scale)
        fixtureDef.shape = polygonShape
        leftWheelsBody.createFixture(fixtureDef)

        // right wheels
        bodyDef.position.set(x - 0.15f * scale, y - 0.2f * scale)
        val rightWheelsBody = world!!.createBody(bodyDef)
        rightWheelsBody.createFixture(fixtureDef)

        polygonShape.dispose()

        mainBody.applyLinearImpulse(tmpVec1.set(MathUtils.random(-2f, 2f), MathUtils.random(2f, 6f)).scl(mainBody.mass), mainBody.worldCenter, true)
        mainBody.applyAngularImpulse(MathUtils.random(-MathUtils.PI / 10f, MathUtils.PI / 10f), true)

        leftWheelsBody.applyLinearImpulse(tmpVec1.set(MathUtils.random(-4f, 4f), MathUtils.random(2f, 6f)).scl(leftWheelsBody.mass), leftWheelsBody.worldCenter, true)
        leftWheelsBody.applyAngularImpulse(MathUtils.random(-MathUtils.PI / 10f, MathUtils.PI / 10f), true)

        rightWheelsBody.applyLinearImpulse(tmpVec1.set(MathUtils.random(-4f, 4f), MathUtils.random(2f, 6f)).scl(rightWheelsBody.mass), rightWheelsBody.worldCenter, true)
        rightWheelsBody.applyAngularImpulse(MathUtils.random(-MathUtils.PI / 10f, MathUtils.PI / 10f), true)


        val textureRegion = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java).findRegion("RobotM")

        val mainBodyEntity = Entity()
        mainBodyEntity.add(TransformComponent(mainBody.position.x, mainBody.position.y))
        mainBodyEntity.add(PhysicsComponent(mainBody))
        mainBodyEntity.add(RendererComponent(TextureRegion(textureRegion, 64 * 2, 0, 64, 48), 1f * scale, 0.75f * scale, renderOrder = 8))

        val leftWheelsEntity = Entity()
        leftWheelsEntity.add(TransformComponent(leftWheelsBody.position.x, leftWheelsBody.position.y))
        leftWheelsEntity.add(PhysicsComponent(leftWheelsBody))
        leftWheelsEntity.add(RendererComponent(TextureRegion(textureRegion, 64 * 7, 0, 64, 48), 1f * scale, 0.75f * scale, renderOrder = 7))

        val rightWheelsEntity = Entity()
        rightWheelsEntity.add(TransformComponent(rightWheelsBody.position.x, rightWheelsBody.position.y))
        rightWheelsEntity.add(PhysicsComponent(rightWheelsBody))
        rightWheelsEntity.add(RendererComponent(TextureRegion(textureRegion, 64 * 9, 0, 64, 48), 1f * scale, 0.75f * scale, renderOrder = 9))

        engine!!.addEntity(mainBodyEntity)
        engine!!.addEntity(leftWheelsEntity)
        engine!!.addEntity(rightWheelsEntity)
    }

    private fun createWallBody(x: Float, y: Float): Body {

        val bodyDef = BodyDef()
        bodyDef.position.set(x, y)
        bodyDef.type = BodyDef.BodyType.StaticBody

        val body = world!!.createBody(bodyDef)

        val shape = PolygonShape()
        shape.setAsBox(0.5f, 0.5f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = shape
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_WALL.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_WALL.toShort()
        fixtureDef.friction = 0f

        body.createFixture(fixtureDef)
        shape.dispose()

        return body
    }

    fun createWall(left: Float, right: Float, top: Float, height: Int, type: String = "Grass") {

        val textureAtlas = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java)
        val textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 5, 0, 64, 64)

        var body: Body
        var entity: Entity
        for (i in 0..height - 1) {
            body = createWallBody(left, top - i)
            entity = Entity()
            entity.add(TransformComponent(left, top - i))
            entity.add(PhysicsComponent(body))
            entity.add(RendererComponent(textureRegion, 1f, 1f))
            body.userData = entity
            engine!!.addEntity(entity)

            body = createWallBody(right, top - i)
            entity = Entity()
            entity.add(TransformComponent(right, top - i))
            entity.add(PhysicsComponent(body))
            entity.add(RendererComponent(textureRegion, 1f, 1f, renderOrder = 5))
            body.userData = entity
            engine!!.addEntity(entity)

        }
    }

    fun createFloor(x: Float, y: Float, width: Int, type: String = "Grass") {
        val textureAtlas = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java)

        for (i in 0..width - 1) {

            val bodyDef = BodyDef()
            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set(x + i, y)

            val body = world!!.createBody(bodyDef)

            val shape = PolygonShape()
            shape.setAsBox(0.5f, 0.5f)

            val fixtureDef = FixtureDef()
            fixtureDef.shape = shape

            when (type) {
                "Purple" -> {
                    fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_FLOOR_UNJUMPABLE.toShort()
                    fixtureDef.filter.maskBits = GM.MAST_BITS_FLOOR_UNJUMPABLE.toShort()
                }
                else -> {
                    fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_FLOOR.toShort()
                    fixtureDef.filter.maskBits = GM.MASK_BITS_FLOOR.toShort()
                }
            }

            when (type) {
                "Grass" -> fixtureDef.friction = 0.8f
                "Sand" -> fixtureDef.friction = 1f
                "Choco" -> fixtureDef.friction = 0.01f
                "Purple" -> fixtureDef.friction = 0.5f
                else -> fixtureDef.friction = 0.8f
            }

            body.createFixture(fixtureDef)
            shape.dispose()

            val textureRegion: TextureRegion

            if (width == 1) {
                textureRegion = TextureRegion(textureAtlas.findRegion(type), 0, 0, 64, 64)
            } else {
                when (i) {
                    0 -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 1, 0, 64, 64)
                    width - 1 -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 3, 0, 64, 64)
                    else -> textureRegion = TextureRegion(textureAtlas.findRegion(type), 64 * 2, 0, 64, 64)
                }
            }

            val entity = Entity()
            entity.add(TransformComponent(x + i, y))
            entity.add(PhysicsComponent(body))
            entity.add(RendererComponent(textureRegion, 1f, 1f, renderOrder = 2))

            engine!!.addEntity(entity)

            body.userData = entity
        }
    }

    private fun shouldMakeSpike(y: Float): Boolean {
        if (y > -150f) {
            return MathUtils.randomBoolean(0.15f)
        }

        if (y > -300) {
            return MathUtils.randomBoolean(0.15f)
        }

        if (y > -450) {
            return MathUtils.randomBoolean(0.20f)
        }

        if (y > -600) {
            return MathUtils.randomBoolean(0.20f)
        }

        if (y > -750) {
            return MathUtils.randomBoolean(0.25f)
        }

        if (y > -900) {
            return MathUtils.randomBoolean(0.25f)
        }
        if (y > -1000) {
            return MathUtils.randomBoolean(0.3f)
        }
        return MathUtils.randomBoolean(0.4f)
    }

    private fun shouldMakeSpring(y: Float): Boolean {
        if (y > -150f) {
            return MathUtils.randomBoolean(0.1f)
        }

        if (y > -300) {
            return MathUtils.randomBoolean(0.15f)
        }

        if (y > -450) {
            return MathUtils.randomBoolean(0.15f)
        }

        if (y > -600) {
            return MathUtils.randomBoolean(0.20f)
        }

        if (y > -750) {
            return MathUtils.randomBoolean(0.25f)
        }

        if (y > -900) {
            return MathUtils.randomBoolean(0.25f)
        }

        if (y > -1000) {
            return MathUtils.randomBoolean(0.3f)
        }
        return MathUtils.randomBoolean(0.4f)
    }

    fun generateFloors(start: Float, height: Int, leftBound: Float, rightBound: Float, type: String = "Grass") {

        var gap: Int = MathUtils.random(4, 6)
        var length: Int = MathUtils.random(4, 6)

        var x: Float = MathUtils.random(leftBound.toInt() + 1, rightBound.toInt() - length - 2) + 0.5f
        var y: Float = MathUtils.floor(start) - gap + 0.5f

        while (y >= start - height) {

            if (gap <= 5 || MathUtils.random() > 0.05f) {

                if (length >= 6 && MathUtils.randomBoolean(0.6f)) {

                    length = MathUtils.random(2, 3)
                    x = MathUtils.random(-GM.SCREEN_WIDTH.toInt() / 2 + 1, -length - 2) + 0.5f
                    createFloor(x, y, length, type)

                    if (shouldMakeSpike(y)) {
                        generateSpikes(x, y + 1, length)
                    }
                    else if (shouldMakeSpring(y)) {
                        generateSprings(x, y + 1, length)
                    } else if (MathUtils.randomBoolean(0.1f)) {
                        generateRandomPowerUpItem(x + MathUtils.random(length - 1), y + 1.5f)
                    }

                    length = MathUtils.random(2, 3)
                    x = MathUtils.random(0, GM.SCREEN_WIDTH.toInt() / 2 - length - 2) + 0.5f
                    createFloor(x, y, length, type)

                    if (shouldMakeSpike(y)) {
                        generateSpikes(x, y + 1, length)
                    } else if (shouldMakeSpring(y)) {
                        generateSprings(x, y + 1, length)
                    } else if (MathUtils.randomBoolean(0.1f)) {
                        generateRandomPowerUpItem(x + MathUtils.random(length - 1), y + 1.5f)
                    }

                } else {
                    createFloor(x, y, length, type)
                    if (shouldMakeSpike(y)) {
                        generateSpikes(x, y + 1, length)
                    } else if (shouldMakeSpring(y)) {
                        generateSprings(x, y + 1, length)
                    } else if (MathUtils.randomBoolean(0.1f)) {
                        generateRandomPowerUpItem(x + MathUtils.random(length - 1), y + 1.5f)
                    }
                }
            }

            x = MathUtils.random(-GM.SCREEN_WIDTH.toInt() / 2 + 1, GM.SCREEN_WIDTH.toInt() / 2 - length - 2) + 0.5f
            y -= gap

            gap = MathUtils.random(4, 6)
            length = MathUtils.random(4, 6)
        }

    }

    fun generateFloorsAndWalls(start: Float, height: Int, left: Float = -GM.SCREEN_WIDTH / 2f + 0.5f, right: Float = GM.SCREEN_WIDTH / 2f - 0.5f) {
        val types = arrayOf("Grass", "Sand", "Choco", "Purple")

        var floorType: String
        var wallType: String

        if (start > -150f) {
            floorType = types[0]
            wallType = types[0]
        } else if (start > -300f) {
            floorType = types[1]
            wallType = types[1]
        } else if (start > -450f) {
            floorType = types[2]
            wallType = types[2]
        } else if (start > -600f) {
            floorType = types[3]
            wallType = types[3]
        } else {
            // random floor type
            floorType = types[MathUtils.random(0, types.size - 1)]
            wallType = "Purple"
        }

        generateFloors(start, height, left, right, floorType)
        createWall(left, right, start, height, wallType)

    }

    fun createRingSaw(x: Float, y: Float) {

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.KinematicBody
        bodyDef.position.set(x, y)

        val body = world!!.createBody(bodyDef)

        val polygonShape = PolygonShape()
        polygonShape.setAsBox(0.5f, 0.25f, tmpVec1.set(0f, 0.25f), 0f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = polygonShape
        fixtureDef.isSensor = true
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_LETHAL.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_LETHAL.toShort()

        body.createFixture(fixtureDef)
        polygonShape.dispose()


        val textureRegion = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java).findRegion("Spin")

        val keyFrames = Array<TextureRegion>()
        keyFrames.add(TextureRegion(textureRegion, 0, 0, 64, 64))
        keyFrames.add(TextureRegion(textureRegion, 64, 0, 64, 64))

        val animation = Animation(0.1f, keyFrames, Animation.PlayMode.LOOP)

        val anims = HashMap<String, Animation>()
        anims.put("sawing", animation)

        val entity = Entity()
        entity.add(TransformComponent(x, y))
        entity.add(PhysicsComponent(body))
        entity.add(RendererComponent(TextureRegion(textureRegion, 0, 0, 64, 64), 1f, 1f, originX = 0.5f, originY = 0.5f, renderOrder = 6))
        entity.add(AnimationComponent(anims, "sawing"))
        entity.add(FollowCameraComponent(x, y))

        body.userData = entity
        engine!!.addEntity(entity)

    }

    fun generateRingSaws(x: Float, y: Float, length: Int) {

        for (i in 0..length - 1) {
            createRingSaw(x + i, y)
        }
    }

    fun createCeiling(x: Float, y: Float) {

        val bodyDef = BodyDef()
        bodyDef.position.set(x, y)
        bodyDef.type = BodyDef.BodyType.KinematicBody

        val body = world!!.createBody(bodyDef)

        val polygonShape = PolygonShape()
        polygonShape.setAsBox(0.5f, 0.5f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = polygonShape
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_CEILING.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_CEILING.toShort()

        body.createFixture(fixtureDef)
        polygonShape.dispose()

        val textureRegion = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java).findRegion("Purple")

        val entity = Entity()
        entity.add(TransformComponent(x, y))
        entity.add(PhysicsComponent(body))
        entity.add(FollowCameraComponent(x, y))
        entity.add(RendererComponent(TextureRegion(textureRegion, 64 * 5, 0, 64, 64), 1f, 1f, renderOrder = 6))

        engine!!.addEntity(entity)
        body.userData = entity
    }

    fun generateCeilings(x: Float, y: Float, length: Int) {
        for (i in 0..length - 1) {
            createCeiling(x + i, y)
        }
    }

    fun createSpike(x: Float, y: Float) {

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.position.set(x, y)

        val body = world!!.createBody(bodyDef)

        val polygonShape = PolygonShape()
        polygonShape.setAsBox(0.5f, 0.25f, tmpVec1.set(0f, -0.25f), 0f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = polygonShape
        fixtureDef.isSensor = true
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_LETHAL.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_LETHAL.toShort()

        body.createFixture(fixtureDef)
        polygonShape.dispose()

        val textureRegion = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java).findRegion("Spikes")

        val entity = Entity()
        entity.add(TransformComponent(x, y))
        entity.add(PhysicsComponent(body))
        entity.add(RendererComponent(textureRegion, 1.0f, 1.0f, renderOrder = 4))

        engine!!.addEntity(entity)

        body.userData = entity
    }

    fun generateSpikes(x: Float, y: Float, length: Int) {
        for (i in 0..length - 1) {
            createSpike(x + i, y)
        }
    }

    fun createSpring(x: Float, y: Float) {
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.KinematicBody
        bodyDef.position.set(x, y)

        val body = world!!.createBody(bodyDef)

        val polygonShape = PolygonShape()
        polygonShape.setAsBox(0.5f, 0.25f, tmpVec1.set(0f, -0.25f), 0f)

        val fixtureDef = FixtureDef()
        fixtureDef.shape = polygonShape
        fixtureDef.isSensor = true
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_SPRING.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_SPRING.toShort()

        body.createFixture(fixtureDef)
        polygonShape.dispose()

        val textureRegion = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java).findRegion("Spring")

        var animation: Animation
        val anims = HashMap<String, Animation>()

        val keyFrames = Array<TextureRegion>()
        keyFrames.add(TextureRegion(textureRegion, 0, 0, 64, 64))
        animation = Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL)
        anims.put("normal", animation)

        keyFrames.clear()
        keyFrames.add(TextureRegion(textureRegion, 64, 0, 64, 64))
        animation = Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL)
        anims.put("hit", animation)

        val entity = Entity()
        entity.add(TransformComponent(x, y))
        entity.add(InteractionComponent(InteractionType.SPRING))
        entity.add(PhysicsComponent(body))
        entity.add(AnimationComponent(anims, "normal"))
        entity.add(RendererComponent(TextureRegion(textureRegion, 0, 0, 64, 64), 1f, 1f, renderOrder = 4))

        engine!!.addEntity(entity)
        body.userData = entity
    }

    fun generateSprings(x: Float, y: Float, length: Int) {
        for (i in 0..length - 1) {
            createSpring(x + i, y)
        }
    }

    fun createPowerUpItem(x: Float, y: Float, type: ItemType) {

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.KinematicBody
        bodyDef.position.set(x, y)

        val body = world!!.createBody(bodyDef)

        val circleShape = CircleShape()
        circleShape.radius = 0.4f

        val fixtureDef = FixtureDef()
        fixtureDef.shape = circleShape
        fixtureDef.isSensor = true
        fixtureDef.filter.categoryBits = GM.CATEGORY_BITS_ITEM.toShort()
        fixtureDef.filter.maskBits = GM.MASK_BITS_ITEM.toShort()

        body.createFixture(fixtureDef)
        circleShape.dispose()

        val textureRegion = assetManager!!.get("img/actors.atlas", TextureAtlas::class.java).findRegion("Items")

        val itemTextureRegion: TextureRegion

        when (type) {
            ItemType.FastFeet -> itemTextureRegion = TextureRegion(textureRegion, 0, 0, 64, 64)
            ItemType.HardSkin -> itemTextureRegion = TextureRegion(textureRegion, 64, 0, 64, 64)
            ItemType.QuickHealing -> itemTextureRegion = TextureRegion(textureRegion, 128, 0, 64, 64)
            ItemType.LowGravity -> itemTextureRegion = TextureRegion(textureRegion, 192, 0, 64, 64)
            else -> itemTextureRegion = TextureRegion(textureRegion, 0, 0, 64, 64)
        }

        val anims = HashMap<String, Animation>()
        var animation: Animation
        val keyFrames = Array<TextureRegion>()

        keyFrames.add(itemTextureRegion)
        animation = Animation(0.1f, keyFrames, Animation.PlayMode.LOOP)
        anims.put("normal", animation)

        val entity = Entity()
        entity.add(TransformComponent(x, y))
        entity.add(InteractionComponent(InteractionType.ITEM, type.name))
        entity.add(PhysicsComponent(body))
        entity.add(RendererComponent(itemTextureRegion, 1f, 1f, renderOrder = 4))
        entity.add(AnimationComponent(anims, "normal"))

        engine!!.addEntity(entity)
        body.userData = entity
    }

    fun generateRandomPowerUpItem(x: Float, y: Float) {
        createPowerUpItem(x, y, ItemType.randomType())
    }
}
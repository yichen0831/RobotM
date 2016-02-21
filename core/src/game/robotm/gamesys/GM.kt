package game.robotm.gamesys


object GM {

    val PPM = 64f

    val SCREEN_WIDTH = 16f
    val SCREEN_HEIGHT = 20f

    val PLAYER_SCALE = 1f

    val CATEGORY_BITS_NOTHING: Int = 0
    val CATEGORY_BITS_WALL: Int = 1 shl 0
    val CATEGORY_BITS_FLOOR: Int = 1 shl 1
    val CATEGORY_BITS_FLOOR_UNJUMPABLE: Int = 1 shl 2
    val CATEGORY_BITS_PLAYER: Int = 1 shl 3
    val CATEGORY_BITS_LETHAL: Int = 1 shl 4
    val CATEGORY_BITS_CEILING: Int = 1 shl 5
    val CATEGORY_BITS_SPRING: Int = 1 shl 6
    val CATEGORY_BITS_ITEM: Int = 1 shl 7


    val MASK_BITS_NOTHING: Int = 0
    val MASK_BITS_WALL: Int = CATEGORY_BITS_PLAYER
    val MASK_BITS_FLOOR: Int = CATEGORY_BITS_PLAYER
    val MAST_BITS_FLOOR_UNJUMPABLE: Int = CATEGORY_BITS_PLAYER

    val MASK_BITS_PLAYER_HITTING_CEILING: Int = CATEGORY_BITS_PLAYER or
            CATEGORY_BITS_WALL or
            CATEGORY_BITS_CEILING or
            CATEGORY_BITS_LETHAL or
            CATEGORY_BITS_ITEM

    val MASK_BITS_PLAYER: Int = CATEGORY_BITS_PLAYER or
            CATEGORY_BITS_WALL or
            CATEGORY_BITS_FLOOR or
            CATEGORY_BITS_FLOOR_UNJUMPABLE or
            CATEGORY_BITS_LETHAL or
            CATEGORY_BITS_CEILING or
            CATEGORY_BITS_SPRING or
            CATEGORY_BITS_ITEM

    val MASK_BITS_LETHAL: Int = CATEGORY_BITS_PLAYER
    val MASK_BITS_CEILING: Int = CATEGORY_BITS_PLAYER
    val MASK_BITS_SPRING: Int = CATEGORY_BITS_PLAYER
    val MASK_BITS_ITEM: Int = CATEGORY_BITS_PLAYER

    var getReady = true
    var gameOver = false
    var cameraY = 0f


    // player info
    var playerHp = 100f
    val playerIsDead: Boolean
        get() = playerHp <= 0
}
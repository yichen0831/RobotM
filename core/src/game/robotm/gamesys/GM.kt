package game.robotm.gamesys


object GM {

    val PPM = 64f

    val SCREEN_WIDTH = 16f
    val SCREEN_HEIGHT = 20f

    val PLAYER_SCALE = 1.5f

    val CATEGORY_BITS_STATIC_OBSTACLE: Int = 1
    val CATEGORY_BITS_PLAYER: Int = 2

    val MASK_BITS_STATIC_OBSTACLE: Int = CATEGORY_BITS_PLAYER
    val MASK_BITS_PLAYER: Int = CATEGORY_BITS_STATIC_OBSTACLE or CATEGORY_BITS_PLAYER

    var getReady = true
    var gameOver = false
    var cameraY = 0f
}
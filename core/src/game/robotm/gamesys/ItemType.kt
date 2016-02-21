package game.robotm.gamesys

import com.badlogic.gdx.math.MathUtils


enum class ItemType {
    HardSkin,
    QuickHealing,
    FastFeet,
    LowGravity;

    companion object {
        val typeArray = arrayOf(HardSkin, QuickHealing, FastFeet, LowGravity)

        fun randomType(): ItemType {
            return typeArray[MathUtils.random(typeArray.size - 1)]
        }
     }
}
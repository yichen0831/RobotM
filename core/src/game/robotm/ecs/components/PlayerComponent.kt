package game.robotm.ecs.components

import com.badlogic.ashley.core.Component
import game.robotm.gamesys.ItemType


class PlayerComponent: Component {

    companion object {
        val HIT_CEILING_COUNT_DOWN = 0.5f

        val DAMAGE_PER_SECOND = 25f

        val HP_REGENERATION_PER_SECOND = 2f
        val HP_REGENERATION_COLD_DURATION = 3f

        val SPEED = 6f

        val FULL_HP = 100f
    }

    var speed = SPEED
    var jumpForce = 8f

    var hp = FULL_HP

    var damage_per_second = DAMAGE_PER_SECOND

    val isDead: Boolean
        get() = hp <= 0

    var deadCountDown = 2f
    var explosionEffect = false

    var hp_regeneration_per_second = HP_REGENERATION_PER_SECOND

    var hp_regeneration_cd_time = 3f
    var hp_regeneration_cd = hp_regeneration_cd_time

    var lethalContactCount = 0

    var hitSpring = false

    var hitCeiling = false
    var hitCeilingCountDown = HIT_CEILING_COUNT_DOWN

    val powerUpStatusMap = hashMapOf(
            ItemType.FastFeet to 0f,
            ItemType.HardSkin to 0f,
            ItemType.QuickHealing to 0f,
            ItemType.LowGravity to 0f)

    fun applyPowerUp(type: ItemType, duration: Float = 5f) {
        powerUpStatusMap[type] = duration
    }
}
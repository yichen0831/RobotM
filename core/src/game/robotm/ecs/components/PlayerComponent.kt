package game.robotm.ecs.components

import com.badlogic.ashley.core.Component


class PlayerComponent: Component {

    companion object {
        val HIT_CEILING_COUNT_DOWN = 0.5f

        val DAMAGE_PER_SECOND = 40f

        val HP_REGENERATION_PER_SECOND = 2f
        val HP_REGENERATION_COLD_DURATION = 3f

        val FULL_HP = 100f
    }

    val speed = 6f
    var jumpForce = 8f

    var hp = FULL_HP

    var hp_regeneration_per_second = HP_REGENERATION_PER_SECOND
    var hp_regeneration_cd = HP_REGENERATION_COLD_DURATION

    var lethalContactCount = 0

    var hitSpring = false

    var hitCeiling = false
    var hitCeilingCountDown = HIT_CEILING_COUNT_DOWN
}
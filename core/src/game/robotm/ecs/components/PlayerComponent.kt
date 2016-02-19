package game.robotm.ecs.components

import com.badlogic.ashley.core.Component


class PlayerComponent: Component {
    val speed = 6f
    var hp = 100f

    var lethalContactCount = 0

    var hitCeiling = false
    var hitCeilingCountDown = 0.5f
}
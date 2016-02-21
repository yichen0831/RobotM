package game.robotm.ecs.components

import com.badlogic.ashley.core.Component

enum class InteractionType {
    SPRING,
    ENEMY,
    ITEM
}

class InteractionComponent(val type: InteractionType, val subType: String = "empty") : Component {
    var status = "normal"
}
package game.robotm.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import java.util.*

class AnimationComponent(val animations: HashMap<String, Animation>, var currentAnim: String) : Component {
    var animTime = 0f
}
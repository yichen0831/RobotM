package game.robotm.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import java.util.*

class AnimationComponent(val animations: HashMap<String, Animation>, initAnim: String) : Component {
    var animTime = 0f

    var previousAnim: String = initAnim

    var currentAnim: String = initAnim
        set(value) {
            if (value != previousAnim) {
                animTime = 0f
                previousAnim = value
            }
            field = value
        }
}
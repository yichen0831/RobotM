package game.robotm.ecs.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body


class PhysicsComponent(val body: Body): Component
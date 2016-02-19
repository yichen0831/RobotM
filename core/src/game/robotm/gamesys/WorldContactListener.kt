package game.robotm.gamesys

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import game.robotm.ecs.components.InteractionComponent
import game.robotm.ecs.components.PlayerComponent


class WorldContactListener : ContactListener {

    override fun beginContact(contact: Contact) {

        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB

        if (fixtureA.filterData.categoryBits == GM.CATEGORY_BITS_PLAYER.toShort() ||
                fixtureB.filterData.categoryBits == GM.CATEGORY_BITS_PLAYER.toShort()) {
            if (fixtureA.filterData.categoryBits == GM.CATEGORY_BITS_PLAYER.toShort()) {
                val playerEntity: Entity = fixtureA.body.userData as Entity
                val playerComponent: PlayerComponent = playerEntity.getComponent(PlayerComponent::class.java)

                when (fixtureB.filterData.categoryBits) {
                    GM.CATEGORY_BITS_LETHAL.toShort() -> {
                        playerComponent.lethalContactCount++
                    }
                    GM.CATEGORY_BITS_CEILING.toShort() -> {
                        playerComponent.hitCeiling = true
                    }
                    GM.CATEGORY_BITS_SPRING.toShort() -> {
                        playerComponent.hitSpring = true
                        val springEntity: Entity = fixtureB.body.userData as Entity
                        val interactionComponent = springEntity.getComponent(InteractionComponent::class.java)
                        interactionComponent.status = "hit"
                    }
                }
            } else {
                val playerEntity: Entity = fixtureB.body.userData as Entity
                val playerComponent: PlayerComponent = playerEntity.getComponent(PlayerComponent::class.java)

                when (fixtureA.filterData.categoryBits) {
                    GM.CATEGORY_BITS_LETHAL.toShort() -> {
                        playerComponent.lethalContactCount++
                    }
                    GM.CATEGORY_BITS_CEILING.toShort() -> {
                        playerComponent.hitCeiling = true
                    }
                    GM.CATEGORY_BITS_SPRING.toShort() -> {
                        playerComponent.hitSpring = true
                        val springEntity: Entity = fixtureA.body.userData as Entity
                        val interactionComponent = springEntity.getComponent(InteractionComponent::class.java)
                        interactionComponent.status = "hit"
                    }
                }
            }
        }
    }

    override fun endContact(contact: Contact) {

        val fixtureA = contact.fixtureA
        val fixtureB = contact.fixtureB

        if (fixtureA.filterData.categoryBits == GM.CATEGORY_BITS_PLAYER.toShort() ||
                fixtureB.filterData.categoryBits == GM.CATEGORY_BITS_PLAYER.toShort()) {
            if (fixtureA.filterData.categoryBits == GM.CATEGORY_BITS_PLAYER.toShort()) {
                val playerEntity: Entity = fixtureA.body.userData as Entity
                val playerComponent: PlayerComponent = playerEntity.getComponent(PlayerComponent::class.java)

                when (fixtureB.filterData.categoryBits) {
                    GM.CATEGORY_BITS_LETHAL.toShort() -> {

                        playerComponent.lethalContactCount--
                    }
                }
            } else {
                val playerEntity: Entity = fixtureB.body.userData as Entity
                val playerComponent: PlayerComponent = playerEntity.getComponent(PlayerComponent::class.java)

                when (fixtureA.filterData.categoryBits) {
                    GM.CATEGORY_BITS_LETHAL.toShort() -> {

                        playerComponent.lethalContactCount--
                    }
                }
            }
        }
    }

    override fun preSolve(contact: Contact, p1: Manifold) {
    }

    override fun postSolve(contact: Contact, contactImpulse: ContactImpulse) {
    }

}
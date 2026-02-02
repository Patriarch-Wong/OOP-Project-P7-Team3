package io.github.team3engine.entity;

import com.badlogic.gdx.math.Rectangle;

/**
 * Objects that have a hitbox and can participate in collision.
 * Implemented by {@link CollidableEntity}; allows a collision system to work with any
 * collidable type without depending on the concrete class.
 */
public interface Collidable {
    Rectangle getHitbox();

    /** Called when this object collides with another collidable. */
    void onCollision(Collidable other);
}

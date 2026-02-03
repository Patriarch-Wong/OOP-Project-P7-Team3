package io.github.team3engine.interfaces;

import com.badlogic.gdx.math.Rectangle;

/**
 * Objects that have a hitbox and can participate in collision.
 * Allows a collision system to work with any collidable type without depending on concrete classes.
 */
public interface Collidable {
    Rectangle getHitbox();

    /** Called when this object collides with another collidable. */
    void onCollision(Collidable other);
}

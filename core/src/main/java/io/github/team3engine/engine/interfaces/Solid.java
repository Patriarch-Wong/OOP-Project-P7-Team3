package io.github.team3engine.engine.interfaces;

import io.github.team3engine.engine.collision.SolidCollisionResolver;
import io.github.team3engine.engine.entity.CollidableEntity;

/**
 * Marker interface for entities that act as solid surfaces (floors, walls, ceilings).
 * Used by ground/ceiling detection to identify collidable surfaces
 * without coupling to concrete entity types.
 */
public interface Solid {

    /**
     * Default collision response for solids: push out overlapping CollidableEntity.
     */
    default void onCollision(Collidable other) {
        if (this instanceof CollidableEntity) {
            SolidCollisionResolver.resolve((CollidableEntity) this, other);
        }
    }
}

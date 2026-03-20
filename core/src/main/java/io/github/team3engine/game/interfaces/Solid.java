package io.github.team3engine.game.interfaces;

/**
 * Marker interface for entities that act as solid surfaces (floors, walls, ceilings).
 * Used by ground/ceiling detection to identify collidable surfaces
 * without coupling to concrete entity types.
 *
 * Implementors that need push-out collision should call
 * {@code SolidCollisionResolver.resolve(this, other)} in their own
 * {@code onCollision()} method.
 */
public interface Solid {
}

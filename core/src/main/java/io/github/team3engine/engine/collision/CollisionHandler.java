package io.github.team3engine.engine.collision;

/**
 * Handles a collision between two typed entities.
 * Used by CollisionMediator to dispatch collision logic
 * without entities needing to know about each other.
 *
 * Type parameters are not bounded to Collidable so that handlers
 * can match on any interface (e.g. Damageable, Pickup) that a
 * collidable entity implements.
 */
public interface CollisionHandler<A, B> {
    void handle(A a, B b);
}

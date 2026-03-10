package io.github.team3engine.engine.interfaces;

import io.github.team3engine.engine.entity.Entity;

/**
 * Interface for entities that can be picked up on contact.
 * After onPickup is called, the entity should be destroyed.
 */
public interface Pickup {
    void onPickup(Entity collector);
}

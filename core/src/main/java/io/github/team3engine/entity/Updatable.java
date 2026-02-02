package io.github.team3engine.entity;

/**
 * Objects that can be updated each frame.
 * Implemented by {@link Entity} and can be used by systems that only need to run logic (e.g. game loop, managers).
 */
public interface Updatable {
    void update(float dt);
}

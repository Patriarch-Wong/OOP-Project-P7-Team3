package io.github.team3engine.interfaces;

/**
 * Objects that can be updated each frame.
 * Can be implemented by entities, systems, or any object that participates in the game loop.
 */
public interface Updatable {
    void update(float dt);
}

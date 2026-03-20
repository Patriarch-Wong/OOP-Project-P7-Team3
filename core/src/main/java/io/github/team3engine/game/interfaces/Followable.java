package io.github.team3engine.game.interfaces;

/**
 * Minimal contract for entities that can be followed by NPC companions.
 */
public interface Followable {
    float getX();
    float getY();
    boolean isFacingRight();
}

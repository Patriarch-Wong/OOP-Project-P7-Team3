package io.github.team3engine.engine.interfaces;

/**
 * Interface for entities that can take damage and have hit points.
 */
public interface Damageable {
    void takeDamage(float amount);
    void heal(float amount);
    float getHp();
    float getMaxHp();
    boolean isAlive();
    boolean isInvincible();
}

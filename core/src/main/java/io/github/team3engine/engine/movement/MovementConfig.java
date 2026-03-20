package io.github.team3engine.engine.movement;

/**
 * Immutable movement tuning values used by {@link MovementManager}.
 */
public class MovementConfig {

    private final float maxSpeed;
    private final float maxFallSpeed;
    private final float acceleration;
    private final float deceleration;
    private final float gravity;

    public MovementConfig(float maxSpeed, float maxFallSpeed, float acceleration, float deceleration, float gravity) {
        this.maxSpeed = maxSpeed;
        this.maxFallSpeed = maxFallSpeed;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
        this.gravity = gravity;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getMaxFallSpeed() {
        return maxFallSpeed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getDeceleration() {
        return deceleration;
    }

    public float getGravity() {
        return gravity;
    }
}

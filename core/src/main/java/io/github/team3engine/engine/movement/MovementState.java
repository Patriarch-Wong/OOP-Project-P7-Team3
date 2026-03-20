package io.github.team3engine.engine.movement;

// each entity that uses movement has its own instance of this class
public class MovementState {

    private float velocityX = 0f;
    private float velocityY = 0f;
    private float speedMultiplier = 1f;
    private boolean movementEnabled = true;

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = Math.max(0f, speedMultiplier);
    }

    public boolean isMovementEnabled() {
        return movementEnabled;
    }

    public void setMovementEnabled(boolean movementEnabled) {
        this.movementEnabled = movementEnabled;
    }

    /**
     * Resets all movement state to default values.
     */
    public void reset() {
        velocityX = 0f;
        velocityY = 0f;
        speedMultiplier = 1f;
        movementEnabled = true;
    }
}

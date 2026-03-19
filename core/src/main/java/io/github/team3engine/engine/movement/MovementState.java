package io.github.team3engine.engine.movement;

// each entity that uses movement has its own instance of this class
public class MovementState {

    private float velocityX = 0f;
    private float velocityY = 0f;
    
    private boolean isGrounded = true;
    private boolean isCrouching = false;
    private boolean movementEnabled = true;
    private float jumpCooldownRemaining = 0f;

    // Getters and Setters
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

    public boolean isGrounded() {
        return isGrounded;
    }

    public void setGrounded(boolean grounded) {
        this.isGrounded = grounded;
    }

    public boolean isCrouching() {
        return isCrouching;
    }

    public boolean setCrouching(boolean crouching) {
        this.isCrouching = crouching;
        return crouching;
    }

    public boolean isMovementEnabled() {
        return movementEnabled;
    }

    public void setMovementEnabled(boolean movementEnabled) {
        this.movementEnabled = movementEnabled;
    }

    public float getJumpCooldownRemaining() {
        return jumpCooldownRemaining;
    }

    public void setJumpCooldownRemaining(float jumpCooldownRemaining) {
        this.jumpCooldownRemaining = jumpCooldownRemaining;
    }

    /**
     * Resets all movement state to default values.
     */
    public void reset() {
        velocityX = 0f;
        velocityY = 0f;
        isGrounded = true;
        jumpCooldownRemaining = 0f;
        movementEnabled = true;
    }
}

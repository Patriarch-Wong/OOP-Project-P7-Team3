package io.github.team3engine.entity;

public class MovementManager {

    // Movement configuration
    private float maxSpeed = 5.0f;
    private float acceleration = 20.0f;
    private float jumpForce = 8.0f;
    private float gravity = -20.0f;

    // State
    private float velocityX = 0.0f;
    private float velocityY = 0.0f;
    private boolean isGrounded = true;
    private boolean movementEnabled = true;

    public void applyMovement(MovementInput input, float deltaTime) {
        if (!movementEnabled) {
            return;
        }

        // Horizontal movement (player-controlled)
        velocityX += input.movementAxis * acceleration * deltaTime;

        // Clamp horizontal speed
        velocityX = clamp(velocityX, -maxSpeed, maxSpeed);

        // Jump (discrete action)
        if (input.jump && isGrounded) {
            velocityY = jumpForce;
            isGrounded = false;
        }

        // Gravity (always applies)
        velocityY += gravity * deltaTime;
    }

    public void setGrounded(boolean grounded) {
        this.isGrounded = grounded;
        if (grounded) {
            velocityY = 0;
        }
    }

    public void enableMovement() {
        movementEnabled = true;
    }

    public void disableMovement() {
        movementEnabled = false;
        velocityX = 0;
    }

    // Utility (private, not part of UML)
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

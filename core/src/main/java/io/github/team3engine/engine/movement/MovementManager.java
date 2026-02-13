package io.github.team3engine.engine.movement;

import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.IMovementInput;

public class MovementManager {
    // Movement configuration
    private float maxSpeed = 300f;
    private float maxFallSpeed = -600f;
    private float acceleration = 700f;
    private float deceleration = 700f;

    private float jumpForce = 450.0f;
    private float gravity = -900.0f;
    private float jumpCooldownDuration = 0.6f;

    // State (start at rest so input responds immediately)
    private float velocityX = 0f;
    private float velocityY = 0f;
    private boolean isGrounded = true;
    private boolean movementEnabled = true;
    private float jumpCooldownRemaining = 0f;

    public void applyMovement(Entity entity, IMovementInput input, float deltaTime) {
        if (!movementEnabled) {
            return;
        }

        float axis = input.getMovementAxis();
        if (Math.abs(axis) > 0.01f) {
            velocityX += axis * acceleration * deltaTime;
        } else {
            // Decelerate when no input
            if (velocityX > 0) {
                velocityX -= deceleration * deltaTime;
                if (velocityX < 0)
                    velocityX = 0;
            } else if (velocityX < 0) {
                velocityX += deceleration * deltaTime;
                if (velocityX > 0)
                    velocityX = 0;
            }
        }

        // Clamp horizontal speed
        velocityX = clamp(velocityX, -maxSpeed, maxSpeed);

        // Jump cooldown: tick down each frame
        jumpCooldownRemaining -= deltaTime;
        if (jumpCooldownRemaining < 0f)
            jumpCooldownRemaining = 0f;

        if (input.isJump() && isGrounded && jumpCooldownRemaining <= 0f) {
            velocityY = jumpForce;
            jumpCooldownRemaining = jumpCooldownDuration;
            isGrounded = false;
        }

        // Gravity (always applies)
        velocityY += gravity * deltaTime;
        velocityY = Math.max(velocityY, maxFallSpeed);

       // entity.setPos(entity.getX() + velocityX * deltaTime, entity.getY() + velocityY * deltaTime);
        entity.getPos().x += velocityX * deltaTime; // check if supposed to be get rather than set
        entity.getPos().y += velocityY * deltaTime;
    }

    public void setGrounded(boolean grounded) {
        this.isGrounded = grounded;
        // Only zero downward velocity when landing; don't zero upward velocity so we
        // don't fight
        // collision resolution (platform pushing entity up) and cause sticking/jitter.
        if (grounded && velocityY < 0f) {
            velocityY = 0f;
        }
    }

    /** True when vertical velocity is upward. */
    public boolean isMovingUpward() {
        return velocityY > 1f;
    }

    /** True when horizontal speed is above threshold. */
    public boolean hasHorizontalMotion() {
        return Math.abs(velocityX) > 5f;
    }

    /** Set vertical velocity to zero (upward motion canceled). */
    public void cancelUpwardVelocity() {
        velocityY = 0f;
    }

    public void enableMovement() {
        movementEnabled = true;
    }

    public void disableMovement() {
        movementEnabled = false;
        velocityX = 0;
    }

    public void hitCeiling() {
        if (velocityY > 0)
            velocityY = 0;
    }
    
    public boolean isGrounded() {
        return isGrounded;
    }

    public float getJumpCooldownRemaining() {
        return jumpCooldownRemaining;
    }

    /** Resets velocity and grounded state (e.g. when switching to a new scene reusing this manager). */
    public void reset() {
        velocityX = 0f;
        velocityY = 0f;
        isGrounded = true;
        jumpCooldownRemaining = 0f;
        movementEnabled = true;
    }

    // Utility (private, not part of UML)
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

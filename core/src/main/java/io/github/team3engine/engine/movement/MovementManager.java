package io.github.team3engine.engine.movement;

import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.IMovementInput;

public class MovementManager {
    // Movement configuration (shared across all entities)
    private float maxSpeed = 300f;
    private float maxFallSpeed = -600f;
    private float acceleration = 700f;
    private float deceleration = 700f;

    private float jumpForce = 620.0f;
    private float gravity = -900.0f;
    private float jumpCooldownDuration = 0.6f;

    // Apply movement to an entity based on its MovementState and input.
    public void applyMovement(Entity entity, MovementState state, IMovementInput input, float deltaTime) {
        if (!state.isMovementEnabled()) {
            return;
        }

        float velocityX = state.getVelocityX();
        float velocityY = state.getVelocityY();

        // Horizontal movement
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
        float jumpCooldown = state.getJumpCooldownRemaining();
        jumpCooldown -= deltaTime;
        if (jumpCooldown < 0f)
            jumpCooldown = 0f;
        state.setJumpCooldownRemaining(jumpCooldown);

        // Jump
        if (input.isJump() && state.isGrounded() && jumpCooldown <= 0f) {
            velocityY = jumpForce;
            state.setJumpCooldownRemaining(jumpCooldownDuration);
            state.setGrounded(false);
        }

        // Gravity (always applies)
        velocityY += gravity * deltaTime;
        velocityY = Math.max(velocityY, maxFallSpeed);

        // Update state
        state.setVelocityX(velocityX);
        state.setVelocityY(velocityY);

        // Apply movement to entity position
        entity.getPos().x += velocityX * deltaTime;
        entity.getPos().y += velocityY * deltaTime;
    }

    /**
     * Set grounded state for an entity.
     * Only zeros downward velocity when landing.
     */
    public void setGrounded(MovementState state, boolean grounded) {
        state.setGrounded(grounded);
        // Only zero downward velocity when landing; don't zero upward velocity
        if (grounded && state.getVelocityY() < 0f) {
            state.setVelocityY(0f);
        }
    }

    // True when vertical velocity is upward.
    public boolean isMovingUpward(MovementState state) {
        return state.getVelocityY() > 1f;
    }

    // True when horizontal speed is above threshold.
    public boolean hasHorizontalMotion(MovementState state) {
        return Math.abs(state.getVelocityX()) > 5f;
    }

    //Set vertical velocity to zero (upward motion canceled).
    public void cancelUpwardVelocity(MovementState state) {
        state.setVelocityY(0f);
    }

    //Enable movement for an entity.
    public void enableMovement(MovementState state) {
        state.setMovementEnabled(true);
    }

    //Disable movement for an entity and zero horizontal velocity.
    public void disableMovement(MovementState state) {
        state.setMovementEnabled(false);
        state.setVelocityX(0);
    }

    //Handle ceiling collision (cancel upward velocity).
    public void hitCeiling(MovementState state) {
        if (state.getVelocityY() > 0)
            state.setVelocityY(0);
    }

    // Utility (private, not part of UML)
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    // Configuration getters/setters 
    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getMaxFallSpeed() {
        return maxFallSpeed;
    }

    public void setMaxFallSpeed(float maxFallSpeed) {
        this.maxFallSpeed = maxFallSpeed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public float getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    public float getJumpForce() {
        return jumpForce;
    }

    public void setJumpForce(float jumpForce) {
        this.jumpForce = jumpForce;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getJumpCooldownDuration() {
        return jumpCooldownDuration;
    }

    public void setJumpCooldownDuration(float jumpCooldownDuration) {
        this.jumpCooldownDuration = jumpCooldownDuration;
    }
}

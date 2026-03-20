package io.github.team3engine.engine.movement;

import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.IMovementInput;

public class MovementManager {
    // Apply movement to an entity based on its MovementState, config, and input.
    public void applyMovement(Entity entity, MovementState state, MovementConfig config, IMovementInput input, float deltaTime) {
        if (!state.isMovementEnabled()) {
            return;
        }

        float velocityX = state.getVelocityX();
        float velocityY = state.getVelocityY();

        // Horizontal movement
        float axis = input.getMovementAxis();
        if (Math.abs(axis) > 0.01f) {
            velocityX += axis * config.getAcceleration() * deltaTime;
        } else {
            // Decelerate when no input
            if (velocityX > 0f) {
                velocityX -= config.getDeceleration() * deltaTime;
                if (velocityX < 0f) {
                    velocityX = 0f;
                }
            } else if (velocityX < 0f) {
                velocityX += config.getDeceleration() * deltaTime;
                if (velocityX > 0f) {
                    velocityX = 0f;
                }
            }
        }

        float maxSpeed = config.getMaxSpeed() * state.getSpeedMultiplier();
        velocityX = clamp(velocityX, -maxSpeed, maxSpeed);

        // Gravity always applies in the engine layer.
        velocityY += config.getGravity() * deltaTime;
        velocityY = Math.max(velocityY, config.getMaxFallSpeed());

        state.setVelocityX(velocityX);
        state.setVelocityY(velocityY);

        entity.getPos().x += velocityX * deltaTime;
        entity.getPos().y += velocityY * deltaTime;
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
        state.setVelocityX(0f);
    }

    //Handle ceiling collision (cancel upward velocity).
    public void hitCeiling(MovementState state) {
        if (state.getVelocityY() > 0f) {
            state.setVelocityY(0f);
        }
    }

    // Utility (private, not part of UML)
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

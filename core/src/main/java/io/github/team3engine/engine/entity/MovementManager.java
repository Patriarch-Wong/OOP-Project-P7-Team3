package io.github.team3engine.engine.entity;

import io.github.team3engine.engine.audio.AudioManager; // You'll need this import
import io.github.team3engine.game.entities.MovementInput;

public class MovementManager {

    // Audio Reference
    private AudioManager audioManager;
    private float footstepTimer = 0;
    private final float FOOTSTEP_INTERVAL = 0.35f; // Adjust this to match walk speed

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

    public MovementManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    // CHANGE signature
    public void applyMovement(Entity entity, MovementInput input, float deltaTime) {
        if (!movementEnabled) { // if not supposed to move
            return;
        }

        // Horizontal movement (player-controlled)
        // velocityX += input.movementAxis * acceleration * deltaTime;
        if (Math.abs(input.movementAxis) > 0.01f) {
            // Accelerate when input is held
            velocityX += input.movementAxis * acceleration * deltaTime;
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

        // --- FOOTSTEP SOUND LOGIC ---
        // Play sound if moving on ground and input is being pressed
        if (isGrounded && Math.abs(input.movementAxis) > 0.1f) {
            footstepTimer += deltaTime;
            if (footstepTimer >= FOOTSTEP_INTERVAL) {
                audioManager.play("walk.mp3");
                footstepTimer = 0;
            }
        } else {
            footstepTimer = FOOTSTEP_INTERVAL; // Reset so next step plays instantly
        }

        // Clamp horizontal speed
        velocityX = clamp(velocityX, -maxSpeed, maxSpeed);

        // Jump cooldown: tick down each frame
        jumpCooldownRemaining -= deltaTime;
        if (jumpCooldownRemaining < 0f)
            jumpCooldownRemaining = 0f;

        // Jump (allowed when cooldown is ready; can jump mid-air after cooldown)
        if (input.jump && isGrounded && jumpCooldownRemaining <= 0f) {
            velocityY = jumpForce;
            jumpCooldownRemaining = jumpCooldownDuration;
            isGrounded = false;
            audioManager.play("jump.mp3");
        }

        // Gravity (always applies)
        velocityY += gravity * deltaTime;
        velocityY = Math.max(velocityY, maxFallSpeed);

        entity.setPos(entity.getX() + velocityX * deltaTime, entity.getY() + velocityY * deltaTime);

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

    // Utility (private, not part of UML)
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

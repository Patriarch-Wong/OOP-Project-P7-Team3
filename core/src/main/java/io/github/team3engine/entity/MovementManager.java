package io.github.team3engine.entity;

import io.github.team3engine.audio.AudioManager; // You'll need this import

public class MovementManager {

    // Audio Reference
    private AudioManager audioManager;
    private float footstepTimer = 0;
    private final float FOOTSTEP_INTERVAL = 0.35f; // Adjust this to match walk speed

    // Movement configuration
    private float maxSpeed = 1000.0f;
    private float acceleration = 50.0f;
    private float jumpForce = 100.0f;
    private float gravity = -50.0f;
    private float jumpCooldownDuration = 0.6f;

    // State
    private float velocityX = 30f;
    private float velocityY = 30f;
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
        velocityX += input.movementAxis * acceleration * deltaTime;

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
        if (jumpCooldownRemaining < 0f) jumpCooldownRemaining = 0f;

        // Jump (allowed when cooldown is ready; can jump mid-air after cooldown)
        if (input.jump && jumpCooldownRemaining <= 0f) {
            velocityY = jumpForce;
            jumpCooldownRemaining = jumpCooldownDuration;
            isGrounded = false;
            audioManager.play("jump.mp3");
        }

        // Gravity (always applies)
        velocityY += gravity * deltaTime;

        entity.getPos().x += velocityX * deltaTime; //check if supposed to be get rather than set
        entity.getPos().y += velocityY * deltaTime; 
        
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

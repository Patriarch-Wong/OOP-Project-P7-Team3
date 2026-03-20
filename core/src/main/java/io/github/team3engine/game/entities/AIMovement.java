package io.github.team3engine.game.entities;

import io.github.team3engine.engine.interfaces.IMovementInput;

/**
 * AI-controlled movement input that moves left and right automatically.
 * The AI changes direction when it reaches boundaries.
 */
public class AIMovement implements IMovementInput {
    private float movementAxis = 1f; // Start moving right
    private float minX; // Left boundary
    private float maxX; // Right boundary
    private float entityWidth; // Width of the entity (for boundary checking)

    // Create AI movement with screen boundaries.
    public AIMovement(float minX, float maxX, float entityWidth) {
        this.minX = minX;
        this.maxX = maxX;
        this.entityWidth = entityWidth;
    }

    public void update(float entityX, float deltaTime) {
        // Boundary detection reverse direction when hitting edges
        if (entityX <= minX && movementAxis < 0) {
            movementAxis = 0.5f; // Move right
        } else if (entityX + entityWidth >= maxX && movementAxis > 0) {
            movementAxis = -0.5f; // Move left
        }
    }

    @Override
    public float getMovementAxis() {
        return movementAxis;
    }

    @Override
    public boolean isJump() {
        return false;
    }

    @Override
    public boolean isCrawl() {
        return false; // default behavior
    }

    // Configuration methods
    public void setDirection(float direction) {
        this.movementAxis = direction;
    }

    public void reverseDirection() {
        this.movementAxis *= -1f;
    }

    public void setBoundaries(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }
}
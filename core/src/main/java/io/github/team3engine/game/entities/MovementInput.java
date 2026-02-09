package io.github.team3engine.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import io.github.team3engine.engine.entity.MovementManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.game.inputs.PlayerInput;

public class MovementInput {
	// assume data is already valid when using these values
    public float movementAxis; // -1 is to go left direction, 1 is to go right direction
    public boolean jump;
    private final MovementManager movementManager;
    private final IOManager io;
    private final PlayerInput playerInput;

    public MovementInput(MovementManager movementManager, IOManager io, PlayerInput playerInput) {
        this.movementManager = movementManager;
        this.io = io;
        this.playerInput = playerInput;
    }

    /**
     * Polls the keyboard to update the intent state.
     */
    public void update() {
        movementAxis = 0;

        // Horizontal movement logic
        if (playerInput.isLeftHeld()) {
            movementAxis -= 1;
            if (movementManager.isGrounded()) 
                io.broadcast("PLAYER_MOVING");
        }
        if (playerInput.isRightHeld()) {
            movementAxis += 1;
            if (movementManager.isGrounded())
                io.broadcast("PLAYER_MOVING");
        }

        // Jump logic
        jump = playerInput.isSpaceHeld();
        if (jump) {
            if (movementManager.isGrounded() && movementManager.getJumpCooldownRemaining() <= 0f) {
                io.broadcast("PLAYER_JUMP");
            }
        }
    }
}
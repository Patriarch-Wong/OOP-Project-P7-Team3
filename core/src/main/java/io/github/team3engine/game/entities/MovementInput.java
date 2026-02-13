package io.github.team3engine.game.entities;

import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.interfaces.IMovementInput;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.game.inputs.PlayerInput;

public class MovementInput implements IMovementInput {
    public float movementAxis;
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

        jump = playerInput.isSpaceHeld();
        if (jump && movementManager.isGrounded() && movementManager.getJumpCooldownRemaining() <= 0f) {
            io.broadcast("PLAYER_JUMP");
        }
    }

    @Override
    public float getMovementAxis() {
        return movementAxis;
    }

    @Override
    public boolean isJump() {
        return jump;
    }
}
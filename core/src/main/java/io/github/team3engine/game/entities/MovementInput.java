package io.github.team3engine.game.entities;

import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.interfaces.IMovementInput;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.game.inputs.PlayerInput;
import io.github.team3engine.engine.movement.MovementState;

public class MovementInput implements IMovementInput {
    public float movementAxis;
    public boolean jump;
    private final MovementState movementState;
    private final IOManager io;
    private final PlayerInput playerInput;

    public MovementInput(MovementState movementState, IOManager io, PlayerInput playerInput) {
        this.movementState = movementState;
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
            if (movementState.isGrounded())
                io.broadcast("PLAYER_MOVING");
        }
        if (playerInput.isRightHeld()) {
            movementAxis += 1;
            if (movementState.isGrounded())
                io.broadcast("PLAYER_MOVING");
        }

        jump = playerInput.isSpaceHeld();
        if (jump && movementState.isGrounded() && movementState.getJumpCooldownRemaining() <= 0f) {
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
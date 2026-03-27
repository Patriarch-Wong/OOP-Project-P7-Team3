package io.github.team3engine.game.entities;

import io.github.team3engine.engine.interfaces.IMovementInput;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.inputs.PlayerInput;

/**
 * Raw movement intent for player-controlled entities.
 * Game-layer movement rules (grounded/crouch/jump gating) are applied by the owning entity.
 */
public class MovementInput implements IMovementInput {
    private float movementAxis;
    private boolean jumpIntent;
    private boolean crouchIntent;

    private final IOManager io;
    private final PlayerInput playerInput;
    private final GroundedStateProvider groundedStateProvider;

    public MovementInput(IOManager io, PlayerInput playerInput) {
        this((GroundedStateProvider) null, io, playerInput);
    }

    public MovementInput(Player player, IOManager io, PlayerInput playerInput) {
        this(player::isGrounded, io, playerInput);
    }

    private MovementInput(GroundedStateProvider groundedStateProvider, IOManager io, PlayerInput playerInput) {
        this.groundedStateProvider = groundedStateProvider;
        this.io = io;
        this.playerInput = playerInput;
    }

    /**
     * Polls keyboard state and stores raw movement intent for this frame.
     */
    public void update() {
        movementAxis = 0f;

        if (playerInput.isLeftHeld()) {
            movementAxis -= 1f;
        }
        if (playerInput.isRightHeld()) {
            movementAxis += 1f;
        }

        jumpIntent = playerInput.isSpaceHeld();
        crouchIntent = playerInput.isDownHeld();

        if (io != null && isGrounded() && movementAxis != 0f) {
            io.broadcast(GameEvents.PLAYER_MOVING);
        }

        if (crouchIntent && io != null && isGrounded()) {
            io.broadcast(GameEvents.PLAYER_CROUCH);
        }
    }

    @Override
    public float getMovementAxis() {
        return movementAxis;
    }

    /**
     * Raw jump signal only. The engine does not apply jump mechanics.
     */
    @Override
    public boolean isJump() {
        return jumpIntent;
    }

    public boolean isJumpIntent() {
        return jumpIntent;
    }

    public boolean isCrouchIntent() {
        return crouchIntent;
    }

    private boolean isGrounded() {
        return groundedStateProvider == null || groundedStateProvider.isGrounded();
    }

    private interface GroundedStateProvider {
        boolean isGrounded();
    }
}

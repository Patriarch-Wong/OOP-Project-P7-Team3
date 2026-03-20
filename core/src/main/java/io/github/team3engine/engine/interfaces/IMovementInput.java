package io.github.team3engine.engine.interfaces;

/**
 * Abstraction for movement intent used by the engine's MovementManager.
 * The game layer provides an implementation (e.g. from keyboard/controller).
 */
public interface IMovementInput {
    /** Horizontal intent: -1 left, 0 none, 1 right. */
    float getMovementAxis();

    /** True when jump is requested. */
    boolean isJump();
}

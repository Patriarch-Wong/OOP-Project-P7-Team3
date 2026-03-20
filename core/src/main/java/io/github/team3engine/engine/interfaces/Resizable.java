package io.github.team3engine.engine.interfaces;

/**
 * For components that need to react to window or viewport size changes.
 */
public interface Resizable {
    void resize(int width, int height);
}

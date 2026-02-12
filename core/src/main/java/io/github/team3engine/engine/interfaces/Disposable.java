package io.github.team3engine.engine.interfaces;

/**
 * For engine components that need explicit cleanup. GameEngine disposes all
 * registered Disposable instances on shutdown.
 */
public interface Disposable {
    void dispose();
}

package io.github.team3engine.engine.interfaces;

/**
 * For components that participate in the game loop render step with delta time.
 * Used by SceneManager (delegates to current scene). Distinct from
 * {@link Renderable} which uses a SpriteBatch.
 */
public interface FrameRenderable {
    void render(float deltaTime);
}

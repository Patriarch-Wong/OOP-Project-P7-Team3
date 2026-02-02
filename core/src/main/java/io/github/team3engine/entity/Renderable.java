package io.github.team3engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Objects that can be rendered with a {@link SpriteBatch}.
 * Implemented by {@link Entity}; allows non-entity drawables (e.g. UI, effects) to participate in the same render pass.
 */
public interface Renderable {
    void render(SpriteBatch batch);
}

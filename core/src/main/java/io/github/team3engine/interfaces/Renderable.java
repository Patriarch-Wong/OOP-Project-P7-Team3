package io.github.team3engine.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Objects that can be rendered with a {@link SpriteBatch}.
 * Allows entities, UI, effects, or other drawables to participate in the same render pass.
 */
public interface Renderable {
    void render(SpriteBatch batch);
}

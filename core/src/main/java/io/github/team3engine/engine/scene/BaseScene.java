package io.github.team3engine.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.team3engine.engine.interfaces.Updatable;

/**
 * Generic scene base with lifecycle: show/hide (on transition), update/render (each frame), dispose.
 * Override {@link #onShow()}, {@link #onHide()}, {@link #getInputProcessorForScene()},
 * {@link #update(float)}, or {@link #render(float)} as needed. Not tied to LibGDX {@link com.badlogic.gdx.Screen}.
 */
public abstract class BaseScene implements Updatable {
    protected final SpriteBatch batch;
    protected final BitmapFont font;
    /** True if this scene owns the font and must dispose it. */
    private final boolean ownFont;

    private Stage stage;

    /** Uses a shared font; caller must not dispose it. Pass null to create and own a font. */
    public BaseScene(SpriteBatch batch, BitmapFont sharedFont) {
        this.batch = batch;
        if (sharedFont != null) {
            this.font = sharedFont;
            this.ownFont = false;
        } else {
            this.font = new BitmapFont();
            this.ownFont = true;
        }
        if (ownFont) {
            this.font.setColor(Color.WHITE);
        }
    }

    /** Creates and owns a BitmapFont (one per scene). Prefer {@link #BaseScene(SpriteBatch, BitmapFont)} with a shared font for lower memory. */
    public BaseScene(SpriteBatch batch) {
        this(batch, null);
    }

    /**
     * Lazy Stage for UI-only scenes (e.g. pause, win). Game scenes that never
     * call this do not allocate a Stage.
     */
    protected final Stage getStage() {
        if (stage == null) {
            stage = new Stage(new ScreenViewport(), batch);
        }
        return stage;
    }

    /** Override to use a different input processor when this scene is shown (default: getStage()). */
    protected InputProcessor getInputProcessorForScene() {
        return getStage();
    }

    /** Returns the input processor for this scene. Used when restoring input after unpause. */
    public InputProcessor getInputProcessor() {
        return getInputProcessorForScene();
    }

    /** Called when this scene becomes active. Sets input processor and invokes {@link #onShow()}. */
    public void show() {
        if (!ownFont) {
            font.setColor(Color.WHITE);
        }
        Gdx.input.setInputProcessor(getInputProcessorForScene());
        onShow();
    }

    /** Override for scene-specific setup (e.g. add stage listeners, create entities). */
    protected void onShow() {}

    /** Updates stage viewport on window resize. Call from game layer (e.g. ApplicationListener.resize) if needed. */
    public void resize(int w, int h) {
        if (stage != null) {
            stage.getViewport().update(w, h, true);
        }
    }

    /** Called when this scene is left (e.g. transition to another scene). */
    public void hide() {
        onHide();
    }

    /** Override for scene-specific cleanup (e.g. clear entities, dispose textures). */
    protected void onHide() {}

    @Override
    public void update(float delta) {}

    /** Clears the screen. No args = black; use overload for custom color when overriding render(). */
    protected final void clearScreen() {
        clearScreen(0f, 0f, 0f, 1f);
    }

    protected final void clearScreen(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /** Draws stage (if created) then calls {@link #renderUI()}. Use when overriding render() to avoid duplicating batch/renderUI. */
    protected final void drawStageAndUI(float delta) {
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
        batch.begin();
        renderUI();
        batch.end();
    }

    /** Renders this scene (clear + stage + renderUI). Override for custom render. */
    public void render(float delta) {
        clearScreen();
        drawStageAndUI(delta);
    }

    /** Draw this scene's UI with the batch already begun. Called from the default {@link #render(float)} after optional Stage. */
    protected abstract void renderUI();

    /** Releases stage and owned font. Call when scene is no longer needed. */
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (ownFont) {
            font.dispose();
        }
    }
}

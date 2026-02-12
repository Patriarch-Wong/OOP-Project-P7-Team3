package io.github.team3engine.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.team3engine.engine.interfaces.Updatable;

/**
 * Generic scene base for the engine. Any game can extend this to define its own
 * scenes. Provides:
 * <ul>
 *   <li>Shared batch and font (subclasses may change {@link #font} color/size)</li>
 *   <li>Optional lazy Stage for UI (only created if {@link #getStage()} is used)</li>
 *   <li>Template render: clear screen (color from {@link #getClearColorR/G/B/A()}) → Stage (if any) → {@link #renderUI()}</li>
 * </ul>
 * Override as needed: {@link #onShow()}, {@link #onHide()}, {@link #getInputProcessorForScene()},
 * {@link #update(float)}, or override {@link #render(float)} for a fully custom loop.
 */
public abstract class BaseScene implements Screen, Updatable {
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
            this.font.setColor(getDefaultFontColor());
        }
    }

    /** Creates and owns a BitmapFont (one per scene). Prefer {@link #BaseScene(SpriteBatch, BitmapFont)} with a shared font for lower memory. */
    public BaseScene(SpriteBatch batch) {
        this(batch, null);
    }

    /** Override to change the default font color (default: white). */
    protected Color getDefaultFontColor() {
        return Color.WHITE;
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

    @Override
    public void show() {
        if (!ownFont) {
            font.setColor(getDefaultFontColor());
        }
        Gdx.input.setInputProcessor(getInputProcessorForScene());
        onShow();
    }

    /** Override for scene-specific setup (e.g. add stage listeners, create entities). */
    protected void onShow() {}

    @Override
    public void resize(int w, int h) {
        if (stage != null) {
            stage.getViewport().update(w, h, true);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    public void hide() {
        onHide();
    }

    /** Override for scene-specific cleanup (e.g. clear entities, dispose textures). */
    protected void onHide() {}

    @Override
    public void update(float delta) {}

    /** Override to change clear color (default: black). */
    protected float getClearColorR() { return 0f; }
    protected float getClearColorG() { return 0f; }
    protected float getClearColorB() { return 0f; }
    protected float getClearColorA() { return 1f; }

    /** Clears the screen using {@link #getClearColorR/G/B/A()}. Use from render() when overriding. */
    protected final void clearScreen() {
        Gdx.gl.glClearColor(getClearColorR(), getClearColorG(), getClearColorB(), getClearColorA());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /** Clears the screen to the given color. Use from render() when overriding. */
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

    @Override
    public void render(float delta) {
        clearScreen();
        drawStageAndUI(delta);
    }

    /** Draw this scene's UI with the batch already begun. Called from the default {@link #render(float)} after optional Stage. */
    protected abstract void renderUI();

    @Override
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

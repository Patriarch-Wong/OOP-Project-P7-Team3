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
import io.github.team3engine.GameEngine;

/**
 * Base for all scenes: optional Stage (lazy), shared batch/font, and template
 * for render (clear → stage → UI). Subclasses implement renderUI() and can
 * override onShow/onHide and getInputProcessorForScene() to customize behavior.
 */
public abstract class BaseScene implements Screen {
    protected final GameEngine engine;
    protected final SpriteBatch batch;
    protected final BitmapFont font;

    private Stage stage;

    public BaseScene(GameEngine engine, SpriteBatch batch) {
        this.engine = engine;
        this.batch = batch;
        this.font = new BitmapFont();
        font.setColor(Color.BLUE);
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
        onShow();
        Gdx.input.setInputProcessor(getInputProcessorForScene());
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

    @Override
    public void hide() {
        onHide();
    }

    /** Override for scene-specific cleanup (e.g. clear entities, dispose textures). */
    protected void onHide() {}

    /** Clears the screen to the given color. Use from render() when overriding. */
    protected final void clearScreen(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /** Draws stage (if created) then UI. Use when overriding render() to avoid duplicating batch/renderUI. */
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
        clearScreen(0, 0, 0, 1);
        drawStageAndUI(delta);
    }

    protected abstract void renderUI();

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        font.dispose();
    }
}

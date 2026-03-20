package io.github.team3engine.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.team3engine.engine.interfaces.Updatable;
import io.github.team3engine.engine.interfaces.Resizable;

public abstract class BaseScene implements Updatable {

    protected final SpriteBatch batch;
    private Stage stage;

    public BaseScene(SpriteBatch batch) {
        this.batch = batch;
    }

    protected final Stage getStage() {
        if (stage == null) {
            stage = new Stage(new ScreenViewport(), batch);
        }
        return stage;
    }

    protected InputProcessor getInputProcessorForScene() { return getStage(); }
    public InputProcessor getInputProcessor() { return getInputProcessorForScene(); }

    public void show() {
        Gdx.input.setInputProcessor(getInputProcessorForScene());
        onShow();
    }

    protected void onShow() {}

    public void resize(int w, int h) {
        if (stage != null) stage.getViewport().update(w, h, true);
    }

    public void hide()  { onHide(); }
    protected void onHide() {}

    @Override
    public void update(float delta) {
    }

    protected final void clearScreen() { clearScreen(0f, 0f, 0f, 1f); }

    protected final void clearScreen(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    protected final void drawStageAndUI(float delta) {
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
        batch.setColor(Color.WHITE);
        batch.begin();
        renderUI();
        batch.end();
    }

    public void render(float delta) {
        clearScreen();
        drawStageAndUI(delta);
    }

    protected abstract void renderUI();

    public void dispose() {
        if (stage != null) { stage.dispose(); stage = null; }
    }
}

package io.github.team3engine.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
<<<<<<< HEAD
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
=======
>>>>>>> 92f0d113cd607df59c2258c16f963d170fb7b03f
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.team3engine.engine.interfaces.Resizable;
import io.github.team3engine.engine.interfaces.Updatable;

public abstract class BaseScene implements Updatable, Resizable {
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
        // Scenes can be entered after a window resize while they were inactive.
        // Sync stage viewport + scene layout on entry to prevent stale HUD/UI placement.
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    protected void onShow() {}

    @Override
    public void resize(int w, int h) {
        if (stage != null) stage.getViewport().update(w, h, true);
        onResize(w, h);
    }

    protected void onResize(int width, int height) {}

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
<<<<<<< HEAD
        OrthographicCamera screenCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        screenCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        screenCam.update();
=======
        // UI/HUD should track the live window size independently from gameplay projection.
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
>>>>>>> a70e28e5d09ddb96a0cdfd6b0026c8df43756d93
        batch.setColor(Color.WHITE);
        batch.begin();
        batch.setProjectionMatrix(screenCam.combined);
        batch.setColor(Color.WHITE);
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

package io.github.team3engine.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.team3engine.engine.interfaces.Updatable;

//Base scene

public abstract class BaseScene implements Updatable {
    protected final SpriteBatch batch;

    private Stage stage;

    // batch to draw scene
    public BaseScene(SpriteBatch batch) {
        this.batch = batch;
    }

    // Stage for UI only scenes
    protected final Stage getStage() {
        if (stage == null) {
            stage = new Stage(new ScreenViewport(), batch);
        }
        return stage;
    }

    // Override to use a different input processor when this scene is shown
    protected InputProcessor getInputProcessorForScene() {
        return getStage();
    }

    // Returns the input processor for this scene
    public InputProcessor getInputProcessor() {
        return getInputProcessorForScene();
    }

    /** Called when this scene becomes active. Sets input processor and invokes {@link #onShow()}. */
    public void show() {
        Gdx.input.setInputProcessor(getInputProcessorForScene());
        onShow();
    }

    //override for scene specific setups
    protected void onShow() {}

    //updates for resize
    public void resize(int w, int h) {
        if (stage != null) {
            stage.getViewport().update(w, h, true);
        }
    }

    //call when transitioning scene
    public void hide() {
        onHide();
    }

    // override for scene clean up
    protected void onHide() {}

    @Override
    public void update(float delta) {}

    //clears scene
    protected final void clearScreen() {
        clearScreen(0f, 0f, 0f, 1f);
    }

    protected final void clearScreen(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    //draws scene then renderui
    protected final void drawStageAndUI(float delta) {
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
        batch.begin();
        renderUI();
        batch.end();
    }

    // redeners scnee
    public void render(float delta) {
        clearScreen();
        drawStageAndUI(delta);
    }

    /** Draw this scene's UI with the batch already begun. Called from the default {@link #render(float)} after optional Stage. */
    protected abstract void renderUI();

    /** Releases stage. Call when scene is no longer needed. */
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}

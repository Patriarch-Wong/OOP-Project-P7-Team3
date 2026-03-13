package io.github.team3engine.engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.team3engine.engine.interfaces.Updatable;
import io.github.team3engine.engine.scoring.ScoreManager;

public abstract class BaseScene implements Updatable {
    protected final SpriteBatch batch;
    private Stage stage;
    private Timer timer = new Timer(60f);
    private boolean timerEnabled = false;

    private final BitmapFont timerFont = new BitmapFont();
    private final GlyphLayout timerLayout = new GlyphLayout();

    public BaseScene(SpriteBatch batch) {
        this.batch = batch;
    }

    protected final Stage getStage() {
        if (stage == null) {
            stage = new Stage(new ScreenViewport(), batch);
        }
        return stage;
    }

    protected InputProcessor getInputProcessorForScene() {
        return getStage();
    }

    public InputProcessor getInputProcessor() {
        return getInputProcessorForScene();
    }

    public void show() {
        Gdx.input.setInputProcessor(getInputProcessorForScene());
        onShow();
    }

    protected void onShow() {}

    // Call this in onShow() of any game scene to start the 60s countdown
    protected void enableTimer() {
        timer = new Timer(60f);
        timerEnabled = true;
        timer.start();
    }

    public void resize(int w, int h) {
        if (stage != null) {
            stage.getViewport().update(w, h, true);
        }
    }

    public void hide() {
        onHide();
    }

    protected void onHide() {}

    @Override
    public void update(float delta) {
        if (timerEnabled) {
            timer.update(delta);
            if (timer.isFinished()) {
                timerEnabled = false; // prevent repeated calls
                onTimerFinished();
            }
        }
    }

    protected void onTimerFinished() {}

    public Timer getTimer() { return timer; }

    protected final void clearScreen() {
        clearScreen(0f, 0f, 0f, 1f);
    }

    protected final void clearScreen(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void renderTimerAndScore() {
        if (!timerEnabled) return;

        // Timer top right
        int seconds = (int) Math.ceil(timer.getTimeRemaining());
        int minutes = seconds / 60;
        int secs = seconds % 60;
        String timeText = String.format("Time: %d:%02d", minutes, secs);

        if (seconds <= 10) {
            timerFont.setColor(Color.RED);
        } else {
            timerFont.setColor(Color.WHITE);
        }

        timerLayout.setText(timerFont, timeText);
        float timerX = Gdx.graphics.getWidth() - timerLayout.width - 20f;
        float timerY = Gdx.graphics.getHeight() - 10f;
        timerFont.draw(batch, timeText, timerX, timerY);

        // Score directly below timer
        String scoreText = "Score: " + ScoreManager.getInstance().getScore();
        timerFont.setColor(Color.YELLOW);
        timerLayout.setText(timerFont, scoreText);
        float scoreX = Gdx.graphics.getWidth() - timerLayout.width - 20f;
        float scoreY = timerY - 20f;
        timerFont.draw(batch, scoreText, scoreX, scoreY);

        timerFont.setColor(Color.WHITE);
    }

    protected final void drawStageAndUI(float delta) {
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
        batch.begin();
        renderUI();
        renderTimerAndScore();
        batch.end();
    }

    public void render(float delta) {
        clearScreen();
        drawStageAndUI(delta);
    }

    protected abstract void renderUI();

    public void dispose() {
        timerFont.dispose();
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}
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

import java.util.function.Supplier;

public abstract class BaseScene implements Updatable {
    protected final SpriteBatch batch;
    private Stage stage;
    private Timer timer;
    private boolean timerEnabled = false;

    private BitmapFont timerFont;
    private GlyphLayout timerLayout;

    // Optional suppliers — set by game layer
    private Supplier<String> scoreDisplay   = null;
    private Supplier<String> rescuedDisplay = null;

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

    protected void enableTimer(float durationSeconds) {
        timer = new Timer(durationSeconds);
        if (timerFont == null) {
            timerFont  = new BitmapFont();
            timerLayout = new GlyphLayout();
        }
        timerEnabled = true;
        timer.start();
    }

    protected void enableTimer() { enableTimer(60f); }

    /** e.g. setScoreDisplay(() -> "Score: " + ScoreManager.getInstance().getScore()); */
    public void setScoreDisplay(Supplier<String> supplier) {
        this.scoreDisplay = supplier;
    }

    /** e.g. setRescuedDisplay(() -> "Rescued: " + rescued + "/1"); */
    public void setRescuedDisplay(Supplier<String> supplier) {
        this.rescuedDisplay = supplier;
    }

    public void resize(int w, int h) {
        if (stage != null) stage.getViewport().update(w, h, true);
    }

    public void hide()  { onHide(); }
    protected void onHide() {}

    @Override
    public void update(float delta) {
        if (timerEnabled) {
            timer.update(delta);
            if (timer.isFinished()) {
                timerEnabled = false;
                onTimerFinished();
            }
        }
    }

    protected void onTimerFinished() {}
    public Timer getTimer() { return timer; }

    protected final void clearScreen() { clearScreen(0f, 0f, 0f, 1f); }

    protected final void clearScreen(float r, float g, float b, float a) {
        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void renderHUD() {
        if (!timerEnabled || timerFont == null) return;

        int seconds = (int) Math.ceil(timer.getTimeRemaining());
        int minutes = seconds / 60;
        int secs    = seconds % 60;
        String timeText    = String.format("Time: %d:%02d", minutes, secs);
        String scoreText   = (scoreDisplay   != null) ? scoreDisplay.get()   : null;
        String rescuedText = (rescuedDisplay != null) ? rescuedDisplay.get() : null;

        float x = Gdx.graphics.getWidth() - 10f;
        float y = Gdx.graphics.getHeight() - 10f;

        // Timer — right-aligned, top right
        timerLayout.setText(timerFont, timeText);
        timerFont.setColor(seconds <= 10 ? Color.RED : Color.WHITE);
        timerFont.draw(batch, timeText, x - timerLayout.width, y);
        y -= timerLayout.height + 6f;

        // Score — right-aligned, below timer
        if (scoreText != null) {
            timerLayout.setText(timerFont, scoreText);
            timerFont.setColor(Color.YELLOW);
            timerFont.draw(batch, scoreText, x - timerLayout.width, y);
            y -= timerLayout.height + 6f;
        }

        // Rescued — right-aligned, below score
        if (rescuedText != null) {
            timerLayout.setText(timerFont, rescuedText);
            timerFont.setColor(Color.WHITE);
            timerFont.draw(batch, rescuedText, x - timerLayout.width, y);
        }

        timerFont.setColor(Color.WHITE);
    }

    protected final void drawStageAndUI(float delta) {
        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
        batch.begin();
        renderUI();
        renderHUD();
        batch.end();
    }

    public void render(float delta) {
        clearScreen();
        drawStageAndUI(delta);
    }

    protected abstract void renderUI();

    public void dispose() {
        if (timerFont != null) { timerFont.dispose(); timerFont = null; }
        if (stage != null) { stage.dispose(); stage = null; }
    }
}

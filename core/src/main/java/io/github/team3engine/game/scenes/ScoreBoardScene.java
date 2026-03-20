package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.game.score.ScoreManager;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.ui.SceneButtonFactory;

public class ScoreBoardScene extends BaseScene {

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final ScoreManager scoreManager;

    private String nextSceneId;
    private int nextLevel = 2;
    private Skin skin;
    private TextButton nextButton;
    private TextButton menuButton;

    public ScoreBoardScene(SpriteBatch batch, BitmapFont sharedFont,
                           SceneManager sceneManager, IOManager ioManager,
                           ScoreManager scoreManager) {
        super(batch);
        this.font = sharedFont;
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.scoreManager = scoreManager;
    }

    public void setNextScene(String sceneId) {
        this.nextSceneId = sceneId;
    }

    public void setNextLevel(int level) {
        Gdx.app.log("ScoreBoard", "setNextLevel called with: " + level);
        this.nextLevel = level;
    }

    @Override
    protected void onShow() {
        super.onShow();
        // Fix 2: reset font colour on entry
        font.setColor(Color.WHITE);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float centerX = screenWidth  / 2f;
        float centerY = screenHeight / 2f;

        // Fix 3: mirror GameOverScene button layout exactly
        float gap    = 20f;
        float totalW = SceneButtonFactory.BUTTON_WIDTH * 2 + gap;
        float startX = centerX - totalW / 2f;
        float btnY   = centerY - 100f;

        nextButton = SceneButtonFactory.create("Next Level", skin, () ->
                ioManager.broadcast(GameEvents.SCOREBOARD_NEXT));

        menuButton = SceneButtonFactory.create("Main Menu", skin, () ->
                ioManager.broadcast(GameEvents.SCOREBOARD_MENU));

        getStage().addActor(nextButton);
        getStage().addActor(menuButton);
        layoutButtons((int) screenWidth, (int) screenHeight);

        ioManager.registerEvent(GameEvents.SCOREBOARD_NEXT, () -> {
            Gdx.app.log("ScoreBoard", "SCOREBOARD_NEXT event fired, nextLevel=" + nextLevel + ", nextSceneId=" + nextSceneId);
            System.out.println("DEBUG: SCOREBOARD_NEXT clicked, nextLevel=" + nextLevel);
            try {
                if (nextSceneId != null) {
                    BaseScene scene = sceneManager.getScene(nextSceneId);
                    Gdx.app.log("ScoreBoard", "Got scene: " + (scene != null ? scene.getClass().getSimpleName() : "null"));
                    System.out.println("DEBUG: Got scene, setting level...");
                    if (scene instanceof TestScene) {
                        Gdx.app.log("ScoreBoard", "Calling setLevel(" + nextLevel + ")");
                        ((TestScene) scene).setLevel(nextLevel);
                        Gdx.app.log("ScoreBoard", "setLevel completed");
                        System.out.println("DEBUG: setLevel completed, about to switch scene");
                    }
                    Gdx.app.postRunnable(() -> {
                        System.out.println("DEBUG: postRunnable executing, switching to " + nextSceneId);
                        Gdx.app.log("ScoreBoard", "About to setScene: " + nextSceneId);
                        sceneManager.setScene(nextSceneId);
                        Gdx.app.log("ScoreBoard", "setScene completed");
                    });
                }
            } catch (Exception e) {
                System.err.println("ERROR in SCOREBOARD_NEXT:");
                e.printStackTrace();
                Gdx.app.error("ScoreBoard", "Error in SCOREBOARD_NEXT", e);
            }
        });
        ioManager.registerEvent(GameEvents.SCOREBOARD_MENU, () -> {
            scoreManager.reset();
            Gdx.app.postRunnable(() -> sceneManager.setScene(SceneType.MAIN_MENU_SCENE.name()));
        });
    }

    @Override
    protected void onHide() {
        ioManager.clearEvent(GameEvents.SCOREBOARD_NEXT);
        ioManager.clearEvent(GameEvents.SCOREBOARD_MENU);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (skin != null) { skin.dispose(); skin = null; }
        nextButton = null;
        menuButton = null;
        Stage s = getStage();
        if (s != null) s.clear();
    }

    @Override
    public void update(float delta) {
        // no timer on scoreboard
    }

    @Override
    public void render(float delta) {
        clearScreen(0.05f, 0.05f, 0.15f, 1f);
        drawStageAndUI(delta);
    }

    @Override
    protected void onResize(int width, int height) {
        layoutButtons(width, height);
    }

    @Override
    protected void renderUI() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        // Fix 2: always start white
        font.setColor(Color.WHITE);

        String title = "=== SCOREBOARD ===";
        layout.setText(font, title);
        font.draw(batch, title, centerX - layout.width / 2f, centerY + 120f);

        String finalLine = "Final Score:  " + scoreManager.getFinalScore();
        layout.setText(font, finalLine);
        font.draw(batch, finalLine, centerX - layout.width / 2f, centerY + 75f);

        String highLine = "High Score:   " + scoreManager.getHighScore();
        layout.setText(font, highLine);
        font.draw(batch, highLine, centerX - layout.width / 2f, centerY + 50f);

        String chooseLine = "Choose your next action:";
        layout.setText(font, chooseLine);
        font.draw(batch, chooseLine, centerX - layout.width / 2f, centerY + 10f);

        font.setColor(Color.WHITE); // reset after
    }

    @Override
    public void dispose() {
        if (skin != null) { skin.dispose(); skin = null; }
        super.dispose();
    }

    private void layoutButtons(int width, int height) {
        if (nextButton == null || menuButton == null) {
            return;
        }
        float centerX = width / 2f;
        float centerY = height / 2f;
        float gap = 20f;
        float totalW = SceneButtonFactory.BUTTON_WIDTH * 2 + gap;
        float startX = centerX - totalW / 2f;
        float btnY = centerY - 100f;

        nextButton.setPosition(startX, btnY);
        menuButton.setPosition(startX + SceneButtonFactory.BUTTON_WIDTH + gap, btnY);
    }
}

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
import io.github.team3engine.engine.scoring.ScoreManager;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.ui.SceneButtonFactory;

public class ScoreBoardScene extends BaseScene {

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final int screenWidth;
    private final int screenHeight;

    private String nextSceneId;
    private Skin skin;

    public ScoreBoardScene(SpriteBatch batch, BitmapFont sharedFont,
                           SceneManager sceneManager, IOManager ioManager,
                           int screenWidth, int screenHeight) {
        super(batch);
        this.font = sharedFont;
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setNextScene(String sceneId) {
        this.nextSceneId = sceneId;
    }

    @Override
    protected void onShow() {
        super.onShow();
        // Fix 2: reset font colour on entry
        font.setColor(Color.WHITE);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        float centerX = screenWidth  / 2f;
        float centerY = screenHeight / 2f;

        // Fix 3: mirror GameOverScene button layout exactly
        float gap    = 20f;
        float totalW = SceneButtonFactory.BUTTON_WIDTH * 2 + gap;
        float startX = centerX - totalW / 2f;
        float btnY   = centerY - 100f;

        TextButton nextButton = SceneButtonFactory.create("Next Level", skin, () ->
                ioManager.broadcast(GameEvents.SCOREBOARD_NEXT));
        nextButton.setPosition(startX, btnY);

        TextButton menuButton = SceneButtonFactory.create("Main Menu", skin, () ->
                ioManager.broadcast(GameEvents.SCOREBOARD_MENU));
        menuButton.setPosition(startX + SceneButtonFactory.BUTTON_WIDTH + gap, btnY);

        getStage().addActor(nextButton);
        getStage().addActor(menuButton);

        ioManager.registerEvent(GameEvents.SCOREBOARD_NEXT, () -> {
            if (nextSceneId != null) {
                ScoreManager.getInstance().reset();
                Gdx.app.postRunnable(() -> sceneManager.setScene(nextSceneId));
            }
        });
        ioManager.registerEvent(GameEvents.SCOREBOARD_MENU, () -> {
            ScoreManager.getInstance().reset();
            Gdx.app.postRunnable(() -> sceneManager.setScene(SceneType.MAIN_MENU_SCENE.name()));
        });
    }

    @Override
    protected void onHide() {
        ioManager.clearEvent(GameEvents.SCOREBOARD_NEXT);
        ioManager.clearEvent(GameEvents.SCOREBOARD_MENU);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (skin != null) { skin.dispose(); skin = null; }
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
    protected void renderUI() {
        ScoreManager sm = ScoreManager.getInstance();
        float centerX = screenWidth  / 2f;
        float centerY = screenHeight / 2f;

        // Fix 2: always start white
        font.setColor(Color.WHITE);

        String title = "=== SCOREBOARD ===";
        layout.setText(font, title);
        font.draw(batch, title, centerX - layout.width / 2f, centerY + 120f);

        String finalLine = "Final Score:  " + sm.getFinalScore();
        layout.setText(font, finalLine);
        font.draw(batch, finalLine, centerX - layout.width / 2f, centerY + 75f);

        String highLine = "High Score:   " + sm.getHighScore();
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
}

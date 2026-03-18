package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.engine.scoring.ScoreManager;
import io.github.team3engine.game.events.GameEvents;

public class ScoreBoardScene extends BaseScene {

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final BitmapFont font;
    private final int screenWidth;
    private final int screenHeight;

    // The next scene to go to when player clicks Next Level
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

    // Call this before switching to ScoreBoardScene to set where Next Level goes
    public void setNextScene(String sceneId) {
        this.nextSceneId = sceneId;
    }

    @Override
    protected void onShow() {
        super.onShow(); // no timer for scoreboard
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        float centerX = screenWidth / 2f;
        float centerY = screenHeight / 2f;

        // Next Level button
        TextButton nextButton = new TextButton("Next Level", skin);
        nextButton.setSize(200, 50);
        nextButton.setPosition(centerX - 110f, centerY - 60f);

        nextButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    nextButton.setColor(0.7f, 0.7f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    nextButton.setColor(1f, 1f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ioManager.broadcast(GameEvents.SCOREBOARD_NEXT);
            }
        });

        // Main Menu button
        TextButton menuButton = new TextButton("Main Menu", skin);
        menuButton.setSize(200, 50);
        menuButton.setPosition(centerX + 20f, centerY - 60f);

        menuButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    menuButton.setColor(0.7f, 0.7f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    menuButton.setColor(1f, 1f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ioManager.broadcast(GameEvents.SCOREBOARD_MENU);
            }
        });

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
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        Stage s = getStage();
        if (s != null) s.clear();
    }

    @Override
    public void update(float delta) {
        // no super.update() — no timer on scoreboard
    }

    @Override
    public void render(float delta) {
        clearScreen(0.05f, 0.05f, 0.15f, 1f);
        drawStageAndUI(delta);
    }

    @Override
    protected void renderUI() {
        ScoreManager sm = ScoreManager.getInstance();
        float centerX = screenWidth / 2f;
        float centerY = screenHeight / 2f;

        font.draw(batch, "=== SCOREBOARD ===",         centerX - 90f, centerY + 120f);
        font.draw(batch, "Final Score:  " + sm.getFinalScore(),  centerX - 80f, centerY + 80f);
        font.draw(batch, "High Score:   " + sm.getHighScore(),   centerX - 80f, centerY + 55f);
        font.draw(batch, "Choose your next action:",   centerX - 90f, centerY + 10f);
    }

    @Override
    public void dispose() {
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        super.dispose();
    }
}

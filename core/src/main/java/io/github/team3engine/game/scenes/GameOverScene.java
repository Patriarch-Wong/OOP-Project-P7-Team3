package io.github.team3engine.game.scenes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.Cursor;

import io.github.team3engine.engine.scene.*;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.audio.AudioManager;

public class GameOverScene extends BaseScene {
    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final BitmapFont font;
    private final int screenWidth;
    private final int screenHeight;

    private SceneType retryLevel;
    private Skin skin;

    public GameOverScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager, IOManager ioManager, AudioManager audioManager, int screenWidth, int screenHeight, SceneType retryLevel) {
        super(batch);
        this.font = sharedFont;
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.retryLevel = retryLevel;
    }

    @Override
    public void onShow() {
        super.onShow();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        float centerX = screenWidth / 2f;
        float centerY = screenHeight / 2f;

        // Retry button
        TextButton retryButton = new TextButton("Retry", skin);
        retryButton.setSize(200, 50);
        retryButton.setPosition(centerX - 210f, centerY - 100f);

        retryButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    retryButton.setColor(0.7f, 0.7f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    retryButton.setColor(1f, 1f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene(retryLevel.name());
            }
        });

        // Main Menu button
        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.setSize(200, 50);
        mainMenuButton.setPosition(centerX + 10f, centerY - 100f);

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    mainMenuButton.setColor(0.7f, 0.7f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    mainMenuButton.setColor(1f, 1f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene(SceneType.MAIN_MENU_SCENE.name());
            }
        });

        getStage().addActor(retryButton);
        getStage().addActor(mainMenuButton);
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render(float delta) {
        clearScreen(0.2f, 0f, 0f, 1f);
        drawStageAndUI(delta);
    }

    @Override
    protected void renderUI() {
        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER", screenWidth / 2 - 120f, screenHeight / 2 + 60f);
    }

    @Override
    public void onHide() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        Stage s = getStage();
        if (s != null) {
            s.clear();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

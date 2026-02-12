package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.Cursor;

import io.github.team3engine.engine.scene.*;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.audio.AudioManager;

public class MainMenuScene extends BaseScene {
    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;

    // UI
    private Skin skin;

    public MainMenuScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager, IOManager ioManager, AudioManager audioManager) {
        super(batch, sharedFont);
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
    }

    @Override
    public void onShow() {
        super.onShow();
        // Load default skin
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // region Create button
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.setSize(200, 50);
        startButton.setPosition((Gdx.graphics.getWidth() - startButton.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - startButton.getHeight()) / 2f);

        // Button action
        startButton.addListener(new ClickListener() {
            @Override // hover enter
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // pointer == -1 means mouse, not touch
                System.out.println("Hovering over Start Game button");
                if (pointer == -1) {
                    startButton.setColor(0.7f, 0.7f, 1f, 1f); // light blue on hover
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }

            @Override // hover exit
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    startButton.setColor(1f, 1f, 1f, 1f); // reset to normal
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                ioManager.broadcast("START_GAME");
            }
        });
        getStage().addActor(startButton);
        // endregion

        // register events
        ioManager.registerEvent("START_GAME", () -> sceneManager.setScene(SceneType.SCENE_1.name()));
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render(float delta) {
        clearScreen(0.15f, 0.15f, 0.4f, 1f);
        drawStageAndUI(delta);
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "Main Menu", 285, 300);
    }

    @Override
    public void onHide() {
        ioManager.clearEvent("START_GAME");
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
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        super.dispose();
    }
}

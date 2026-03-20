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
import io.github.team3engine.game.events.GameEvents;

public class MainMenuScene extends BaseScene {
    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final BitmapFont font;
    private final int screenWidth;
    private final int screenHeight;

    // UI
    private Skin skin;

    public MainMenuScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager, IOManager ioManager, AudioManager audioManager, int screenWidth, int screenHeight) {
        super(batch);
        this.font = sharedFont;
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    protected void onShow() {
        super.onShow();
        // Load default skin
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // region Create button
        TextButton scene1Button = new TextButton("Scene 1", skin);
        scene1Button.setSize(200, 50);
        scene1Button.setPosition((screenWidth - scene1Button.getWidth()) / 2f,
            (screenHeight - scene1Button.getHeight()) / 2f);

        
        TextButton testSceneButton = new TextButton("Test Scene", skin);
        testSceneButton.setSize(200, 50);
        testSceneButton.setPosition((screenWidth - testSceneButton.getWidth()) / 2f,
            (screenHeight - scene1Button.getHeight() - testSceneButton.getHeight()) / 3f);


        // Button action
        scene1Button.addListener(new ClickListener() {
            @Override // hover enter
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // pointer == -1 means mouse, not touch
                if (pointer == -1) {
                    scene1Button.setColor(0.7f, 0.7f, 1f, 1f); // light blue on hover
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }

            @Override // hover exit
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    scene1Button.setColor(1f, 1f, 1f, 1f); // reset to normal
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                ioManager.broadcast(GameEvents.START_GAME);
            }
        });
        getStage().addActor(scene1Button);

        // Button action
        testSceneButton.addListener(new ClickListener() {
            @Override // hover enter
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // pointer == -1 means mouse, not touch
                if (pointer == -1) {
                    testSceneButton.setColor(0.7f, 0.7f, 1f, 1f); // light blue on hover
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }

            @Override // hover exit
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    testSceneButton.setColor(1f, 1f, 1f, 1f); // reset to normal
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                ioManager.broadcast(GameEvents.START_GAME_TEST);
            }
        });
        getStage().addActor(testSceneButton);
        // endregion

        // register events
        ioManager.registerEvent(GameEvents.START_GAME, () -> {
            Gdx.app.log("Game", "Starting game - Scene 1");
            sceneManager.setScene(SceneType.SCENE_1.name());
        });
        ioManager.registerEvent(GameEvents.START_GAME_TEST, () -> {
            Gdx.app.log("Game", "Starting game - Test Scene");
            sceneManager.setScene(SceneType.TEST_SCENE.name());
        });
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
        ioManager.clearEvent(GameEvents.START_GAME);
        ioManager.clearEvent(GameEvents.START_GAME_TEST);
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

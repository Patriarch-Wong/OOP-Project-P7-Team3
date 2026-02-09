package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.UIManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.engine.scene.SceneType;
import io.github.team3engine.game.scenes.*;

/**
 * Entry point: initializes the engine and uses SceneManager to swap and render
 * the current scene.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private boolean isPaused = false;

    private AudioManager audioManager;
    private SceneManager sceneManager;
    private UIManager uiManager;
    private IOManager ioManager;

    // local variables
    private static final float FOOTSTEP_INTERVAL = 0.4f;
    private float footstepTimer = 0;
    private boolean wasMoving = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        audioManager = new AudioManager();
        ioManager = new IOManager();
        sceneManager = SceneManager.getInstance();

        uiManager = new UIManager(audioManager);

        // Register scenes (game-specific); generic SceneManager only holds by id.
        sceneManager.registerScene(SceneType.MAIN_MENU_SCENE.name(),
                new MainMenuScene(batch, sceneManager, ioManager, audioManager));
        sceneManager.registerScene(SceneType.SCENE_1.name(), new Scene1(batch, sceneManager, ioManager, audioManager));
        sceneManager.registerScene(SceneType.SCENE_2.name(), new Scene2(batch, sceneManager, ioManager, audioManager));
        sceneManager.setScene(SceneType.MAIN_MENU_SCENE.name());

        // Win condition: switch between Scene1 and Scene2. Defer to end of frame to
        // avoid changing scene mid-render.
        ioManager.registerEvent("PLAYER_WIN", () -> {
            audioManager.play("victory.mp3");
            String next = SceneType.SCENE_1.name().equals(sceneManager.getCurrentSceneId())
                    ? SceneType.SCENE_2.name()
                    : SceneType.SCENE_1.name();
            Gdx.app.postRunnable(() -> sceneManager.setScene(next));
            System.out.println("Player won! Switching to " + next);
        });

        ioManager.registerEvent("PLAYER_MOVING", () -> {
            float dt = Gdx.graphics.getDeltaTime();
            footstepTimer += dt;
            if (!wasMoving) {
                footstepTimer = FOOTSTEP_INTERVAL;
                wasMoving = true;
            }
            if (footstepTimer >= FOOTSTEP_INTERVAL) {
                audioManager.play("walk.mp3");
                footstepTimer = 0f;
            }
        });

        ioManager.registerEvent("PLAYER_JUMP", () -> audioManager.play("jump.mp3"));

        ioManager.registerEvent("GAME_PAUSE", () -> {
            isPaused = true;
            sceneManager.setPaused(isPaused);
            uiManager.toggleMenu(isPaused);
            ioManager.setActive(!isPaused);
        });

        ioManager.registerEvent("GAME_UNPAUSE", () -> {
            isPaused = false;
            sceneManager.setPaused(isPaused);
            uiManager.toggleMenu(isPaused);
            ioManager.setActive(!isPaused);
        });
        // endregion

        audioManager.loadGameSounds();
        audioManager.playMusic("title.mp3", true);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (sceneManager.getCurrentSceneId() != SceneType.MAIN_MENU_SCENE.name()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                if (isPaused) {
                    ioManager.broadcast("GAME_UNPAUSE");
                } else {
                    ioManager.broadcast("GAME_PAUSE");
                }
                return;
            }
        }
        if (sceneManager.getCurrentScene() != null) {
            sceneManager.getCurrentScene().render(deltaTime);
        }

        if (isPaused) {
            uiManager.update(deltaTime);
            uiManager.draw();
        }
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        if (batch != null)
            batch.dispose();
        if (uiManager != null)
            uiManager.dispose();
        if (sceneManager != null)
            sceneManager.disposeAll();
        if (audioManager != null)
            audioManager.dispose();
        if (ioManager != null)
            ioManager.dispose();
    }
}

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
import io.github.team3engine.game.scenes.Scene1;
import io.github.team3engine.game.scenes.Scene2;

/**
 * Entry point: initializes the engine and uses SceneManager to swap and render the current scene.
 */
public class Main extends ApplicationAdapter {
    private GameEngine gameEngine;
    private SpriteBatch batch;
    private UIManager uiManager;
    private boolean isPaused = false;

    private static final float FOOTSTEP_INTERVAL = 0.4f;
    private float footstepTimer = 0;
    private boolean wasMoving = false;

    @Override
    public void create() {
        gameEngine = new GameEngine();
        gameEngine.init();

        batch = new SpriteBatch();
        AudioManager audioManager = gameEngine.getAudioManager();
        IOManager ioManager = gameEngine.getIOManager();
        SceneManager sceneManager = gameEngine.getSceneManager();

        uiManager = new UIManager(audioManager);

        // Register scenes (game-specific); generic SceneManager only holds by id.
        sceneManager.registerScene(SceneType.SCENE_1.name(), new Scene1(gameEngine, batch));
        sceneManager.registerScene(SceneType.SCENE_2.name(), new Scene2(gameEngine, batch));
        sceneManager.setScene(SceneType.SCENE_1.name());

        // Win condition: switch between Scene1 and Scene2. Defer to end of frame to avoid changing scene mid-render.
        ioManager.registerEvent("PLAYER_WIN", () -> {
            String next = SceneType.SCENE_1.name().equals(sceneManager.getCurrentSceneId())
                ? SceneType.SCENE_2.name()
                : SceneType.SCENE_1.name();
            Gdx.app.postRunnable(() -> sceneManager.setScene(next));
        });

        Gdx.input.setInputProcessor(ioManager);

        audioManager.loadGameSounds();
        audioManager.playMusic("title.mp3", true);

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
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        SceneManager sceneManager = gameEngine.getSceneManager();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            sceneManager.setPaused(isPaused);
            uiManager.toggleMenu(isPaused);
            if (!isPaused) {
                Gdx.input.setInputProcessor(gameEngine.getIOManager());
            }
            return;
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
        if (gameEngine != null) gameEngine.dispose();
        if (batch != null) batch.dispose();
        if (uiManager != null) uiManager.dispose();
    }
}

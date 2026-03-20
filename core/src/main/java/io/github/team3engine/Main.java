package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.team3engine.engine.UIManager;
import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.scenes.*;
import io.github.team3engine.game.scenes.demo.Scene1;
import io.github.team3engine.engine.interfaces.ScoreRule;
import io.github.team3engine.game.score.NpcRescueRule;
import io.github.team3engine.game.score.ObjectiveRule;
import io.github.team3engine.game.score.TimeBonusRule;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes the GameEngine and uses its managers to register
 * scenes, wire game-specific events, and run the loop.
 */
public class Main extends ApplicationAdapter {
    private GameEngine engine;
    private SpriteBatch batch;
    private BitmapFont sharedFont;
    private UIManager uiManager;
    private boolean isPaused = false;
<<<<<<< HEAD
    private int currentLevel = 1;
    private TestScene testScene;
=======
    // Keep gameplay rendering anchored to the original startup resolution.
    private int fixedViewportWidth;
    private int fixedViewportHeight;
>>>>>>> a70e28e5d09ddb96a0cdfd6b0026c8df43756d93

    private static final float FOOTSTEP_INTERVAL = 0.4f;
    private float footstepTimer = 0;
    private boolean wasMoving = false;

    @Override
    public void create() {
        engine = new GameEngine();
        engine.init();

        batch = new SpriteBatch();
        sharedFont = new BitmapFont();
        SceneManager sceneManager = engine.getSceneManager();
        IOManager ioManager = engine.getIOManager();
        AudioManager audioManager = engine.getAudioManager();
        EntityManager entityManager = engine.getEntityManager();
        CollisionManager collisionManager = engine.getCollisionManager();
        MovementManager movementManager = engine.getMovementManager();

        uiManager = new UIManager(audioManager);

        // Store width and height once
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        fixedViewportWidth = screenWidth;
        fixedViewportHeight = screenHeight;

        // Register scenes
        sceneManager.registerScene(SceneType.MAIN_MENU_SCENE.name(),
                new MainMenuScene(batch, sharedFont, sceneManager, ioManager, audioManager));
        List<ScoreRule> testSceneRules = Arrays.asList(
                new ObjectiveRule(),
                new NpcRescueRule(),
                new TimeBonusRule()
        );
        testScene = new TestScene(batch, sharedFont, sceneManager, ioManager, audioManager, entityManager, collisionManager,
                movementManager, screenWidth, screenHeight, testSceneRules, currentLevel);
        sceneManager.registerScene(SceneType.TEST_SCENE.name(), testScene);
        sceneManager.registerScene(SceneType.SCENE_1.name(),
                new Scene1(batch, sharedFont, sceneManager, ioManager, audioManager, entityManager, collisionManager,
                        movementManager, screenWidth, screenHeight));

        ScoreBoardScene scoreBoardScene = new ScoreBoardScene(batch, sharedFont, sceneManager, ioManager);
        sceneManager.registerScene(SceneType.SCORE_BOARD.name(), scoreBoardScene);
        GameOverScene gameOverScene = new GameOverScene(batch, sharedFont, sceneManager, SceneType.TEST_SCENE.name());
        sceneManager.registerScene(SceneType.GAME_OVER.name(), gameOverScene);

        sceneManager.setScene(SceneType.MAIN_MENU_SCENE.name());

        // global events reguster here
        ioManager.registerEvent(GameEvents.PLAYER_MOVING, () -> {
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
        ioManager.registerEvent(GameEvents.PLAYER_JUMP, () -> audioManager.play("jump.mp3"));
        ioManager.registerEvent(GameEvents.PLAYER_DEAD, () -> {
            BaseScene currentScene = sceneManager.getCurrentScene();
            String currentSceneId = sceneManager.getCurrentSceneId();
            if (SceneType.GAME_OVER.name().equals(currentSceneId)) {
                return;
            }
            if (isGameplayScene(currentScene) && currentSceneId != null) {
                gameOverScene.setRetryScene(currentSceneId);
                gameOverScene.setRetryLevel(currentLevel);
            }
            Gdx.app.log("Game", "Player died!");
            Gdx.app.postRunnable(() -> sceneManager.setScene(SceneType.GAME_OVER.name()));
        });
        ioManager.registerEvent(GameEvents.GAME_PAUSE, () -> {
            Gdx.app.log("Game", "Game paused");
            isPaused = true;
            uiManager.toggleMenu(true);
            ioManager.setActive(false);
        });
        ioManager.registerEvent(GameEvents.GAME_UNPAUSE, () -> {
            Gdx.app.log("Game", "Game resumed");
            isPaused = false;
            uiManager.toggleMenu(false);
            ioManager.setActive(true);
            // Restore the current scene's input processor
            if (sceneManager.getCurrentScene() != null) {
                Gdx.input.setInputProcessor(sceneManager.getCurrentScene().getInputProcessor());
            }
        });

        audioManager.preload("walk.mp3", "jump.mp3", "collide.mp3", "victory.mp3", "bullet_hit.mp3");
        audioManager.playMusic("title.mp3", true);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        SceneManager sceneManager = engine.getSceneManager();
        IOManager ioManager = engine.getIOManager();
        BaseScene currentScene = sceneManager.getCurrentScene();

        if (isGameplayScene(currentScene)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                if (isPaused) {
                    ioManager.broadcast(GameEvents.GAME_UNPAUSE);
                } else {
                    ioManager.broadcast(GameEvents.GAME_PAUSE);
                }
                return;
            }
        }

        if (!isPaused) {
            engine.update(deltaTime);
        }
        if (batch != null && fixedViewportWidth > 0 && fixedViewportHeight > 0) {
            batch.getProjectionMatrix().setToOrtho2D(0, 0, fixedViewportWidth, fixedViewportHeight);
        }
        engine.render(isPaused ? 0 : deltaTime);
        if (isPaused) {
            uiManager.update(deltaTime);
            uiManager.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (engine != null && engine.getSceneManager() != null) {
            engine.getSceneManager().resize(width, height);
        }
        if (uiManager != null) {
            uiManager.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        if (batch != null)
            batch.dispose();
        if (sharedFont != null)
            sharedFont.dispose();
        if (uiManager != null)
            uiManager.dispose();
        if (engine != null)
            engine.dispose();
    }

    private boolean isGameplayScene(BaseScene scene) {
        return scene instanceof GameplayScene;
    }
}

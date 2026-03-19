package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import io.github.team3engine.engine.UIManager;
import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.collision.CollisionMediator;
import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.interfaces.Solid;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.scenes.*;

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

        // Register scenes
        sceneManager.registerScene(SceneType.MAIN_MENU_SCENE.name(),
                new MainMenuScene(batch, sharedFont, sceneManager, ioManager, audioManager, screenWidth, screenHeight));
        sceneManager.registerScene(SceneType.TEST_SCENE.name(),
                new TestScene(batch, sharedFont, sceneManager, ioManager, audioManager, entityManager, collisionManager,
                        movementManager, screenWidth, screenHeight));
        sceneManager.registerScene(SceneType.SCENE_1.name(),
                new Scene1(batch, sharedFont, sceneManager, ioManager, audioManager, entityManager, collisionManager,
                        movementManager, screenWidth, screenHeight));

        ScoreBoardScene scoreBoardScene = new ScoreBoardScene(batch, sharedFont, sceneManager, ioManager, screenWidth,
                screenHeight);
        sceneManager.registerScene(SceneType.SCORE_BOARD.name(), scoreBoardScene);
        sceneManager.registerScene(SceneType.GAME_OVER.name(),
                new GameOverScene(batch, sharedFont, sceneManager, ioManager, audioManager, screenWidth, screenHeight, SceneType.TEST_SCENE));

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
            Gdx.app.log("Game", "Player died!");
            // Show Game Over screen
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

        if (!SceneType.MAIN_MENU_SCENE.name().equals(sceneManager.getCurrentSceneId())) {
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
        engine.render(isPaused ? 0 : deltaTime);
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
        if (sharedFont != null)
            sharedFont.dispose();
        if (uiManager != null)
            uiManager.dispose();
        if (engine != null)
            engine.dispose();
    }
}

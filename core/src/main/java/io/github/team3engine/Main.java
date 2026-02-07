package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.collision.CollisionManager;
import io.github.team3engine.entity.*;
import io.github.team3engine.io.IOManager;
import io.github.team3engine.io.PlayerInput;
import io.github.team3engine.scene.SceneManager;
import io.github.team3engine.scene.ScreenType;

/**
 * Main scene: holds creation of the world and runs the game loop using the engine's managers.
 */
public class Main extends ApplicationAdapter {
    private GameEngine gameEngine;

    private SpriteBatch batch;
    private Texture image;
    private UIManager uiManager;
    private Circle player;
    private MovementInput movementInput;
    private PlayerInput playerInput;
    private boolean isPaused = false;

    private float footstepTimer = 0;
    private static final float FOOTSTEP_INTERVAL = 0.4f;
    private boolean wasMoving = false;

    private static final float MAX_DELTA = 0.1f;

    @Override
    public void create() {
        gameEngine = new GameEngine();
        gameEngine.init();

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        IOManager ioManager = gameEngine.getIOManager();
        AudioManager audioManager = gameEngine.getAudioManager();
        EntityManager entityManager = gameEngine.getEntityManager();
        CollisionManager collisionManager = gameEngine.getCollisionManager();
        SceneManager sceneManager = gameEngine.getSceneManager();

        playerInput = new PlayerInput();
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);

        audioManager.loadGameSounds();
        audioManager.playMusic("title.mp3", true);

        movementInput = new MovementInput();

        player = new Circle("player_circle", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 30f,
                playerInput, ioManager);
        entityManager.addEntity(player);

        Bucket bucket = new Bucket("bucket", Gdx.graphics.getWidth() / 2f, 20f);
        entityManager.addEntity(bucket);

        float bulletX = Gdx.graphics.getWidth() * 0.5f;
        float bulletY = Gdx.graphics.getHeight() * 0.75f;
        Bullet singleBullet = new Bullet("bullet_single", bulletX, bulletY, null, audioManager);
        singleBullet.setVelocity(0f, 0f);
        entityManager.addEntity(singleBullet);

        float gw = Gdx.graphics.getWidth();
        float gh = Gdx.graphics.getHeight();
        float scaleX = gw / 19f;
        float scaleY = gh / 12f;
        Texture platformTex = new Texture(Gdx.files.internal("platform.png"));

        Platform p1 = new Platform("platform_1", 1f * scaleX, 1f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p1);
        Platform p2 = new Platform("platform_2", 6f * scaleX, 5f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p2);
        Platform p3h = new Platform("platform_3_h", 11f * scaleX, 9f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        Platform p3v = new Platform("platform_3_v", 11f * scaleX, 8f * scaleY, 1f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p3h);
        entityManager.addEntity(p3v);

        collisionManager.register(player);
        collisionManager.register(bucket);
        collisionManager.register(singleBullet);
        collisionManager.register(p1);
        collisionManager.register(p2);
        collisionManager.register(p3h);
        collisionManager.register(p3v);

        WinBox winBox = new WinBox("win_box", 50f, ioManager, audioManager);
        entityManager.addEntity(winBox);
        collisionManager.register(winBox);

        uiManager = new UIManager(audioManager);
        sceneManager.init(batch);

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

        ioManager.registerEvent("PLAYER_WIN", () -> {
            sceneManager.setScreen(ScreenType.WIN_SCREEN);
            System.out.println("u win");
        });
    }

    @Override
    public void render() {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), MAX_DELTA);
        IOManager ioManager = gameEngine.getIOManager();
        SceneManager sceneManager = gameEngine.getSceneManager();
        EntityManager entityManager = gameEngine.getEntityManager();
        MovementManager movementManager = gameEngine.getMovementManager();
        CollisionManager collisionManager = gameEngine.getCollisionManager();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            uiManager.toggleMenu(isPaused);
            if (isPaused) {
                sceneManager.setScreen(ScreenType.PAUSE_SCREEN);
            } else {
                sceneManager.setScreen(ScreenType.SCREEN_1);
                Gdx.input.setInputProcessor(ioManager);
            }
            return;
        }

        if (sceneManager.getCurrentScreenType() == ScreenType.PAUSE_SCREEN) {
            ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
            batch.begin();
            batch.draw(image, 140, 210);
            entityManager.renderAll(batch);
            batch.end();
            uiManager.update(deltaTime);
            uiManager.draw();
            return;
        }

        if (!isPaused) {
            movementInput.update();
            movementManager.applyMovement(player, movementInput, deltaTime);
            entityManager.updateAll(deltaTime);
            collisionManager.update(deltaTime);
            Array<CollidableEntity[]> collisionPairs = collisionManager.resolveCollisions();

            for (CollidableEntity[] pair : collisionPairs) {
                if (pair == null || pair.length < 2) continue;
                CollidableEntity a = pair[0], b = pair[1];
                if (a != player && b != player) continue;
                CollidableEntity other = (a == player) ? b : a;
                if (!(other instanceof Platform)) continue;
                if (movementManager.isMovingUpward() && !movementManager.hasHorizontalMotion()) {
                    movementManager.cancelUpwardVelocity();
                    break;
                }
            }

            boolean isOnFloor = player.getPos().y <= player.getRadius() + 1f;
            boolean isOnPlatform = false;
            float circleBottom = player.getPos().y - player.getRadius();
            float sinkTolerance = 3f;

            for (Entity e : entityManager.getAll()) {
                if (e instanceof Platform) {
                    Platform platform = (Platform) e;
                    float platformTop = platform.getPos().y + platform.getHeight();
                    float platformLeft = platform.getPos().x;
                    float platformRight = platform.getPos().x + platform.getWidth();
                    float circleCenterX = player.getPos().x;
                    boolean landed = circleBottom <= platformTop + sinkTolerance && circleBottom >= platformTop - sinkTolerance;
                    boolean overPlatform = circleCenterX >= platformLeft - player.getRadius() && circleCenterX <= platformRight + player.getRadius();
                    if (landed && overPlatform) {
                        isOnPlatform = true;
                        break;
                    }
                }
            }

            if (isOnFloor || isOnPlatform) {
                movementManager.setGrounded(true);
            } else {
                movementManager.setGrounded(false);
            }

            if (touchesCeiling(player, entityManager)) {
                movementManager.hitCeiling();
            }
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        entityManager.renderAll(batch);
        batch.end();
        uiManager.update(deltaTime);
        uiManager.draw();
    }

    private boolean touchesCeiling(Circle player, EntityManager entityManager) {
        float circleTop = player.getPos().y + player.getRadius();
        float tolerance = 6f;
        for (Entity e : entityManager.getAll()) {
            if (e instanceof Platform) {
                Platform platform = (Platform) e;
                float platformBottom = platform.getPos().y;
                float platformLeft = platform.getPos().x;
                float platformRight = platform.getPos().x + platform.getWidth();
                float circleX = player.getPos().x;
                boolean underPlatform = circleTop >= platformBottom - tolerance && circleTop <= platformBottom + tolerance;
                boolean horizontallyAligned = circleX >= platformLeft - player.getRadius() && circleX <= platformRight + player.getRadius();
                if (underPlatform && horizontallyAligned) return true;
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (image != null) image.dispose();
        if (uiManager != null) uiManager.dispose();
        if (gameEngine != null) gameEngine.dispose();
    }
}

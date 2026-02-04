package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.collision.CollisionManager;
import io.github.team3engine.entity.*;
import io.github.team3engine.io.IOManager;
import io.github.team3engine.io.PlayerInput;

public class Main extends ApplicationAdapter {
    // Managers
    private AudioManager audioManager;
    private UIManager uiManager;
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private MovementManager movementManager;
    private IOManager ioManager;
    private PlayerInput playerInput;

    // Input & State
    private MovementInput movementInput;
    private SpriteBatch batch;
    private Texture image;
    private boolean isPaused = false;

    private Circle player;

    // Footstep logic
    private float footstepTimer = 0;
    private final float FOOTSTEP_INTERVAL = 0.4f;
    private boolean wasMoving = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        // IO Manager
        ioManager = new IOManager();
        playerInput = new PlayerInput();
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);

        // 1. Audio Setup
        audioManager = new AudioManager();
        audioManager.findClip("walk.mp3");
        audioManager.findClip("jump.mp3");
        audioManager.findClip("collide.mp3");
        audioManager.findClip("victory.mp3");
        audioManager.playMusic("title.mp3", true);

        // 2. Entity & Movement Setup
        entityManager = new EntityManager();
        movementInput = new MovementInput();
        movementManager = new MovementManager(audioManager); // Manager handles sfx!

        player = new Circle("player_circle", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 30f,
                playerInput, ioManager);
        entityManager.addEntity(player);

        Bucket bucket = new Bucket("bucket", Gdx.graphics.getWidth() / 2f, 20f);
        entityManager.addEntity(bucket);

        // Initialize Bullet
        float bulletX = Gdx.graphics.getWidth() * 0.5f;
        float bulletY = Gdx.graphics.getHeight() * 0.75f;
        Bullet singleBullet = new Bullet("bullet_single", bulletX, bulletY, null, audioManager);
        singleBullet.setVelocity(0f, 0f);
        entityManager.addEntity(singleBullet);

        // Initialize platforms from layout (19x12 grid; scale to screen pixels)
        float gw = Gdx.graphics.getWidth();
        float gh = Gdx.graphics.getHeight();
        float scaleX = gw / 19f;
        float scaleY = gh / 12f;
        Texture platformTex = new Texture(Gdx.files.internal("platform.png"));

        // Platform 1: bottom-left (grid 1,1 size 8x1)
        Platform p1 = new Platform("platform_1", 1f * scaleX, 1f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p1);

        // Platform 2: middle (grid 6,5 size 8x1)
        Platform p2 = new Platform("platform_2", 6f * scaleX, 5f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p2);

        // Platform 3: top-right L = horizontal (11,9 size 8x1) + vertical (11,8 size
        // 1x1)
        Platform p3h = new Platform("platform_3_h", 11f * scaleX, 9f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        Platform p3v = new Platform("platform_3_v", 11f * scaleX, 8f * scaleY, 1f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p3h);
        entityManager.addEntity(p3v);

        // 3. Collision Setup
        collisionManager = new CollisionManager();
        collisionManager.setAudioManager(audioManager);
        collisionManager.register(player);
        collisionManager.register(bucket);
        collisionManager.register(singleBullet);
        collisionManager.register(p1);
        collisionManager.register(p2);
        collisionManager.register(p3h);
        collisionManager.register(p3v);

        // Initialize WinBox
        WinBox winBox = new WinBox("win_box", 50f, ioManager, audioManager);
        entityManager.addEntity(winBox);
        collisionManager.register(winBox);

        // 4. UI Setup
        uiManager = new UIManager(audioManager);

        // setup eventlisteners (output)

        ioManager.registerEvent("PLAYER_MOVING", () -> { // smthcan happen here
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
            // scenemanager change scenes
            System.out.println("u win");
        });
    }

    /**
     * Max delta per frame to avoid huge physics steps (e.g. after alt-tab) and
     * tunneling through platforms.
     */
    private static final float MAX_DELTA = 0.1f;

    @Override
    public void render() {
        float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), MAX_DELTA);

        // Handle Pause Toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            uiManager.toggleMenu(isPaused);
        }

        // Logic Update
        if (!isPaused) {

            movementInput.update(); // Poll keys
            movementManager.applyMovement(player, movementInput, deltaTime); // Move & Play SFX

            entityManager.updateAll(deltaTime);

            collisionManager.update(deltaTime);
            collisionManager.resolveCollisions();

            // Ground detection after resolve: only set grounded when circle has actually landed
            // (bottom at or just below platform top), not when still above — avoids slowing down in mid-air.
            boolean isOnFloor = player.getPos().y <= player.getRadius() + 1f;
            boolean isOnPlatform = false;
            float circleBottom = player.getPos().y - player.getRadius();
            float sinkTolerance = 3f; // only "on platform" when at or just below surface, not above

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
            boolean hitCeiling = touchesCeiling(player, entityManager);

            if (hitCeiling) {
                movementManager.hitCeiling(); // or setVelocityY(0)
            }

            // System.out.println("IS GROUNDED: " + movementManager.getGrounded());
        }

        // Rendering
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        entityManager.renderAll(batch);
        batch.end();

        // UI is always drawn last so it's on top
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

                boolean underPlatform = circleTop >= platformBottom - tolerance &&
                        circleTop <= platformBottom + tolerance;

                boolean horizontallyAligned = circleX >= platformLeft - player.getRadius() &&
                        circleX <= platformRight + player.getRadius();

                if (underPlatform && horizontallyAligned) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        entityManager.disposeAll();
        uiManager.dispose();
    }
}

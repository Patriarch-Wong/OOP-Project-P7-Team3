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
        audioManager.playMusic("title.mp3", true);

        // 2. Entity & Movement Setup
        entityManager = new EntityManager();
        movementInput = new MovementInput();
        movementManager = new MovementManager(audioManager); // Manager handles sfx!

        player = new Circle("player_circle", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 30f, playerInput, ioManager);
        entityManager.addEntity(player);

        Bucket bucket = new Bucket("bucket", Gdx.graphics.getWidth() / 2f, 20f);
        entityManager.addEntity(bucket);

        //Initialize Bullet
        float bulletX = Gdx.graphics.getWidth() * 0.5f;
        float bulletY = Gdx.graphics.getHeight() * 0.75f;
        Bullet singleBullet = new Bullet("bullet_single", bulletX, bulletY, null, audioManager);
        singleBullet.setVelocity(0f, 0f);
        entityManager.addEntity(singleBullet);

        //Initialize Platform
        Texture platformTex = new Texture(Gdx.files.internal("platform.png"));
        Platform p = new Platform("platform_1", 100f, 150f, 300f, 24f, platformTex);
        entityManager.addEntity(p);

        // 3. Collision Setup
        collisionManager = new CollisionManager();
        collisionManager.setAudioManager(audioManager);
        collisionManager.register(player);
        collisionManager.register(bucket);
        collisionManager.register(singleBullet);
        collisionManager.register(p);

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
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

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

            // Ground detection: player landed when at bottom of screen (Circle clamps y >= radius)
            if (player.getPos().y <= player.getRadius() + 1f) {
                movementManager.setGrounded(true);
            }

            collisionManager.update(deltaTime);
            collisionManager.resolveCollisions();
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

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        entityManager.disposeAll();
        uiManager.dispose();
    }
}

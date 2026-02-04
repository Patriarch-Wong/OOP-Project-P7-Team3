package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.collision.CollisionManager;
import io.github.team3engine.entity.Bucket;
import io.github.team3engine.entity.Circle;
import io.github.team3engine.entity.EntityManager;
import io.github.team3engine.io.IOManager;
import io.github.team3engine.io.PlayerInput;
import io.github.team3engine.UIManager;

public class Main extends ApplicationAdapter {
    // Managers
    private AudioManager audioManager;
    private UIManager uiManager;
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private IOManager ioManager;
    private PlayerInput playerInput;

    // Game Assets/State
    private SpriteBatch batch;
    private Texture image;
    private boolean isPaused = false;
    private Circle circle;

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
        playerInput = new PlayerInput(circle);
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);

        // 1. Initialize Audio
        audioManager = new AudioManager();
        audioManager.findClip("walk.mp3");
        audioManager.findClip("jump.mp3");
        audioManager.findClip("collide.mp3");
        audioManager.setMusicVolume(0.05f);
        audioManager.setSFXVolume(0.5f);
        audioManager.playMusic("title.mp3", true);

        // 2. Initialize Entities
        entityManager = new EntityManager();
        float cx = Gdx.graphics.getWidth() * 0.5f;
        float cy = Gdx.graphics.getHeight() * 0.5f;
        circle = new Circle("player_circle", cx, cy, 30f, playerInput, ioManager);
        circle.setColor(0.2f, 0.6f, 1f, 1f);
        entityManager.addEntity(circle);

        Bucket bucket = new Bucket("bucket", 0, 20f);
        bucket.setPos(Gdx.graphics.getWidth() * 0.5f - bucket.getWidth() * 0.5f, 20f);
        entityManager.addEntity(bucket);

        // 3. Initialize Collisions
        collisionManager = new CollisionManager();
        collisionManager.setAudioManager(audioManager);
        collisionManager.register(circle);
        collisionManager.register(bucket);

        // 4. Initialize UI (Passing the audioManager so they can communicate)
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

        // 1. Handle Input for Pausing
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            uiManager.toggleMenu(isPaused); // Handles visibility and InputProcessor
        }

        // 2. Update simulation when not paused
        if (!isPaused) {
            entityManager.updateAll(deltaTime);
            collisionManager.resolveCollisions();
            handleGameInput(deltaTime);
        }

        // 3. Clear Screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // 4. Draw Game World (Always visible)
        batch.begin();
        batch.draw(image, 140, 210);
        entityManager.renderAll(batch);
        batch.end();

        // 5. Update and Draw UI (Drawn last to be on top)
        uiManager.update(deltaTime);
        uiManager.draw();
    }

    private void handleGameInput(float deltaTime) {
        // SFX test
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            audioManager.play("test_sfx.mp3");
        }

        // Jump
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            audioManager.play("jump.mp3");
        }

        // Collision test
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            audioManager.play("collide.mp3");
        }

        // Movement / Footsteps
        // if (Gdx.input.isKeyPressed(Input.Keys.A) ||
        // Gdx.input.isKeyPressed(Input.Keys.D)) {
        // footstepTimer += deltaTime;
        // if (footstepTimer >= FOOTSTEP_INTERVAL) {
        // audioManager.play("walk.mp3");
        // footstepTimer = 0;
        // }
        // } else {
        // footstepTimer = FOOTSTEP_INTERVAL;
        // }
    }

    @Override
    public void dispose() {
        entityManager.disposeAll();
        batch.dispose();
        image.dispose();
        uiManager.dispose(); // Clean up stage and skin
        // If your AudioManager has a dispose, call it here too
    }
}
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

        // 1. IO System Setup
        ioManager = new IOManager();
        playerInput = new PlayerInput(); // Fixed: Removed 'player' from constructor
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);

        // 2. Audio Setup
        audioManager = new AudioManager();
        audioManager.findClip("walk.mp3");
        audioManager.findClip("jump.mp3");
        audioManager.findClip("collide.mp3");
        audioManager.playMusic("title.mp3", true);

        // 3. Entity & Movement Setup
        entityManager = new EntityManager();
        movementInput = new MovementInput();
        movementManager = new MovementManager(audioManager); 

        // Initialize Player with correct dependencies
        player = new Circle("player_circle", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 30f, playerInput, ioManager);
        player.setColor(0.2f, 0.6f, 1f, 1f);
        entityManager.addEntity(player);

        // Initialize Bucket
        Bucket bucket = new Bucket("bucket", Gdx.graphics.getWidth() / 2f, 20f);
        entityManager.addEntity(bucket);

        // Initialize Bullet (Ensure Bullet.java exists in entity package)
        float bulletX = Gdx.graphics.getWidth() * 0.5f;
        float bulletY = Gdx.graphics.getHeight() * 0.75f;
        Bullet singleBullet = new Bullet("bullet_single", bulletX, bulletY, null, audioManager);
        singleBullet.setVelocity(0f, 0f);
        entityManager.addEntity(singleBullet);

        // 4. Collision Setup
        collisionManager = new CollisionManager();
        collisionManager.setAudioManager(audioManager);
        collisionManager.register(player);
        collisionManager.register(bucket);
        collisionManager.register(singleBullet);

        // 5. UI Setup
        uiManager = new UIManager(audioManager);

        // Register the movement event to bridge Circle's update to the walking sound
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

        // Handle Pause Toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            uiManager.toggleMenu(isPaused);
            
            // Critical: Toggle the input manager's activity
            ioManager.setActive(!isPaused);
            
            // Clear movement flags on state change
            if (!isPaused) {
                playerInput.reset();
                wasMoving = false;
            }
        }

        // Logic Update
        if (!isPaused) {
            // Update inputs
            ioManager.update(deltaTime);
            movementInput.update(); 
            
            // Movement manager applies logic and sfx
            movementManager.applyMovement(movementInput, deltaTime); 

            // Standard entity and collision updates
            entityManager.updateAll(deltaTime);
            collisionManager.update(deltaTime);
            collisionManager.resolveCollisions();
        }

        // Rendering
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        entityManager.renderAll(batch);
        batch.end();

        // UI draw call (always after game world)
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
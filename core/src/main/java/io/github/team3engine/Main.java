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

public class Main extends ApplicationAdapter {
    // Managers
    private AudioManager audioManager;
    private UIManager uiManager;
    private EntityManager entityManager;
    private CollisionManager collisionManager;
    private MovementManager movementManager;

    // Input & State
    private MovementInput movementInput;
    private SpriteBatch batch;
    private Texture image;
    private boolean isPaused = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

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

        Circle player = new Circle("player", Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f, 30f);
        entityManager.addEntity(player);

        Bucket bucket = new Bucket("bucket", Gdx.graphics.getWidth()/2f, 20f);
        entityManager.addEntity(bucket);

        // 3. Collision Setup
        collisionManager = new CollisionManager();
        collisionManager.setAudioManager(audioManager);
        collisionManager.register(player);
        collisionManager.register(bucket);

        // 4. UI Setup
        uiManager = new UIManager(audioManager);
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
            movementManager.applyMovement(movementInput, deltaTime); // Move & Play SFX
            
            entityManager.updateAll(deltaTime);
            collisionManager.update(delta);
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
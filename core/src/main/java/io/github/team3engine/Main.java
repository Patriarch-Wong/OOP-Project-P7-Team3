package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.UIManager;

public class Main extends ApplicationAdapter {
    // Managers
    private AudioManager audioManager;
    private UIManager uiManager;

    // Game Assets/State
    private SpriteBatch batch;
    private Texture image;
    private boolean isPaused = false;
    
    // Footstep logic
    private float footstepTimer = 0;
    private final float FOOTSTEP_INTERVAL = 0.2f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        
        // 1. Initialize Audio
        audioManager = new AudioManager();
        audioManager.findClip("walk.mp3");
        audioManager.findClip("jump.mp3");
        audioManager.findClip("collide.mp3");
        
        audioManager.setMusicVolume(0.05f);
        audioManager.setSFXVolume(0.5f);
        audioManager.playMusic("title.mp3", true);   

        // 2. Initialize UI (Passing the audioManager so they can communicate)
        uiManager = new UIManager(audioManager);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        
        // 1. Handle Input for Pausing
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            uiManager.toggleMenu(isPaused); // Handles visibility and InputProcessor
        }

        // 2. Clear Screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // 3. Draw Game World (Always visible)
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();

        // 4. State-Based Logic
        if (!isPaused) {
            handleGameInput(deltaTime);
        }

        // 5. Update and Draw UI (Drawn last to be on top)
        uiManager.update(deltaTime);
        uiManager.draw();
    }

    private void handleGameInput(float deltaTime) {
        // Jump
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            audioManager.play("jump.mp3");
        }

        // Collision test
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            audioManager.play("collide.mp3");
        }

        // Movement / Footsteps
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            footstepTimer += deltaTime;
            if (footstepTimer >= FOOTSTEP_INTERVAL) {
                audioManager.play("walk.mp3");
                footstepTimer = 0; 
            }
        } else {
            footstepTimer = FOOTSTEP_INTERVAL; 
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        uiManager.dispose(); // Clean up stage and skin
        // If your AudioManager has a dispose, call it here too
    }
}
package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.team3engine.audio.AudioManager;


public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private AudioManager audioManager; //

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        
        
        
        // Preload sound effect.
        // Into Game Engine
        audioManager = new AudioManager();
        audioManager.findClip("walk.mp3");
        audioManager.findClip("jump.mp3");
        audioManager.findClip("collide.mp3");
        
        audioManager.setMusicVolume(0.05f);
        audioManager.setSFXVolume(0.5f);
        audioManager.playMusic("title.mp3", true);   
        
    }
    
    private float footstepTimer = 0;
    private final float FOOTSTEP_INTERVAL = 0.2f;

    @Override
    public void render() {
    	float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
        
     // Into the I/O MANAGER
     // TRIGGER SFX: Press P to play the sound effect 
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            audioManager.play("test_sfx.mp3");
        }
        
     // JUMP TRIGGER (Space key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            audioManager.play("jump.mp3");
        }

        // COLLIDE TRIGGER (C key for testing)
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            audioManager.play("collide.mp3");
        }

        // Check if A or D is being held down
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            footstepTimer += deltaTime;

            if (footstepTimer >= FOOTSTEP_INTERVAL) {
                audioManager.play("walk.mp3");
                footstepTimer = 0; 
            }
        } else {
            // Keep this at FOOTSTEP_INTERVAL so the next press triggers immediately
            footstepTimer = FOOTSTEP_INTERVAL; 
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();

    }
}
package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.team3engine.entity.AudioManager;


public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private AudioManager audioManager; //

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        
        audioManager = new AudioManager();
        audioManager.setMusicVolume(0.05f);
        audioManager.setSFXVolume(0.5f);
        audioManager.playMusic("title.mp3", true);   
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
        
     // TRIGGER SFX: Press SPACE to play the sound effect
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // This uses the play method from your AudioManager
            audioManager.play("test_sfx.mp3");
            System.out.println("Space pressed: Playing test_sfx.mp3");
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();

    }
}
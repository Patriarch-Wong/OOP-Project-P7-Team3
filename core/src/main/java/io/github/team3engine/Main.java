package io.github.team3engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.io.IOManager;
import io.github.team3engine.io.PlayerInput;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private AudioManager audioManager; //
    private IOManager ioManager;
    private PlayerInput playerInput;
    private TextureObject bucketplayer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        bucketplayer = new TextureObject("bucket.png", 200, 50, 100);

        audioManager = new AudioManager();
        audioManager.setMusicVolume(0.05f);
        audioManager.setSFXVolume(0.5f);
        audioManager.playMusic("title.mp3", true);

        ioManager = new IOManager();
        playerInput = new PlayerInput(bucketplayer);
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);
        // setup eventlisteners (output)
        ioManager.registerEvent("PLAYER_DIED", () -> { // smthcan happen here
            System.out.println("bucketplayer has died");
        });
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

        // IO MANAGER demo
        if (playerInput.isLeftHeld()) {
            // inside this block shd use the other maanger methods
            float bucketX = bucketplayer.getX() - Gdx.graphics.getDeltaTime() * bucketplayer.getSpeed();
            bucketplayer.setX(bucketX);
        }
        if (playerInput.isRightHeld()) {
            // inside this block shd use the other maanger methods
            float bucketX = bucketplayer.getX() + Gdx.graphics.getDeltaTime() * bucketplayer.getSpeed();
            bucketplayer.setX(bucketX);
        }
        // simulate player died by pressing u key
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            // broadcast that event player died has happened
            ioManager.broadcast("PLAYER_DIED");
        }
        // add anothter callback to playerdied
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            ioManager.registerEvent("PLAYER_DIED", () -> { // smthcan happen here
                System.out.println("second callback");
            });
        }

        batch.begin();

        batch.draw(bucketplayer.getTexture(), bucketplayer.getX(), bucketplayer.getY(),
                bucketplayer.getTexture().getWidth(),
                bucketplayer.getTexture().getHeight());

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();

    }
}
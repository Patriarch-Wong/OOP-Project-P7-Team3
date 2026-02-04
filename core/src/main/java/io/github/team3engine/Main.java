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


public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private AudioManager audioManager;
    private EntityManager entityManager;
    private CollisionManager collisionManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        audioManager = new AudioManager();
        audioManager.findClip("walk.mp3");
        audioManager.findClip("jump.mp3");
        audioManager.findClip("collide.mp3");
        audioManager.setMusicVolume(0.05f);
        audioManager.setSFXVolume(0.5f);
        audioManager.playMusic("title.mp3", true);

        entityManager = new EntityManager();
        float cx = Gdx.graphics.getWidth() * 0.5f;
        float cy = Gdx.graphics.getHeight() * 0.5f;
        Circle circle = new Circle("player_circle", cx, cy, 30f);
        circle.setColor(0.2f, 0.6f, 1f, 1f);
        entityManager.addEntity(circle);

        Bucket bucket = new Bucket("bucket", 0, 20f);
        bucket.setPos(Gdx.graphics.getWidth() * 0.5f - bucket.getWidth() * 0.5f, 20f);
        entityManager.addEntity(bucket);

        collisionManager = new CollisionManager();
        collisionManager.setAudioManager(audioManager);
        collisionManager.register(circle);
        collisionManager.register(bucket);
    }
    
    private float footstepTimer = 0;
    private final float FOOTSTEP_INTERVAL = 0.2f;

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        entityManager.updateAll(deltaTime);
        collisionManager.resolveCollisions();

        batch.begin();
        batch.draw(image, 140, 210);
        entityManager.renderAll(batch);
        batch.end();

        // SFX: P, Space, C
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            audioManager.play("test_sfx.mp3");
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            audioManager.play("jump.mp3");
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            audioManager.play("collide.mp3");
        }

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
        entityManager.disposeAll();
        batch.dispose();
        image.dispose();
    }
}
package io.github.team3engine;

import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.collision.CollisionManager;
import io.github.team3engine.entity.EntityManager;
import io.github.team3engine.entity.MovementManager;
import io.github.team3engine.io.IOManager;
import io.github.team3engine.scene.SceneManager;

/**
 * Holds all managers for the game engine. Main (or other scenes) creates the world
 * and runs the loop using these managers via the getters.
 */
public class GameEngine {
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private IOManager ioManager;
    private AudioManager audioManager;

    public GameEngine() {}

    /**
     * Initialize and wire the managers only. Does not create entities or world.
     */
    public void init() {
        audioManager = new AudioManager();
        ioManager = new IOManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager(audioManager);
        collisionManager = new CollisionManager();
        collisionManager.setAudioManager(audioManager);
        sceneManager = SceneManager.getInstance();
    }

    public void start() {}

    public void stop() {}

    /**
     * Dispose managers owned by the engine (entities, scene).
     */
    public void dispose() {
        if (entityManager != null) entityManager.disposeAll();
        if (sceneManager != null) sceneManager.disposeAll();
    }

    public SceneManager getSceneManager() { return sceneManager; }
    public EntityManager getEntityManager() { return entityManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IOManager getIOManager() { return ioManager; }
    public AudioManager getAudioManager() { return audioManager; }
}

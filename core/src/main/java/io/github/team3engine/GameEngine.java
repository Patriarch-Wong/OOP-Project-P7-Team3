package io.github.team3engine;

import com.badlogic.gdx.Gdx;

import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.entity.MovementManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.SceneManager;

/**
 * Generic central orchestrator of the game engine. Holds all core managers and
 * provides init/start/stop and game loop update/render. Event wiring is done
 * by the game layer (e.g. Main), not here.
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
        movementManager = new MovementManager();
        collisionManager = new CollisionManager();
        sceneManager = SceneManager.getInstance();
    }

    public void start() {}

    public void stop() {}

    /**
     * Update step of the game loop (UML: update(deltaTime)). Delegates to IOManager and SceneManager.
     */
    public void update(float deltaTime) {
        if (ioManager != null) ioManager.update(deltaTime);
        if (sceneManager != null) sceneManager.update(deltaTime);
    }

    /**
     * Render step of the game loop. Delegates to SceneManager.
     */
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (sceneManager != null) sceneManager.render(delta);
    }

    /**
     * Dispose managers owned by the engine (entities, scene, audio, IO, collision).
     */
    public void dispose() {
        if (ioManager != null) ioManager.dispose();
        if (entityManager != null) entityManager.disposeAll();
        if (collisionManager != null) collisionManager.clear();
        if (sceneManager != null) sceneManager.disposeAll();
        if (audioManager != null) audioManager.dispose();
    }

    public SceneManager getSceneManager() { return sceneManager; }
    public EntityManager getEntityManager() { return entityManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IOManager getIOManager() { return ioManager; }
    public AudioManager getAudioManager() { return audioManager; }
}

package io.github.team3engine;

import java.util.ArrayList;
import java.util.List;

import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.interfaces.Disposable;
import io.github.team3engine.engine.interfaces.FrameRenderable;
import io.github.team3engine.engine.interfaces.Updatable;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.SceneManager;

/**
 * Generic central orchestrator of the game engine. Holds all core managers,
 * registers them by interface (Updatable, FrameRenderable, Disposable), and
 * drives update/render/dispose by iterating those lists. Event wiring is done
 * by the game layer (e.g. Main), not here.
 */
public class GameEngine {
    private final List<Updatable> updatables = new ArrayList<>();
    private final List<FrameRenderable> frameRenderables = new ArrayList<>();
    private final List<Disposable> disposables = new ArrayList<>();

    private SceneManager sceneManager;
    private EntityManager entityManager;
    private MovementManager movementManager;
    private CollisionManager collisionManager;
    private IOManager ioManager;
    private AudioManager audioManager;

    public GameEngine() {}

    /**
     * Initialize and wire the managers only; register them for update/render/dispose.
     */
    public void init() {
        audioManager = new AudioManager();
        ioManager = new IOManager();
        entityManager = new EntityManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager();
        sceneManager = SceneManager.getInstance();

        updatables.add(ioManager);
        updatables.add(sceneManager);

        frameRenderables.add(sceneManager);

        disposables.add(sceneManager);
        disposables.add(collisionManager);
        disposables.add(entityManager);
        disposables.add(ioManager);
        disposables.add(audioManager);
    }

    public void start() {}

    public void stop() {}

    //call update(deltaTime) on every registered Updatable.
    public void update(float deltaTime) {
        for (Updatable u : updatables) {
            u.update(deltaTime);
        }
    }

    //call render(deltaTime) on every registered FrameRenderable.

    public void render(float deltaTime) {
        for (FrameRenderable r : frameRenderables) {
            r.render(deltaTime);
        }
    }

    //call dispose() on every registered Disposable.
    public void dispose() {
        for (Disposable d : disposables) {
            d.dispose();
        }
    }

    public SceneManager getSceneManager() { return sceneManager; }
    public EntityManager getEntityManager() { return entityManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IOManager getIOManager() { return ioManager; }
    public AudioManager getAudioManager() { return audioManager; }
}

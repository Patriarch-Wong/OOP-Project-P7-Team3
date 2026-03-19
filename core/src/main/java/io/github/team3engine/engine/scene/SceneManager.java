package io.github.team3engine.engine.scene;

import java.util.HashMap;
import java.util.Map;

import io.github.team3engine.engine.interfaces.Disposable;
import io.github.team3engine.engine.interfaces.FrameRenderable;
import io.github.team3engine.engine.interfaces.Updatable;

/**
 * Generic scene manager for the game engine. Holds scenes by string id,
 * manages the current scene, and handles show/hide on transition. Game-specific
 * scene creation and event wiring belong in the game (e.g. Main), not here.
 */
public class SceneManager implements Updatable, FrameRenderable, Disposable {
    // private static SceneManager instance;

    private final Map<String, BaseScene> scenes = new HashMap<>();
    private String currentSceneId;

    public SceneManager() {

    }

    // public static SceneManager getInstance() {
    //     if (instance == null) instance = new SceneManager();
    //     return instance;
    // }

    /**
     * Registers a scene under the given id. Does not switch to it.
     */
    public void registerScene(String id, BaseScene scene) {
        if (id == null || scene == null) return;
        scenes.put(id, scene);
    }

    /**
     * Unregisters a scene by id. If it is the current scene, current is cleared (caller should set another).
     */
    public void unregisterScene(String id) {
        scenes.remove(id);
        if (id != null && id.equals(currentSceneId)) {
            currentSceneId = null;
        }
    }

    /**
     * Sets the current scene by id. Hides the previous scene and shows the new one.
     * The scene must already be registered.
     */
    public void setScene(String id) {
        if (id == null || !scenes.containsKey(id) || id.equals(currentSceneId)) {
            return;
        }
        BaseScene current = getCurrentScene();
        if (current != null) {
            current.hide();
        }
        this.currentSceneId = id;
        BaseScene next = getCurrentScene();
        if (next != null) {
            next.show();
        }
    }

    /**
     * Reloads the active scene by running its hide/show lifecycle.
     * No-op when there is no active scene.
     */
    public void reloadCurrentScene() {
        BaseScene current = getCurrentScene();
        if (current == null) {
            return;
        }
        current.hide();
        current.show();
    }

    public BaseScene getScene(String id) {
        return scenes.get(id);
    }

    public BaseScene getCurrentScene() {
        return currentSceneId == null ? null : scenes.get(currentSceneId);
    }

    public String getCurrentSceneId() {
        return currentSceneId;
    }

    @Override
    public void update(float deltaTime) {
        BaseScene current = getCurrentScene();
        if (current != null) {
            current.update(deltaTime);
        }
    }

    @Override
    public void render(float delta) {
        BaseScene current = getCurrentScene();
        if (current != null) {
            current.render(delta);
        }
    }

    public void disposeAll() {
        for (BaseScene scene : scenes.values()) {
            if (scene != null) {
                scene.dispose();
            }
        }
        scenes.clear();
        currentSceneId = null;
    }

    @Override
    public void dispose() {
        disposeAll();
    }
}

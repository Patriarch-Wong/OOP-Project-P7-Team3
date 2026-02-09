package io.github.team3engine.engine.scene;

import java.util.HashMap;
import java.util.Map;

/**
 * Generic scene manager for the game engine. Holds scenes by string id,
 * manages the current scene, and handles show/hide on transition. Game-specific
 * scene creation and event wiring belong in the game (e.g. Main), not here.
 */
public class SceneManager {
    private static SceneManager instance;

    private final Map<String, BaseScene> scenes = new HashMap<>();
    private String currentSceneId;
    private boolean paused = false;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

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

    public BaseScene getCurrentScene() {
        return currentSceneId == null ? null : scenes.get(currentSceneId);
    }

    public String getCurrentSceneId() {
        return currentSceneId;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
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
}

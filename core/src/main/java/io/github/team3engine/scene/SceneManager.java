package io.github.team3engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.team3engine.GameEngine;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;

    private SceneType currentSceneType;
    private final Map<SceneType, BaseScene> scenes = new HashMap<>();
    private boolean paused = false;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void init(GameEngine engine, SpriteBatch batch) {
        scenes.put(SceneType.SCENE_1, new Scene1(engine, batch));
        scenes.put(SceneType.SCENE_2, new Scene2(engine, batch));
        scenes.put(SceneType.PAUSE_SCENE, new PauseScene(engine, batch));
        scenes.put(SceneType.WIN_SCENE, new WinScene(engine, batch));
        this.currentSceneType = SceneType.SCENE_1;
        getCurrentScene().show();

        // Win condition toggles between Scene1 and Scene2. Defer switch to end of frame
        // so we don't change scene mid-render (which would dispose entities while still in use).
        engine.getIOManager().registerEvent("PLAYER_WIN", () -> {
            final SceneType next = (currentSceneType == SceneType.SCENE_1) ? SceneType.SCENE_2 : SceneType.SCENE_1;
            Gdx.app.postRunnable(() -> setScene(next));
        });
    }

    public BaseScene getCurrentScene() {
        return scenes.get(currentSceneType);
    }

    public void setScene(SceneType type) {
        BaseScene current = getCurrentScene();
        if (current != null) {
            current.hide();
        }
        this.currentSceneType = type;
        BaseScene next = getCurrentScene();
        if (next != null) {
            next.show();
        }
    }

    public SceneType getCurrentSceneType() {
        return currentSceneType;
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
    }
}

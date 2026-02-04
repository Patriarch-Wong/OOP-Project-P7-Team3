package io.github.team3engine.scene;

import com.badlogic.gdx.utils.Array;

public class SceneManager {
    public enum SceneType { MENU, GAME, SETTINGS } // Define your scenes
    private SceneType currentScene = SceneType.MENU;

    public void setScene(SceneType type) {
        this.currentScene = type;
        System.out.println("Switching scene to: " + type);
    }

    public SceneType getCurrentScene() {
        return currentScene;
    }
}
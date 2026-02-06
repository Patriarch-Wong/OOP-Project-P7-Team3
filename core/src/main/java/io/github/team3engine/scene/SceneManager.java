package io.github.team3engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;
    
    private ScreenType currentScreenType;
    private final Map<ScreenType, Object> screens = new HashMap<>();

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void init(SpriteBatch batch) {
        this.currentScreenType = ScreenType.SCREEN_1;
    }

    public void setScreen(ScreenType type) {
        this.currentScreenType = type;
    }

    public ScreenType getCurrentScreenType() {
        return currentScreenType;
    }

    public void disposeAll() {
        screens.clear();
    }
}
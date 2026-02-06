package io.github.team3engine.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;
    
    private Game game;
    private SpriteBatch batch;
    private final Map<ScreenType, Screen> screens = new HashMap<>();

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void init(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
    }

    public void setScreen(ScreenType type) {
        Screen screen = screens.get(type);
        if (screen == null) {
            screen = createScreen(type);
            screens.put(type, screen);
        }
        game.setScreen(screen);
    }

    private Screen createScreen(ScreenType type) {
        switch (type) {
            case TEST_SCREEN_1: return new TestScreen1(game, batch);
            case TEST_SCREEN_2: return new TestScreen2(game, batch);
            default: throw new IllegalArgumentException("Unknown: " + type);
        }
    }

    public void disposeAll() {
        for (Screen s : screens.values()) {
            if (s != null) s.dispose();
        }
        screens.clear();
    }
}
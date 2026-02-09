package io.github.team3engine.game.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.engine.scene.SceneType;

public class WinScene extends BaseScene {
    private final SceneManager sceneManager;

    public WinScene(SpriteBatch batch, SceneManager sceneManager) {
        super(batch);
        this.sceneManager = sceneManager;
        
        getStage().addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sceneManager.setScene(SceneType.WIN_SCENE.name());
                return true;
            }
        });
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "Congrats! You won!", 100, 400);
    }
}

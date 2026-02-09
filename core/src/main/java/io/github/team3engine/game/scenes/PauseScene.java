package io.github.team3engine.game.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import io.github.team3engine.engine.scene.*;

public class PauseScene extends BaseScene {
    private final SceneManager sceneManager;

    public PauseScene(SpriteBatch batch, SceneManager sceneManager) {
        super(batch);
        this.sceneManager = sceneManager;
        getStage().addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sceneManager.setScene(SceneType.PAUSE_SCENE.name());
                return true;
            }
        });
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "PAUSE!", 100, 400);
        font.draw(batch, "...Take a break. We all need one.", 200, 350);
    }
}

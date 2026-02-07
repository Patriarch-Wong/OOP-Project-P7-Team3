package io.github.team3engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import io.github.team3engine.GameEngine;

public class PauseScene extends BaseScene {
    public PauseScene(GameEngine engine, SpriteBatch batch) {
        super(engine, batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SceneManager.getInstance().setScene(SceneType.PAUSE_SCENE.name());
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

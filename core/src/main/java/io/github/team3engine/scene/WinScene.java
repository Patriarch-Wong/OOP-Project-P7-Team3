package io.github.team3engine.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import io.github.team3engine.GameEngine;

public class WinScene extends BaseScene {
    public WinScene(GameEngine engine, SpriteBatch batch) {
        super(engine, batch);
        getStage().addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SceneManager.getInstance().setScene(SceneType.WIN_SCENE.name());
                return true;
            }
        });
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "Congrats! You won!", 100, 400);
    }
}

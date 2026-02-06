package io.github.team3engine.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Screen1 extends BaseScreen {
    public Screen1(Game game, SpriteBatch batch) {
        super(game, batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SceneManager.getInstance().setScreen(ScreenType.SCREEN_2);
                return true;
            }
        });
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "SCREEN 1 - TAP ANYWHERE", 100, 400);
        font.draw(batch, "SceneManager WORKS!", 100, 350);
    }
}


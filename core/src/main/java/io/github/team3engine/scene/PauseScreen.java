package io.github.team3engine.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class PauseScreen extends BaseScreen {
    public PauseScreen(Game game, SpriteBatch batch) {
        super(game, batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SceneManager.getInstance().setScreen(ScreenType.PAUSE_SCREEN);
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


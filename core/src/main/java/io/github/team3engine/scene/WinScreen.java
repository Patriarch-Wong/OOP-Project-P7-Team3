package io.github.team3engine.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class WinScreen extends BaseScreen {
    public WinScreen(Game game, SpriteBatch batch) {
        super(game, batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SceneManager.getInstance().setScreen(ScreenType.WIN_SCREEN);
                return true;
            }
        });
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "Congrats! You won!", 100, 400);
    }
}


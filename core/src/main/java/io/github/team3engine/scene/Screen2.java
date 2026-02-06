package io.github.team3engine.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class Screen2 extends BaseScreen {
    public Screen2(Game game, SpriteBatch batch) {
        super(game, batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SceneManager.getInstance().setScreen(ScreenType.SCREEN_1);
                return true;
            }
        });
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "Good luck!", 100, 400);
    }
}


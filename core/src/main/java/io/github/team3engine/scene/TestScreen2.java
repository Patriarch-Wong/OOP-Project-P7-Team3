package io.github.team3engine.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class TestScreen2 extends BaseScreen {
    public TestScreen2(Game game, SpriteBatch batch) {
        super(game, batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SceneManager.getInstance().setScreen(ScreenType.TEST_SCREEN_1);
                return true;
            }
        });
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "SCREEN 2 - TAP TO GO BACK", 100, 400);
        font.draw(batch, "Switching works both ways!", 100, 350);
    }
}


package io.github.team3engine.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Factory for consistently styled scene buttons.
 * Use in any scene — keeps button size, hover and cursor behaviour uniform.
 */
public class SceneButtonFactory {

    public static final float BUTTON_WIDTH  = 200f;
    public static final float BUTTON_HEIGHT = 50f;

    private SceneButtonFactory() {}

    public static TextButton create(String label, Skin skin, Runnable onClick) {
        TextButton btn = new TextButton(label, skin);
        btn.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        btn.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    btn.setColor(0.7f, 0.7f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    btn.setColor(1f, 1f, 1f, 1f);
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClick.run();
            }
        });

        return btn;
    }
}

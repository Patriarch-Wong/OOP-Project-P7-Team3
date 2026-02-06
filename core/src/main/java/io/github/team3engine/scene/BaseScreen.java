package io.github.team3engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class BaseScreen implements Screen {
    protected final Game game;
    protected final SpriteBatch batch;
    protected final Stage stage;
    protected final BitmapFont font;

    public BaseScreen(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.stage = new Stage(new ScreenViewport(), batch);
        this.font = new BitmapFont();
        font.setColor(Color.BLUE);
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.act(delta);
        stage.draw();
        
        batch.begin();
        renderUI();
        batch.end();
    }

    protected abstract void renderUI();

    @Override public void dispose() { 
        stage.dispose(); 
        font.dispose(); 
    }
}

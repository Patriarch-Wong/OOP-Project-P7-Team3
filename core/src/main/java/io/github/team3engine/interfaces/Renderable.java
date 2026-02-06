package io.github.team3engine.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


//for objects that are renderable with spritebatch, helps to allow other sprites to be
//rendered togehter
public interface Renderable {
    void render(SpriteBatch batch);
}

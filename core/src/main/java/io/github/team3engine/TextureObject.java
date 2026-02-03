package io.github.team3engine;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextureObject {
    private Texture tex;
    private float x;
    private float y;
    private int speed;

    // constructor
    public TextureObject(String texturePath, float x, float y, int speed) {
        this.tex = new Texture(texturePath);
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public Texture getTexture() {
        return tex;
    }

    void setTexture(Texture t) {
        this.tex = t;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
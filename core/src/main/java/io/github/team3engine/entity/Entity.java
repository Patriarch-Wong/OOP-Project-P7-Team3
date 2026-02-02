package io.github.team3engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity implements Updatable, Renderable, Identifiable {
    private String id;
    private boolean active;
    private boolean destroyed;

    protected Vector2 position;
    protected float rotation;
    protected Vector2 scale;

    public Entity(String id) {
        this.id = id;
        this.active = true;
        this.destroyed = false;
        this.position = new Vector2();
        this.scale = new Vector2(1, 1);
        this.rotation = 0f;
    }

    public String getId() {
        return id;
    }

    public abstract void update(float dt);

    public abstract void render(SpriteBatch batch);

    public abstract void dispose();

    public void destroy() {
        this.destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector2 getPos() {
        return position;
    }

    public void setPos(float x, float y) {
        this.position.set(x, y);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Vector2 getScale() {
        return scale;
    }

    public void setScale(float x, float y) {
        this.scale.set(x, y);
    }
}

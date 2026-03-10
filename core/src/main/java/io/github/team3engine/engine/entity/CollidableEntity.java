package io.github.team3engine.engine.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.github.team3engine.engine.interfaces.Collidable;

public abstract class CollidableEntity extends Entity implements Collidable {
    protected Vector2 velocity;
    protected Rectangle hitbox;
    protected float speed;
    protected Vector2 direction;

    public CollidableEntity(String id) {
        super(id);
        this.velocity = new Vector2();
        this.hitbox = new Rectangle();
        this.direction = new Vector2();
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    // updates hitbox position to match entity position
    protected void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
    }

    @Override
    public abstract void onCollision(Collidable other);

    @Override
    public void update(float dt) {
        updateHitbox();
    }
}

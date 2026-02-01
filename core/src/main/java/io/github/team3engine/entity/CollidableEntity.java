package io.github.team3engine.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class CollidableEntity extends Entity {
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

    protected void updateHitbox() {
        // Typically updates hitbox position to match entity position
        // Assuming hitbox needs to be centered or at position
        // For now, setting it to position.x, position.y
        hitbox.setPosition(position.x, position.y);
    }

    public abstract void onCollision(CollidableEntity other);

    @Override
    public void update(float dt) {
        // Basic movement based on velocity
        position.mulAdd(velocity, dt);
        updateHitbox();
    }
}

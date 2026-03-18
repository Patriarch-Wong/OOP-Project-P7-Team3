package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Solid;

/**
 * Static fire hazard. Deals damage to any Damageable entity on contact.
 * Implements Solid so player can't walk through it.
 */
public class Fire extends CollidableEntity {
    private final float width;
    private final float height;
    private final float damage;
    private final Texture texture;
    private final Animation<TextureRegion> animation;
    private float stateTime = 0f;

    public Fire(String id, float x, float y, float width, float height, float damage) {
        super(id);
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.texture = new Texture("ui/sprites/fire_spritesheet.png");
        TextureRegion[][] frames = TextureRegion.split(texture, 32, 32);
        this.animation = new Animation<>(0.15f, frames[0]);
        setPos(x, y);
        updateHitbox();
    }

    public Fire(String id, float x, float y, float width, float height) {
        this(id, x, y, width, height, 20f);
    }

    public float getDamage() { return damage; }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(width, height);
    }

    @Override
    public void update(float dt) {
        stateTime += dt;
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        batch.draw(frame, position.x, position.y, width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        // Game logic handled by CollisionMediator
    }
}

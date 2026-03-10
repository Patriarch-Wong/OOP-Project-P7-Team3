package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Solid;

/**
 * Static fire hazard. Deals damage to any Damageable entity on contact.
 * Implements Solid so player can't walk through it.
 */
public class Fire extends CollidableEntity implements Solid {
    private final float width;
    private final float height;
    private final float damage;
    private final ShapeRenderer shapeRenderer;
    private float animTimer = 0f;

    public Fire(String id, float x, float y, float width, float height, float damage) {
        super(id);
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.shapeRenderer = new ShapeRenderer();
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
        animTimer += dt;
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float flicker = (float) Math.sin(animTimer * 8f) * 0.15f;
        shapeRenderer.setColor(1f, 0.3f + flicker, 0f, 1f);
        shapeRenderer.rect(position.x, position.y, width, height);

        float coreMargin = width * 0.2f;
        shapeRenderer.setColor(1f, 0.7f + flicker, 0f, 0.8f);
        shapeRenderer.rect(position.x + coreMargin, position.y,
                width - coreMargin * 2, height * 0.7f);

        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        // Game logic handled by CollisionMediator
    }
}

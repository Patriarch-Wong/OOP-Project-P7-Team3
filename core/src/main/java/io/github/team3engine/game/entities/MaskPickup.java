package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Pickup;
import io.github.team3engine.game.status.DamageReductionEffect;

/**
 * Pickup that grants 50% damage reduction for 10 seconds.
 * Rendered as a blue bobbing circle.
 */
public class MaskPickup extends CollidableEntity implements Pickup {
    private static final float SIZE = 16f;
    private static final float REDUCTION = 0.5f;
    private static final float DURATION = 10f;

    private final ShapeRenderer shapeRenderer;
    private float bobTimer = 0f;
    private final float baseY;

    public MaskPickup(String id, float x, float y) {
        super(id);
        this.baseY = y;
        this.shapeRenderer = new ShapeRenderer();
        setPos(x, y);
        updateHitbox();
    }

    @Override
    public void onPickup(Entity collector) {
        if (collector instanceof Player) {
            Player player = (Player) collector;
            player.getStatusEffects().apply(new DamageReductionEffect(REDUCTION, DURATION));
        }
        destroy();
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x - SIZE, position.y - SIZE);
        hitbox.setSize(SIZE * 2, SIZE * 2);
    }

    @Override
    public void update(float dt) {
        bobTimer += dt;
        position.y = baseY + (float) Math.sin(bobTimer * 3f) * 4f;
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.58f, 0.86f, 1f);
        shapeRenderer.circle(position.x, position.y, SIZE);
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Player && !isDestroyed()) {
            onPickup((Player) other);
        }
    }
}

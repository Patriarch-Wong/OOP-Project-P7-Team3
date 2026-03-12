package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Pickup;
import io.github.team3engine.game.status.DamageReductionEffect;

/**
 * Pickup that grants 30% damage reduction for 15 seconds.
 */
public class WetTowelPickup extends CollidableEntity implements Pickup {
    private static final float SIZE = 22f;
    private static final float REDUCTION = 0.3f;
    private static final float DURATION = 15f;

    private final Texture texture;
    private float bobTimer = 0f;
    private final float baseY;

    public WetTowelPickup(String id, float x, float y) {
        super(id);
        this.baseY = y;
        this.texture = new Texture("towel.png");
        setPos(x, y);
        updateHitbox();
    }

    @Override
    public void onPickup(Entity collector) {
        if (collector instanceof Player) {
            Player player = (Player) collector;
            player.getStatusEffects().apply(new DamageReductionEffect(REDUCTION, DURATION, "Towel"));
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
        float drawSize = SIZE * 2;
        batch.draw(texture, position.x - SIZE, position.y - SIZE, drawSize, drawSize);
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

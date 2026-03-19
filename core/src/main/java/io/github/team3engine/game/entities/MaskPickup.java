package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Pickup;
import io.github.team3engine.game.status.DamageReductionEffect;

/**
 * Pickup that grants damage reduction and optionally extends the scene timer.
 * All values are configurable via constructor — no hardcoded game constants.
 */
public class MaskPickup extends CollidableEntity implements Pickup {
    private static final float SIZE = 22f;

    private final float reduction;       // damage reduction fraction e.g. 0.5 = 50%
    private final float duration;        // how long the effect lasts in seconds
    private final float timerExtend;     // seconds added to scene timer (0 = no extension)

    private final Texture texture;
    private float         bobTimer = 0f;
    private final float   baseY;

    // Optional callback to extend the scene timer — wired from the scene
    private Runnable onTimerExtend;

    /**
     * @param id           entity id
     * @param x            x position
     * @param y            y position
     * @param reduction    damage reduction fraction (0.0 – 1.0)
     * @param duration     effect duration in seconds
     * @param timerExtend  seconds to add to scene timer on pickup (0 = none)
     */
    public MaskPickup(String id, float x, float y,
                      float reduction, float duration, float timerExtend) {
        super(id);
        this.baseY       = y;
        this.reduction   = reduction;
        this.duration    = duration;
        this.timerExtend = timerExtend;
        this.texture     = new Texture("mask.png");
        setPos(x, y);
        updateHitbox();
    }

    /** Convenience constructor — 50% reduction, 10s duration, 10s timer extension. */
    public MaskPickup(String id, float x, float y) {
        this(id, x, y, 0.5f, 10f, 10f);
    }

    /** Wire up timer extension from the scene. */
    public void setOnTimerExtend(Runnable callback) {
        this.onTimerExtend = callback;
    }

    @Override
    public void onPickup(Entity collector) {
        if (collector instanceof Player) {
            Player player = (Player) collector;
            player.getStatusEffects().apply(
                    new DamageReductionEffect(reduction, duration, "Mask"));

            if (timerExtend > 0f && onTimerExtend != null) {
                onTimerExtend.run();
            }
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
        bobTimer   += dt;
        position.y  = baseY + (float) Math.sin(bobTimer * 3f) * 4f;
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        float drawSize = SIZE * 2;
        batch.draw(texture, position.x - SIZE, position.y - SIZE, drawSize, drawSize);
    }

    @Override
    public void dispose() { texture.dispose(); }

    @Override
    public void onCollision(Collidable other) {}
}

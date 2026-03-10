package io.github.team3engine.game.status;

import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.status.StatusEffect;

/**
 * Reduces damage taken by a percentage for a duration.
 * E.g. reductionFactor=0.5 means 50% less damage.
 */
public class DamageReductionEffect extends StatusEffect {
    private final float reductionFactor;
    private final String name;

    public DamageReductionEffect(float reductionFactor, float duration, String name) {
        super(duration);
        this.reductionFactor = reductionFactor;
        this.name = name;
    }

    /** Returns the multiplier for incoming damage (e.g. 0.5 = half damage). */
    public float getDamageMultiplier() {
        return 1f - reductionFactor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void onApply(Entity target) { }

    @Override
    public void onRemove(Entity target) { }
}

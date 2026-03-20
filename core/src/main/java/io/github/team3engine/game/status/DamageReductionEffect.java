package io.github.team3engine.game.status;

import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.game.status.StatusEffect;

/**
 * Reduces damage taken by a percentage for a duration.
 * E.g. reductionFactor=0.5 means 50% less damage.
 */
public class DamageReductionEffect extends StatusEffect {
    private static final float MIN_FACTOR = 0f;
    private static final float MAX_FACTOR = 1f;

    private final float reductionFactor;
    private final String name;

    public DamageReductionEffect(String effectKey, float reductionFactor, float duration, String name) {
        super(effectKey, duration);
        this.reductionFactor = clamp(reductionFactor, MIN_FACTOR, MAX_FACTOR);
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

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

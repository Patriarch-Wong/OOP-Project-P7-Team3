package io.github.team3engine.game.status;

import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.game.status.StatusEffect;

/**
 * Reduces movement speed by a percentage.
 * E.g. slowFactor=0.3 means 30% slower.
 */
public class SlowEffect extends StatusEffect {
    private static final float MIN_FACTOR = 0f;
    private static final float MAX_FACTOR = 1f;

    private final float slowFactor;

    /**
     * @param slowFactor fraction of speed to reduce (0.3 = 30% slower)
     * @param duration   seconds, or Float.MAX_VALUE for permanent
     */
    public SlowEffect(String effectKey, float slowFactor, float duration) {
        super(effectKey, duration);
        this.slowFactor = clamp(slowFactor, MIN_FACTOR, MAX_FACTOR);
    }

    /** Returns the speed multiplier (e.g. 0.7 for a 30% slow). */
    public float getSpeedMultiplier() {
        return 1f - slowFactor;
    }

    @Override
    public String getName() {
        return "Slow";
    }

    @Override
    public void onApply(Entity target) { }

    @Override
    public void onRemove(Entity target) { }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

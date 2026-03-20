package io.github.team3engine.game.status;

import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.game.status.StatusEffect;

/**
 * Reduces movement speed by a percentage.
 * E.g. slowFactor=0.3 means 30% slower.
 */
public class SlowEffect extends StatusEffect {
    private final float slowFactor;

    /**
     * @param slowFactor fraction of speed to reduce (0.3 = 30% slower)
     * @param duration   seconds, or Float.MAX_VALUE for permanent
     */
    public SlowEffect(float slowFactor, float duration) {
        super(duration);
        this.slowFactor = slowFactor;
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
}

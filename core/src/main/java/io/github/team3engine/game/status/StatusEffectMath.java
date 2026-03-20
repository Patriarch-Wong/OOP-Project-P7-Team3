package io.github.team3engine.game.status;

import java.util.List;

/**
 * Pure helpers for combining active status effects.
 */
public final class StatusEffectMath {
    private StatusEffectMath() {}

    public static float applyDamageReductions(float incomingDamage, List<DamageReductionEffect> reductions) {
        float damage = incomingDamage;
        if (reductions != null) {
            for (DamageReductionEffect reduction : reductions) {
                damage *= reduction.getDamageMultiplier();
            }
        }
        return Math.max(0f, damage);
    }

    public static float strongestSlowMultiplier(List<SlowEffect> slows) {
        float multiplier = 1f;
        if (slows != null) {
            for (SlowEffect slow : slows) {
                multiplier = Math.min(multiplier, slow.getSpeedMultiplier());
            }
        }
        return Math.max(0f, multiplier);
    }
}

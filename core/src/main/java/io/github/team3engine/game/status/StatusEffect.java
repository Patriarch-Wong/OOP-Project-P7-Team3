package io.github.team3engine.game.status;

import io.github.team3engine.engine.entity.Entity;

import java.util.Objects;

/**
 * A timed effect applied to an entity. Subclasses define what happens
 * on apply, each tick, and on removal.
 */
public abstract class StatusEffect {
    private final String effectKey;
    protected final float duration;
    protected float elapsed;

    /**
     * @param duration effect duration in seconds. Use Float.MAX_VALUE for permanent effects.
     */
    public StatusEffect(String effectKey, float duration) {
        this.effectKey = Objects.requireNonNull(effectKey, "effectKey");
        this.duration = duration;
        this.elapsed = 0f;
    }

    public final String getEffectKey() {
        return effectKey;
    }

    public void tick(float dt) {
        elapsed += dt;
    }

    public boolean isExpired() {
        return elapsed >= duration;
    }

    public float getRemainingTime() {
        return Math.max(0f, duration - elapsed);
    }

    public abstract String getName();
    public abstract void onApply(Entity target);
    public abstract void onRemove(Entity target);
}

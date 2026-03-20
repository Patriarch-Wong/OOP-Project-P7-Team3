package io.github.team3engine.game.status;

import io.github.team3engine.engine.entity.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages a list of active status effects on an entity.
 * Ticks all effects each frame and auto-removes expired ones.
 */
public class StatusEffectManager {
    private final List<StatusEffect> effects = new ArrayList<>();
    private final Entity owner;

    public StatusEffectManager(Entity owner) {
        this.owner = owner;
    }

    public void apply(StatusEffect effect) {
        effects.add(effect);
        effect.onApply(owner);
    }

    public void remove(StatusEffect effect) {
        if (effects.remove(effect)) {
            effect.onRemove(owner);
        }
    }

    public void update(float dt) {
        Iterator<StatusEffect> it = effects.iterator();
        while (it.hasNext()) {
            StatusEffect effect = it.next();
            effect.tick(dt);
            if (effect.isExpired()) {
                effect.onRemove(owner);
                it.remove();
            }
        }
    }

    public boolean hasEffect(Class<? extends StatusEffect> type) {
        for (StatusEffect e : effects) {
            if (type.isInstance(e)) return true;
        }
        return false;
    }

    public <T extends StatusEffect> T getEffect(Class<T> type) {
        for (StatusEffect e : effects) {
            if (type.isInstance(e)) return type.cast(e);
        }
        return null;
    }

    public <T extends StatusEffect> List<T> getAllEffects(Class<T> type) {
        List<T> matched = new ArrayList<>();
        for (StatusEffect e : effects) {
            if (type.isInstance(e)) matched.add(type.cast(e));
        }
        return matched;
    }

    public List<StatusEffect> getAll() {
        return effects;
    }

    public void clearAll() {
        for (StatusEffect e : effects) {
            e.onRemove(owner);
        }
        effects.clear();
    }
}

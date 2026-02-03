package io.github.team3engine.collision;

import com.badlogic.gdx.utils.Array;
import io.github.team3engine.entity.CollidableEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionManager {

    private boolean enabled = true;
    private final Map<CollidableEntity, Integer> layers = new HashMap<>();

    public CollisionManager() { }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void register(CollidableEntity entity) {
        register(entity, 0);
    }

    public void register(CollidableEntity entity, int layer) {
        if (entity == null) return;
        layers.put(entity, layer);
    }

    public void unregister(CollidableEntity entity) {
        layers.remove(entity);
    }

    public int getLayer(CollidableEntity entity) {
        return layers.getOrDefault(entity, 0);
    }

    public Array<CollidableEntity[]> checkCollisions() {
        Array<CollidableEntity[]> pairs = new Array<>();

        if (!enabled) {
            return pairs;
        }

        List<CollidableEntity> list = new ArrayList<>(layers.keySet());

        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                CollidableEntity a = list.get(i);
                CollidableEntity b = list.get(j);

                if (skipEntity(a) || skipEntity(b)) continue;
                if (getLayer(a) != getLayer(b)) continue;

                if (aabbOverlap(a, b)) {
                    pairs.add(new CollidableEntity[]{a, b});
                }
            }
        }

        return pairs;
    }

    public Array<CollidableEntity[]> resolveCollisions() {
        Array<CollidableEntity[]> pairs = checkCollisions();

        for (CollidableEntity[] pair : pairs) {
            if (pair == null || pair.length < 2) continue;

            CollidableEntity a = pair[0];
            CollidableEntity b = pair[1];

            try {
                a.onCollision(b);
            } catch (Exception e) {
                System.err.println("Exception in onCollision for entity " + a.getId() + ": " + e.getMessage());
            }

            try {
                b.onCollision(a);
            } catch (Exception e) {
                System.err.println("Exception in onCollision for entity " + b.getId() + ": " + e.getMessage());
            }
        }

        return pairs;
    }

    private boolean skipEntity(CollidableEntity e) {
        return e == null || e.isDestroyed() || !e.isActive();
    }

    private boolean aabbOverlap(CollidableEntity a, CollidableEntity b) {
        if (a == null || b == null) return false;
        if (a.getHitbox() == null || b.getHitbox() == null) return false;
        return a.getHitbox().overlaps(b.getHitbox());
    }
}

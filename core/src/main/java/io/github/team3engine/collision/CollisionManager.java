package com.game.collision;

import com.game.entity.CollidableEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Performs broad + narrow AABB collision detection.
 * collisionMatrix can be extended later to filter by layer tags.
 */
public class CollisionManager {

    /** layer-name → enabled flag (extensible per-layer toggle) */
    private final Map<String, Boolean> collisionMatrix;

    public CollisionManager() {
        collisionMatrix = new HashMap<>();
    }

    // ── layer helpers ─────────────────────────
    public void setLayerEnabled(String layer, boolean enabled) {
        collisionMatrix.put(layer, enabled);
    }
    public boolean isLayerEnabled(String layer) {
        return collisionMatrix.getOrDefault(layer, true);
    }

    // ── detection ─────────────────────────────
    /**
     * Returns every pair of entities whose hitboxes currently overlap.
     */
    public List<CollidableEntity[]> checkCollisions(List<CollidableEntity> entities) {
        List<CollidableEntity[]> pairs = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                CollidableEntity a = entities.get(i);
                CollidableEntity b = entities.get(j);

                if (aabb(a, b)) {
                    pairs.add(new CollidableEntity[]{a, b});
                }
            }
        }
        return pairs;
    }

    /**
     * Detects collisions and immediately dispatches onCollision to both entities.
     */
    public List<CollidableEntity[]> resolveCollisions(List<CollidableEntity> entities) {
        List<CollidableEntity[]> pairs = checkCollisions(entities);
        for (CollidableEntity[] pair : pairs) {
            pair[0].onCollision(pair[1]);
            pair[1].onCollision(pair[0]);
        }
        return pairs;
    }

    // ── AABB math ─────────────────────────────
    private static boolean aabb(CollidableEntity a, CollidableEntity b) {
        return a.getHitbox().overlaps(b.getHitbox());
    }
}

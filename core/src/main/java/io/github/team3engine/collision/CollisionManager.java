package io.github.team3engine.collision;

import com.badlogic.gdx.utils.Array;
import io.github.team3engine.entity.CollidableEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionManager {

    private boolean collisionMatrix = true;
    private final Map<CollidableEntity, Integer> collisionLayers = new HashMap<>();

    public CollisionManager() { }

    public void setCollisionMatrixEnabled(boolean enabled) {
        this.collisionMatrix = enabled;
    }

    public boolean isCollisionMatrixEnabled() {
        return collisionMatrix;
    }

    public void setLayer(CollidableEntity entity, int layer) {
        if (entity == null) return;
        collisionLayers.put(entity, layer);
    }

    public void removeEntity(CollidableEntity entity) {
        collisionLayers.remove(entity);
    }

    public int getLayer(CollidableEntity entity) {
        return collisionLayers.getOrDefault(entity, 0);
    }

    public List<CollidableEntity> getRegisteredEntities() {
        return new ArrayList<>(collisionLayers.keySet());
    }

    public Array<CollidableEntity> checkCollisions() {
        Array<CollidableEntity> colliding = new Array<>();

        if (!collisionMatrix) {
            return colliding;
        }

        List<CollidableEntity> entities = getRegisteredEntities();
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                CollidableEntity a = entities.get(i);
                CollidableEntity b = entities.get(j);

                if (!layersCanCollide(a, b)) continue;

                if (aabb(a, b)) {
                    if (!colliding.contains(a, true)) colliding.add(a);
                    if (!colliding.contains(b, true)) colliding.add(b);
                }
            }
        }
        return colliding;
    }

    public Array<CollidableEntity> resolveCollsion() {
        Array<CollidableEntity> colliding = new Array<>();

        if (!collisionMatrix) {
            return colliding;
        }

        List<CollidableEntity> entities = getRegisteredEntities();
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                CollidableEntity a = entities.get(i);
                CollidableEntity b = entities.get(j);

                if (!layersCanCollide(a, b)) continue;

                if (aabb(a, b)) {
                    try {
                        a.onCollision(b);
                    } catch (Exception ignored) {}

                    try {
                        b.onCollision(a);
                    } catch (Exception ignored) {}

                    if (!colliding.contains(a, true)) colliding.add(a);
                    if (!colliding.contains(b, true)) colliding.add(b);
                }
            }
        }
        return colliding;
    }

    private boolean layersCanCollide(CollidableEntity a, CollidableEntity b) {
        return getLayer(a) == getLayer(b);
    }

    private static boolean aabb(CollidableEntity a, CollidableEntity b) {
        if (a == null || b == null || a.getHitbox() == null || b.getHitbox() == null) {
            return false;
        }
        return a.getHitbox().overlaps(b.getHitbox());
    }
}

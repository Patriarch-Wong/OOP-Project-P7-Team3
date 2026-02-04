package io.github.team3engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class EntityManager {
    private Array<Entity> entities;
    private Array<Entity> pendingAdd;
    private Array<Entity> pendingRemove;

    public EntityManager() {
        entities = new Array<>();
        pendingAdd = new Array<>();
        pendingRemove = new Array<>();
    }

    public void addEntity(Entity e) {
        pendingAdd.add(e);
    }

    public void clear() {
        entities.clear();
        pendingAdd.clear();
        pendingRemove.clear();
    }

    public Entity getById(String id) {
        for (Entity e : entities) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        // Check pending adds if requested before update
        for (Entity e : pendingAdd) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

    public Array<Entity> getAll() {
        return entities;
    }

    public Array<CollidableEntity> getCollidables() {
        Array<CollidableEntity> collidables = new Array<>();
        for (Entity e : entities) {
            if (e instanceof CollidableEntity) {
                collidables.add((CollidableEntity) e);
            }
        }
        return collidables;
    }

    public Array<NonCollidableEntity> getNonCollidables() {
        Array<NonCollidableEntity> nonCollidables = new Array<>();
        for (Entity e : entities) {
            if (e instanceof NonCollidableEntity) {
                nonCollidables.add((NonCollidableEntity) e);
            }
        }
        return nonCollidables;
    }

    public void updateAll(float dt) {
        flushPendingAdds();
        flushPendingRemovals();

        for (Entity e : entities) {
            if (e.isActive()) {
                e.update(dt);
            }
            if (e.isDestroyed()) {
                pendingRemove.add(e);
            }
        }

        // Clean up destroyed entities after update
        purgeDestroyed();
    }

    public void renderAll(SpriteBatch batch) {
        for (Entity e : entities) {
            if (e.isActive() && !e.isDestroyed()) {
                e.render(batch);
            }
        }
    }

    protected void flushPendingAdds() {
        if (pendingAdd.size > 0) {
            entities.addAll(pendingAdd);
            pendingAdd.clear();
        }
    }

    protected void flushPendingRemovals() {
        if (pendingRemove.size > 0) {
            entities.removeAll(pendingRemove, true);
            pendingRemove.clear();
        }
    }

    public void purgeDestroyed() {
        // Collect destroyed entities
        for (Entity e : entities) {
            if (e.isDestroyed()) {
                pendingRemove.add(e);
            }
        }
        flushPendingRemovals();
    }

    public void disposeAll() {
        for (Entity e : entities) {
            e.dispose();
        }
        entities.clear();
        pendingAdd.clear();
        pendingRemove.clear();
    }
}

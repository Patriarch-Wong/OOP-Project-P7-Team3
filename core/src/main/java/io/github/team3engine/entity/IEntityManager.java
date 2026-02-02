package io.github.team3engine.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Contract for managing entities: add, remove, lookup, update, and render.
 * Allows swapping implementations (e.g. for tests or a spatial/pooled manager) without changing dependents.
 */
public interface IEntityManager {
    void addEntity(Entity e);

    void clear();

    Entity getById(String id);

    Array<Entity> getAll();

    Array<CollidableEntity> getCollidables();

    Array<NonCollidableEntity> getNonCollidables();

    void updateAll(float dt);

    void renderAll(SpriteBatch batch);
}

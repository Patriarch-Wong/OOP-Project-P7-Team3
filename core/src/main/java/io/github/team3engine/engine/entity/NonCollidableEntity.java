package io.github.team3engine.engine.entity;

public abstract class NonCollidableEntity extends Entity {

    public NonCollidableEntity(String id) {
        super(id);
    }

    @Override
    public void update(float dt) {
    }
}

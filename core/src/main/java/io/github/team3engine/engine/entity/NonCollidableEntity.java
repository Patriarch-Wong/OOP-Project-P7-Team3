package io.github.team3engine.engine.entity;

public abstract class NonCollidableEntity extends Entity {

    public NonCollidableEntity(String id) {
        super(id);
    }

    @Override
    public void update(float dt) {
        // Default update behavior for non-collidable entities if needed
        // UML shows update(dt: float) : void, but as an abstract method in Entity, we
        // must implement it or keep abstract
        // UML for NonCollidableEntity has +update(dt: float) : void explicitly
    }
}

package io.github.team3engine.interfaces;

import com.badlogic.gdx.math.Rectangle;

//for object that can collide
public interface Collidable {
    Rectangle getHitbox();

    //when collides with others
    void onCollision(Collidable other);
}

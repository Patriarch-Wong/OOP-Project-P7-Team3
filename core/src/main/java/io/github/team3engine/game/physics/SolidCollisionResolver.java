package io.github.team3engine.game.physics;

import com.badlogic.gdx.math.Rectangle;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;

public final class SolidCollisionResolver {
    /**
     * Extra push-out factor along the X axis to prevent the mover from
     * immediately re-entering the solid on the next frame due to
     * floating-point rounding.
     */
    private static final float HORIZONTAL_PUSH_MARGIN = 1.1f;

    /**
     * Extra push-out factor along the Y axis. Slightly smaller than X
     * because vertical corrections (landing/ceiling) are more visually
     * noticeable and a large margin would cause jitter.
     */
    private static final float VERTICAL_PUSH_MARGIN = 1.05f;

    private SolidCollisionResolver() {}

    public static void resolve(CollidableEntity solid, Collidable other) {
        if (!(other instanceof CollidableEntity))
            return;

        CollidableEntity mover = (CollidableEntity) other;
        Rectangle a = solid.getHitbox();
        Rectangle b = mover.getHitbox();

        if (a == null || b == null)
            return;
        if (!a.overlaps(b))
            return;

        // compute penetration amounts on each axis
        float aLeft = a.x;
        float aRight = a.x + a.width;
        float aBottom = a.y;
        float aTop = a.y + a.height;

        float bLeft = b.x;
        float bRight = b.x + b.width;
        float bBottom = b.y;
        float bTop = b.y + b.height;

        float overlapX = Math.min(aRight, bRight) - Math.max(aLeft, bLeft);
        float overlapY = Math.min(aTop, bTop) - Math.max(aBottom, bBottom);

        if (overlapX <= 0 || overlapY <= 0)
            return; // no overlap

        // centers for direction
        float aCenterX = a.x + a.width * 0.5f;
        float aCenterY = a.y + a.height * 0.5f;
        float bCenterX = b.x + b.width * 0.5f;
        float bCenterY = b.y + b.height * 0.5f;

        if (overlapX < overlapY) {
            // push along X
            float dx = (bCenterX < aCenterX) ? -overlapX * HORIZONTAL_PUSH_MARGIN : overlapX * HORIZONTAL_PUSH_MARGIN;
            mover.setPos(mover.getPos().x + dx, mover.getPos().y);
            mover.setVelocity(0f, mover.getVelocity().y);
        } else {
            // push along Y - add extra margin to ensure complete separation
            float dy = (bCenterY < aCenterY) ? -overlapY * VERTICAL_PUSH_MARGIN : overlapY * VERTICAL_PUSH_MARGIN;
            mover.setPos(mover.getPos().x, mover.getPos().y + dy);
            mover.setVelocity(mover.getVelocity().x, 0f);
        }
    }
}

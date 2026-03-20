package io.github.team3engine.game.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.game.interfaces.Solid;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.game.entities.Circle;
import io.github.team3engine.game.entities.Player;

/**
 * Shared ground and ceiling detection logic for scenes with a Circle player.
 * Extracted from Scene1/Scene2 to eliminate duplication.
 */
public class GroundDetector {
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final EntityManager entityManager;

    public GroundDetector(MovementManager movementManager, CollisionManager collisionManager,
                          EntityManager entityManager) {
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
        this.entityManager = entityManager;
    }

    /**
     * Check if the player is hitting a solid surface while jumping upward,
     * and cancel upward velocity if so.
     */
    public void checkFallCondition(Circle player) {
        Array<Collidable[]> collisionPairs = collisionManager.resolveCollisions();
        for (Collidable[] pair : collisionPairs) {
            if (pair == null || pair.length < 2)
                continue;

            Collidable a = pair[0], b = pair[1];

            if (a != player && b != player)
                continue;
            Collidable other = (a == player) ? b : a;
            if (!(other instanceof Solid))
                continue;
            if (movementManager.isMovingUpward(player.getMovementState()) &&
                player.getY() + player.getRadius() <= other.getHitbox().y + 2f) {
                movementManager.cancelUpwardVelocity(player.getMovementState());
                break;
            }
        }
    }

    /**
     * Detect whether the player is on the floor or on a solid surface,
     * and update grounded state accordingly. Also handles ceiling hits.
     */
    public void checkGroundDetection(Circle player) {
        boolean isOnFloor = player.getY() <= player.getRadius() + 1f;
        boolean isOnPlatform = false;
        float circleBottom = player.getY() - player.getRadius();
        float sinkTolerance = 5f;

        for (Entity e : entityManager.getAll()) {
            if (!(e instanceof Solid)) continue;
            Collidable solid = (Collidable) e;
            Rectangle box = solid.getHitbox();

            float platformTop = box.y + box.height;
            float platformLeft = box.x;
            float platformRight = box.x + box.width;
            float circleCenterX = player.getX();
            boolean landed = circleBottom <= platformTop + sinkTolerance
                    && circleBottom >= platformTop - sinkTolerance;
            boolean overPlatform = circleCenterX >= platformLeft - player.getRadius()
                    && circleCenterX <= platformRight + player.getRadius();
            if (landed && overPlatform) {
                isOnPlatform = true;
                break;
            }
        }

        if (isOnFloor || isOnPlatform) {
            movementManager.setGrounded(player.getMovementState(), true);
        } else {
            movementManager.setGrounded(player.getMovementState(), false);
        }

        if (player.touchesCeiling(entityManager)) {
            movementManager.hitCeiling(player.getMovementState());
        }
    }

    /**
     * Check if the Player is hitting a solid surface while jumping upward,
     * and cancel upward velocity if so.
     */
    public void checkFallCondition(Player player) {
        Array<Collidable[]> collisionPairs = collisionManager.resolveCollisions();
        for (Collidable[] pair : collisionPairs) {
            if (pair == null || pair.length < 2) continue;

            Collidable a = pair[0], b = pair[1];
            if (a != player && b != player) continue;
            Collidable other = (a == player) ? b : a;
            if (!(other instanceof Solid)) continue;
            if (movementManager.isMovingUpward(player.getMovementState()) &&
                player.getY() + player.getHeight() <= other.getHitbox().y + 2f) {
                movementManager.cancelUpwardVelocity(player.getMovementState());
                break;
            }
        }
    }

    /**
     * Detect whether the Player is on the floor or on a solid surface,
     * and update grounded state accordingly. Also handles ceiling hits.
     */
    public void checkGroundDetection(Player player) {
        boolean isOnPlatform = false;
        float playerBottom = player.getY();
        float sinkTolerance = 5f;

        for (Entity e : entityManager.getAll()) {
            if (!(e instanceof Solid)) continue;
            Collidable solid = (Collidable) e;
            Rectangle box = solid.getHitbox();

            float platformTop = box.y + box.height;
            float platformLeft = box.x;
            float platformRight = box.x + box.width;
            float playerCenterX = player.getX();
            boolean landed = playerBottom <= platformTop + sinkTolerance
                    && playerBottom >= platformTop - sinkTolerance;
            boolean overPlatform = playerCenterX >= platformLeft - player.getWidth() / 2f
                    && playerCenterX <= platformRight + player.getWidth() / 2f;
            if (landed && overPlatform) {
                isOnPlatform = true;
                break;
            }
        }

        if (isOnPlatform) {
            movementManager.setGrounded(player.getMovementState(), true);
        } else {
            movementManager.setGrounded(player.getMovementState(), false);
        }

        if (player.touchesCeiling(entityManager)) {
            movementManager.hitCeiling(player.getMovementState());
        }
    }
}

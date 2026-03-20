package io.github.team3engine.game.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.game.interfaces.Solid;
import io.github.team3engine.engine.movement.MovementState;

/**
 * A collidable entity with a circular shape.
 * Movement is driven by the engine (MovementManager + MovementInput), not by
 * this entity.
 * The hitbox is the axis-aligned bounding box of the circle for compatibility
 * with the existing collision system.
 */
public class Circle extends CollidableEntity {

    protected float radius;
    protected float moveSpeed = 220f;
    protected final com.badlogic.gdx.math.Circle circle;
    protected final ShapeRenderer shapeRenderer;
    protected Color color;
    private final float screenWidth;
    private final float screenHeight;
    // Movement state for this entity
    private MovementState movementState;


    public Circle(String id, float radius, float screenWidth, float screenHeight) {
        super(id);
        this.radius = radius;
        this.movementState = new MovementState();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.circle = new com.badlogic.gdx.math.Circle(position.x, position.y, radius);
        this.shapeRenderer = new ShapeRenderer();
        this.color = new Color(1f, 1f, 1f, 1f);
        updateHitbox();
    }

    public Circle(String id, float x, float y, float radius, float screenWidth, float screenHeight) {
        this(id, radius, screenWidth, screenHeight);
        setPos(x, y);
        updateHitbox();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        circle.setRadius(radius);
        updateHitbox();
    }

    public com.badlogic.gdx.math.Circle getCircle() {
        return circle;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    /**
     * Get the movement state for this entity.
     * Used by MovementManager to track velocity, grounded status, etc.
     */
    public MovementState getMovementState() {
        return movementState;
    }

    @Override
    protected void updateHitbox() {
        circle.setPosition(position.x, position.y);
        circle.setRadius(radius);
        hitbox.setPosition(position.x - radius, position.y - radius);
        hitbox.setSize(2f * radius, 2f * radius);
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void update(float dt) {
        // Keep circle on screen (border clamp)
        float w = screenWidth;
        float h = screenHeight;
        position.x = Math.max(radius, Math.min(w - radius, position.x));
        position.y = Math.max(radius, Math.min(h - radius, position.y));

        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(position.x, position.y, radius);
        shapeRenderer.end();

        // Debug: Draw hitbox outline
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // shapeRenderer.setColor(0f, 1f, 0f, 1f); // Green outline for circle hitbox
        // shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        // shapeRenderer.end();

        batch.begin();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
    }

    public boolean touchesCeiling(EntityManager entityManager) {
        float circleTop = this.getPos().y + this.getRadius();
        float tolerance = 6f;

        for (Entity e : entityManager.getAll()) {
            if (!(e instanceof Solid))
                continue;
            CollidableEntity solid = (CollidableEntity) e;
            com.badlogic.gdx.math.Rectangle box = solid.getHitbox();

            float platformBottom = box.y;
            float platformLeft = box.x;
            float platformRight = box.x + box.width;
            float circleX = this.getPos().x;

            boolean underPlatform = circleTop >= platformBottom - tolerance &&
                    circleTop <= platformBottom + tolerance;

            boolean horizontallyAligned = circleX >= platformLeft - this.getRadius() &&
                    circleX <= platformRight + this.getRadius();

            if (underPlatform && horizontallyAligned) {
                return true;
            }
        }
        return false;
    }
}

package io.github.team3engine.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.team3engine.interfaces.Collidable;

/**
 * A winning objective entity - a square that the player must reach.
 * Spawns at a random position on the screen and has collision detection.
 */
public class WinBox extends CollidableEntity {
    private final float size;
    private final ShapeRenderer shapeRenderer;
    private Color color;

    /**
     * Create a WinBox 
     * @param id Entity identifier
     * @param size Width and height of the square
     */
    public WinBox(String id, float size) {
        super(id);
        this.size = size;
        this.shapeRenderer = new ShapeRenderer();
        this.color = new Color(0.2f, 0.8f, 0.2f, 1f); // Green color
        setPos(550, 0);
        updateHitbox();
    }

    public float getSize() {
        return size;
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

    @Override
    protected void updateHitbox() {
        // Square hitbox
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(size, size);
    }

    @Override
    public void update(float dt) {
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(position.x, position.y, size, size);
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        if (!(other instanceof CollidableEntity)) return;

        CollidableEntity ce = (CollidableEntity) other;
        Rectangle a = this.getHitbox();
        Rectangle b = ce.getHitbox();

        if (a == null || b == null) return;
        if (!a.overlaps(b)) return;

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

        if (overlapX <= 0 || overlapY <= 0) return; // no overlap

        // centers for direction
        float aCenterX = a.x + a.width * 0.5f;
        float aCenterY = a.y + a.height * 0.5f;
        float bCenterX = b.x + b.width * 0.5f;
        float bCenterY = b.y + b.height * 0.5f;

        if (overlapX < overlapY) {
            // push along X
            float dx = (bCenterX < aCenterX) ? -overlapX : overlapX;
            ce.setPos(ce.getPos().x + dx, ce.getPos().y);
            ce.setVelocity(0f, ce.getVelocity().y);
        } else {
            // push along Y
            float dy = (bCenterY < aCenterY) ? -overlapY : overlapY;
            ce.setPos(ce.getPos().x, ce.getPos().y + dy);
            ce.setVelocity(ce.getVelocity().x, 0f);
        }

        // Ensure hitbox is updated after resolving
        ce.updateHitbox();
    }
}

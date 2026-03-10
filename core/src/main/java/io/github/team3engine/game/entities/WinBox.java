package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.io.IOManager;

/**
 * A winning objective entity - triggers victory event on contact with any CollidableEntity.
 */
public class WinBox extends CollidableEntity {
    private final float size;
    private final ShapeRenderer shapeRenderer;
    private Color color;
    private final IOManager io;
    
    public WinBox(String id, float x, float y, float size, IOManager io) {
        super(id);
        this.size = size;
        this.io = io;
        this.shapeRenderer = new ShapeRenderer();
        this.color = new Color(0.2f, 0.8f, 0.2f, 1f);

        setPos(x, y);
        updateHitbox();
    }

    @Override
    protected void updateHitbox() {
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
    public void onCollision(Collidable other) {
        if (!(other instanceof CollidableEntity)) return;

        CollidableEntity entity = (CollidableEntity) other;

        io.broadcast("PLAYER_WIN");

        // Resolve overlap so the entity doesn't phase through the box
        float aLeft = hitbox.x;
        float aRight = hitbox.x + hitbox.width;
        float aBottom = hitbox.y;
        float aTop = hitbox.y + hitbox.height;

        float bLeft = entity.getHitbox().x;
        float bRight = entity.getHitbox().x + entity.getHitbox().width;
        float bBottom = entity.getHitbox().y;
        float bTop = entity.getHitbox().y + entity.getHitbox().height;

        float overlapX = Math.min(aRight, bRight) - Math.max(aLeft, bLeft);
        float overlapY = Math.min(aTop, bTop) - Math.max(aBottom, bBottom);

        if (overlapX > 0 && overlapY > 0) {
            if (overlapX < overlapY) {
                float dx = (entity.getPos().x < (hitbox.x + hitbox.width / 2)) ? -overlapX : overlapX;
                entity.setPos(entity.getPos().x + dx, entity.getPos().y);
                entity.setVelocity(0f, entity.getVelocity().y);
            } else {
                float dy = (entity.getPos().y < (hitbox.y + hitbox.height / 2)) ? -overlapY : overlapY;
                entity.setPos(entity.getPos().x, entity.getPos().y + dy);
                entity.setVelocity(entity.getVelocity().x, 0f);
            }
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
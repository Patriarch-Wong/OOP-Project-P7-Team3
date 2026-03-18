package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Solid;

/**
 * A winning objective entity - triggers victory event on contact with any
 * CollidableEntity.
 */
public class WinBox extends CollidableEntity implements Solid {
    private final float sizeX;
    private final float sizeY;
    private final ShapeRenderer shapeRenderer;
    private Color color;

    public WinBox(String id, float x, float y, float sizeX, float sizeY) {
        super(id);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.shapeRenderer = new ShapeRenderer();
        this.color = new Color(0.2f, 0.8f, 0.2f, 1f);

        setPos(x, y);
        updateHitbox();
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(sizeX, sizeY);
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
        shapeRenderer.rect(position.x, position.y, sizeX, sizeY);
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void onCollision(Collidable other) {
        Solid.super.onCollision(other); // push-out

    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
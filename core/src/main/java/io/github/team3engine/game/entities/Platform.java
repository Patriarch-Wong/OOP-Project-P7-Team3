package io.github.team3engine.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Solid;

/**
 * Rectangular platform. Supports either loading its own Texture (by path)
 * or reusing a shared Texture instance (won't dispose shared texture).
 *
 * Collision resolution: pushes the other CollidableEntity out of overlap
 * along the minimal penetration axis and zeroes velocity on that axis.
 */
public class Platform extends CollidableEntity implements Solid {
    private final Texture texture;
    private final boolean ownsTexture;
    private final float width;
    private final float height;
    private final ShapeRenderer shape;
    private final Color color;

    /**
     * Create a platform that loads its own texture from the given path.
     * Platform will dispose this texture when disposed.
     */
    public Platform(String id, float x, float y, float width, float height, String texturePath) {
        super(id);
        if (texturePath != null) {
            this.texture = new Texture(Gdx.files.internal(texturePath));
            this.ownsTexture = true;
        } else {
            this.texture = null;
            this.ownsTexture = false;
        }
        this.width = width;
        this.height = height;
        this.shape = new ShapeRenderer();
        this.color = new Color(0.3f, 0.3f, 0.8f, 1f);
        setPos(x, y);
        updateHitbox();
    }

    /**
     * Create a platform that uses a shared Texture instance.
     * The sharedTexture will NOT be disposed by this Platform.
     */
    public Platform(String id, float x, float y, float width, float height, Texture sharedTexture) {
        super(id);
        this.texture = sharedTexture;
        this.ownsTexture = false;
        this.width = width;
        this.height = height;
        this.shape = new ShapeRenderer();
        this.color = new Color(0.3f, 0.3f, 0.8f, 1f);
        setPos(x, y);
        updateHitbox();
    }

    /**
     * Convenience constructor without texture.
     */
    public Platform(String id, float x, float y, float width, float height) {
        this(id, x, y, width, height, (String) null);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    protected void updateHitbox() {
        // Platform hitbox is bottom-left anchored at position
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(width, height);
    }

    @Override
    public void update(float dt) {
        // Static platform: keep hitbox in sync if position changes
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        if (texture != null) {
            batch.begin();
            batch.draw(texture, position.x, position.y, width, height);
            batch.end();
        } else {
            shape.setProjectionMatrix(batch.getProjectionMatrix());
            shape.setTransformMatrix(batch.getTransformMatrix());
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(color);
            shape.rect(position.x, position.y, width, height);
            shape.end();
        }

        // Debug: Draw hitbox outline
        // shape.setProjectionMatrix(batch.getProjectionMatrix());
        // shape.setTransformMatrix(batch.getTransformMatrix());
        // shape.begin(ShapeRenderer.ShapeType.Line);
        // shape.setColor(1f, 0f, 0f, 1f); // Red outline
        // shape.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        // shape.end();

        batch.begin();
    }

    @Override
    public void dispose() {
        // Only dispose texture if this instance owns it (loaded it).
        if (ownsTexture && texture != null) {
            texture.dispose();
        }
        if (shape != null) {
            shape.dispose();
        }
    }

    @Override
    public void onCollision(Collidable other) {
        Solid.super.onCollision(other); // push-out
    }
}

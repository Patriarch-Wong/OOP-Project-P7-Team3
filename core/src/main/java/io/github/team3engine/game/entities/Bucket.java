package io.github.team3engine.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;

/**
 * A collidable entity that displays and acts as a bucket (e.g. for catching drops).
 * Renders using the bucket texture; hitbox matches texture size at position.
 */
public class Bucket extends CollidableEntity {

    private final Texture texture;
    private final float width;
    private final float height;

    public Bucket(String id, String texturePath, float x, float y) {
        super(id);
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        setPos(x, y);
        updateHitbox();
    }

    public Bucket(String id, float x, float y) {
        this(id, "bucket.png", x, y);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(width, height);
    }

    @Override
    public void update(float dt) {
        // Bucket does not move; just keep hitbox in sync
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        // Collision sound is played by CollisionManager; no extra behavior here
    }
}

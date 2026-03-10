package io.github.team3engine.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.movement.MovementState;

/**
 * A collidable entity that displays and acts as a bucket (e.g. for catching
 * drops).
 * Renders using the bucket texture; hitbox matches texture size at position.
 */
public class Bucket extends CollidableEntity {

    private final Texture texture;
    private final float width;
    private final float height;

    // Movement state for AI-controlled movement
    private MovementState movementState;
    private final float screenWidth;
    private final float screenHeight;

    public Bucket(String id, String texturePath, float x, float y, float screenWidth, float screenHeight) {
        super(id);
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        this.movementState = new MovementState();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setPos(x, y);
        updateHitbox();
    }

    public Bucket(String id, float x, float y, float screenWidth, float screenHeight) {
        this(id, "bucket.png", x, y, screenWidth, screenHeight);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    /**
     * Get the movement state for this bucket.
     * Used by MovementManager for AI-controlled movement.
     */
    public MovementState getMovementState() {
        return movementState;
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(width, height);
    }

    @Override
    public void update(float dt) {
        // Update hitbox to match position (position may be changed by MovementManager)
        // Keep on screen (border clamp)
        float w = screenWidth;
        float h = screenHeight;
        position.x = Math.max(0, Math.min(w - this.getWidth(), position.x));
        position.y = Math.max(0, Math.min(h - this.getHeight(), position.y));

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
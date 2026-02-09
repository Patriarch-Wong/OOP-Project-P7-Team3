package io.github.team3engine.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;

import java.util.Random;

/**
 * Bullet entity - collidable. Plays a SFX on collision.
 * Currently renders "randomly" each frame: sometimes texture, sometimes a shape with jitter.
 */
public class Bullet extends CollidableEntity {
    private final Texture texture;
    private final float width;
    private final float height;
    private final ShapeRenderer shape;
    private final Color color;
    private final Random random;
    private final AudioManager audioManager;
    private final String hitSfx = "bullet_hit.mp3"; // put this in assets/audio/

    public Bullet(String id, float x, float y, String texturePath, AudioManager audioManager) {
        super(id);
        this.texture = texturePath != null ? new Texture(Gdx.files.internal(texturePath)) : null;
        this.width = texture != null ? texture.getWidth() : 8f;
        this.height = texture != null ? texture.getHeight() : 8f;
        this.shape = new ShapeRenderer();
        this.color = new Color(1f, 0.6f, 0f, 1f); // orange by default
        this.random = new Random();
        this.audioManager = audioManager;
        setPos(x, y);
        updateHitbox();
    }

    public Bullet(String id, float x, float y, AudioManager audioManager) {
        this(id, x, y, null, audioManager);
    }

    @Override
    protected void updateHitbox() {
        // simple AABB based on width/height
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(width, height);
    }

    @Override
    public void update(float dt) {
        // movement handled by velocity on CollidableEntity
        super.update(dt);
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        // Entities are rendered inside an active SpriteBatch in Main.
        // Follow Circle's pattern: end batch, draw with ShapeRenderer or draw texture, then re-begin batch.
        batch.end();

        boolean useTexture = texture != null && random.nextBoolean();

        if (useTexture) {
            // draw the texture with a small random offset to look "random"
            float ox = (random.nextFloat() - 0.5f) * 4f;
            float oy = (random.nextFloat() - 0.5f) * 4f;
            batch.begin();
            batch.draw(texture, position.x + ox, position.y + oy, width, height);
            batch.end();
        } else {
            // draw a jittery circle/rectangle
            shape.setProjectionMatrix(batch.getProjectionMatrix());
            shape.setTransformMatrix(batch.getTransformMatrix());
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(color);
            float jitterX = position.x + (random.nextFloat() - 0.5f) * 6f;
            float jitterY = position.y + (random.nextFloat() - 0.5f) * 6f;
            float radius = Math.max(2f, Math.min(width, height) * 0.5f);
            shape.circle(jitterX, jitterY, radius);
            shape.end();
        }

        // restore SpriteBatch so other entities can continue drawing
        batch.begin();
    }

    @Override
    public void dispose() {
        if (texture != null) texture.dispose();
        if (shape != null) shape.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        // Play bullet-specific sound if we have an audio manager
        if (audioManager != null) {
            audioManager.play(hitSfx);
        }
        // optional: mark destroyed if desired
        // this.destroy();
    }
}

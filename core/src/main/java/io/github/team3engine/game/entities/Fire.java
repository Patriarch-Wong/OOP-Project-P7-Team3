package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;

/**
 * Static fire hazard. Deals damage to any Damageable entity on contact.
 */
public class Fire extends CollidableEntity {
    private final float width;
    private final float height;
    private final float damage;
    private final Texture texture;
    private final boolean ownsTexture;
    private final Animation<TextureRegion> animation;
    private float stateTime = 0f;
    private float scaleX = 1f;
    private float scaleY = 1f;
    private float maxScaleX = 3f;
    private float maxScaleY = 2f;
    private final boolean upsideDown;

    public Fire(String id, float x, float y, float width, float height, float damage,
            Texture fireTex, int frameCols, int frameRows, boolean upsideDown) {
        super(id);
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.texture = fireTex;
        // Texture is provided externally (shared); this Fire does not own it
        this.ownsTexture = false;
        this.upsideDown = upsideDown;
        int frameWidth = texture.getWidth() / frameCols;
        int frameHeight = texture.getHeight() / frameRows;
        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];

        int idx = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[idx++] = tmp[i][j];
            }
        }

        this.animation = new Animation<>(0.15f, frames);

        setPos(x, y);
        updateHitbox();
    }

    public Fire(String id, float x, float y, float width, float height, Texture fireTex, int frameCols, int frameRows,
            boolean upsideDown) {
        this(id, x, y, width, height, 20f, fireTex, frameCols, frameRows, upsideDown);
    }

    public float getDamage() {
        return damage;
    }

    public void setMaxScale(float maxScaleX, float maxScaleY) {
        this.maxScaleX = Math.max(1f, maxScaleX);
        this.maxScaleY = Math.max(1f, maxScaleY);
        scaleX = clamp(scaleX, 1f, this.maxScaleX);
        scaleY = clamp(scaleY, 1f, this.maxScaleY);
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = clamp(scaleX, 1f, maxScaleX);
        this.scaleY = clamp(scaleY, 1f, maxScaleY);
    }

    public void addScale(float dx, float dy) {
        setScale(scaleX + dx, scaleY + dy);
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    @Override
    protected void updateHitbox() {
        float scaledWidth = width * scaleX;
        float scaledHeight = height * scaleY;

        float hitboxY = upsideDown ? position.y - scaledHeight : position.y;
        hitbox.setPosition(position.x, hitboxY);
        hitbox.setSize(scaledWidth, scaledHeight);
    }

    @Override
    public void update(float dt) {
        stateTime += dt;
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        float scaledWidth = width * scaleX;
        float scaledHeight = height * scaleY;
        if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(stateTime, true);
            if (frame != null) {
                TextureRegion drawFrame = new TextureRegion(frame);
                if (upsideDown) {
                    drawFrame.flip(false, true);
                }
                float drawY = upsideDown ? position.y - scaledHeight : position.y;
                batch.draw(drawFrame, position.x, drawY, scaledWidth, scaledHeight);
                return;
            }
        }
        batch.draw(texture, position.x, position.y, scaledWidth, scaledHeight);
    }

    @Override
    public void dispose() {
        if (ownsTexture && texture != null) {
            texture.dispose();
        }
    }

    @Override
    public void onCollision(Collidable other) {
        // Game logic handled by CollisionMediator
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}

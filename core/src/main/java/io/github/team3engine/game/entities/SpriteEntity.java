package io.github.team3engine.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;

/**
 * Generic sprite entity that can render either a single texture
 * or a sprite sheet animation. Configure it directly in the scene.
 */
public class SpriteEntity extends CollidableEntity {
    private final Texture texture;
    private final Animation<TextureRegion> animation;
    private final float width;
    private final float height;
    private final boolean is_hazard;
    private float stateTime;

    // constructor for static texture sprite
    public SpriteEntity(String id, float x, float y, String texturePath,
                        float scaleX, float scaleY, boolean is_hazard) {
        super(id);
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.animation = null;
        this.width = texture.getWidth() * scaleX;
        this.height = texture.getHeight() * scaleY;
        this.is_hazard = is_hazard;
        this.position = new Vector2(x, y);
        this.stateTime = 0f;
        updateHitbox();
    }

    // constructor for animated sprite
    public SpriteEntity(String id, float x, float y, String spritesheetPath,
                        int frameCols, int frameRows, float frameDuration,
                        float scaleX, float scaleY, boolean is_hazard) {
        super(id);
        this.texture = new Texture(Gdx.files.internal(spritesheetPath));
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
        this.animation = new Animation<>(frameDuration, frames);
        this.width = frameWidth * scaleX;
        this.height = frameHeight * scaleY;
        this.is_hazard = is_hazard;
        this.position = new Vector2(x, y);
        this.stateTime = 0f;
        updateHitbox();
    }

    public boolean isHazard() {
        return is_hazard;
    }

    @Override
    public void update(float dt) {
        stateTime += dt;
        updateHitbox();
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(width, height);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(stateTime, true);
            batch.draw(frame, position.x, position.y, width, height);
            return;
        }
        batch.draw(texture, position.x, position.y, width, height);
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        // No default behavior; scenes decide what to do with hazards.
    }
}

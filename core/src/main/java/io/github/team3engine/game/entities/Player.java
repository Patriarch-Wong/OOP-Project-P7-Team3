package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Damageable;
import io.github.team3engine.engine.interfaces.Solid;
import io.github.team3engine.engine.movement.MovementState;
import io.github.team3engine.engine.status.StatusEffectManager;
import io.github.team3engine.game.ui.FloatingText;
import io.github.team3engine.game.status.DamageReductionEffect;
import io.github.team3engine.game.status.SlowEffect;

import java.util.List;
import java.util.ArrayList;

public class Player extends CollidableEntity implements Damageable {
    private final float width;
    private final float height;
    private float baseSpeed = 220f;
    private final Texture texture;
    private final float screenWidth;
    private final float screenHeight;

    // Animation
    private final List<Texture> allTextures = new ArrayList<>();
    private final TextureRegion idleFrameEast;
    private final TextureRegion idleFrameWest;
    private final Animation<TextureRegion> runAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> crouchAnimation;
    private float stateTime = 0f;
    private float crouchStateTime = 0f;
    private boolean wasCrouching = false;
    private boolean facingRight = true;

    // HP system
    private float hp;
    private float maxHp;
    private float invincibilityTimer = 0f;
    private static final float INVINCIBILITY_DURATION = 3.0f;

    // Floating damage text
    private final FloatingText damageText;

    // Status effects
    private final StatusEffectManager statusEffects;

    // Movement
    private final MovementState movementState;

    // NPC carry
    private boolean carryingNPC = false;
    private int rescuedCount = 0;

    public Player(String id, float x, float y, float width, float height,
                  float maxHp, float screenWidth, float screenHeight) {
        super(id);
        this.width = width;
        this.height = height;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.movementState = new MovementState();
        this.statusEffects = new StatusEffectManager(this);
        this.texture = new Texture("player.png");
        this.damageText = new FloatingText(1.2f, new Color(1f, 0.2f, 0.2f, 1f), 1.2f, new Vector2(0f, 20f));
        
        // Idle frames (single image per direction)
        Texture idleE = loadTexture("player/rotations/east.png");
        Texture idleW = loadTexture("player/rotations/west.png");
        this.idleFrameEast = new TextureRegion(idleE);
        this.idleFrameWest = new TextureRegion(idleW);

        // Running animation (6 frames, east only — flip for west)
        this.runAnimation = loadFrameAnimation("player/animations/running-6-frames/east/frame_", 6, 0.1f);

        // Jumping animation (9 frames)
        this.jumpAnimation = loadFrameAnimation("player/animations/jumping-1/east/frame_", 9, 0.08f);

        // Crouching animation (5 frames)
        this.crouchAnimation = loadFrameAnimation("player/animations/crouching/east/frame_", 5, 0.12f);
        
        setPos(x, y);
        updateHitbox();
    }

    // --- Damageable ---

    @Override
    public void takeDamage(float amount) {
        if (!isAlive() || isInvincible()) return;

        // Apply damage reduction from all active effects (multiplicative stacking)
        List<DamageReductionEffect> reductions = statusEffects.getAllEffects(DamageReductionEffect.class);
        for (DamageReductionEffect reduction : reductions) {
            amount *= reduction.getDamageMultiplier();
        }

        // final damage after buffs/debuffs
        float finalDamage = amount;

        if (finalDamage > 0.01f) {
            damageText.show(String.format("-%.0f", finalDamage));
            hp = Math.max(0f, hp - finalDamage);
            invincibilityTimer = INVINCIBILITY_DURATION;
        }
    }

    @Override
    public void heal(float amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    @Override
    public float getHp() { return hp; }

    @Override
    public float getMaxHp() { return maxHp; }

    @Override
    public boolean isAlive() { return hp > 0f; }

    @Override
    public boolean isInvincible() { return invincibilityTimer > 0f; }

    // --- Status Effects ---

    public StatusEffectManager getStatusEffects() { return statusEffects; }

    /** Get effective move speed after applying slow effects. */
    public float getEffectiveSpeed() {
        float speed = baseSpeed;
        SlowEffect slow = statusEffects.getEffect(SlowEffect.class);
        if (slow != null) {
            speed *= slow.getSpeedMultiplier();
        }
        return speed;
    }

    // --- Movement ---

    public MovementState getMovementState() { return movementState; }

    public float getBaseSpeed() { return baseSpeed; }

    public void setBaseSpeed(float speed) { this.baseSpeed = speed; }

    // --- NPC Carry ---

    public boolean isCarryingNPC() { return carryingNPC; }

    public void pickUpNPC() { this.carryingNPC = true; }

    public void rescueNPC() {
        this.carryingNPC = false;
        this.rescuedCount++;
    }

    public int getRescuedCount() { return rescuedCount; }

    // --- Dimensions ---

    public float getWidth() { return width; }

    public float getHeight() { return height; }

    // --- Entity overrides ---

    @Override
    protected void updateHitbox() {
        // position is bottom-center of the player
        hitbox.setPosition(position.x - width / 2f, position.y);
        hitbox.setSize(width, height);
    }
    
    /** Loads a texture and tracks it for disposal. */
    private Texture loadTexture(String path) {
        Texture t = new Texture(path);
        allTextures.add(t);
        return t;
    }

    /** Loads numbered frame PNGs (frame_000.png … frame_NNN.png) into an Animation. */
    private Animation<TextureRegion> loadFrameAnimation(String pathPrefix, int count, float frameDuration) {
        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            Texture t = loadTexture(pathPrefix + String.format("%03d", i) + ".png");
            frames[i] = new TextureRegion(t);
        }
        return new Animation<>(frameDuration, frames);
    }

    @Override
    public void update(float dt) {
        // Advance animation timer
        stateTime += dt;

        // Track crouch animation timer
        boolean crouchingNow = movementState.isCrouching();
        if (crouchingNow && !wasCrouching) {
            crouchStateTime = 0f;
        }
        if (crouchingNow) {
            crouchStateTime += dt;
        }
        wasCrouching = crouchingNow;

        // Track facing direction
        if (movementState.getVelocityX() > 0) facingRight = true;
        else if (movementState.getVelocityX() < 0) facingRight = false;
        // Tick status effects
        statusEffects.update(dt);

        // Tick invincibility
        if (invincibilityTimer > 0f) {
            invincibilityTimer -= dt;
        }

        // Clamp to screen
        position.x = Math.max(width / 2f, Math.min(screenWidth - width / 2f, position.x));
        position.y = Math.max(0f, Math.min(screenHeight - height, position.y));
        updateHitbox();

        damageText.update(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        // Blink during invincibility (skip render every other 0.1s)
        if (isInvincible()) {
            int blinkPhase = (int) (invincibilityTimer / 0.1f);
            if (blinkPhase % 2 == 0) {
                return; // skip this frame's render
            }
        }
        TextureRegion frame = getCurrentFrame();

        // Flip for facing direction
        if (facingRight && frame.isFlipX()) frame.flip(true, false);
        else if (!facingRight && !frame.isFlipX()) frame.flip(true, false);

        // Scale draw size based on frame aspect ratio, keeping height fixed
        float frameAspect = (float) frame.getRegionWidth() / frame.getRegionHeight();
        float drawHeight = height;
        float drawWidth = drawHeight * frameAspect;

        batch.draw(frame, position.x - drawWidth / 2f, position.y, drawWidth, drawHeight);
        damageText.render(batch, position.x, position.y + height);
    }
    
    /** Picks the correct animation frame based on current movement state. */
    private TextureRegion getCurrentFrame() {
        // Priority: jumping > crouching > running > idle
        if (!movementState.isGrounded()) {
            return jumpAnimation.getKeyFrame(stateTime, false);
        }
        if (movementState.isCrouching()) {
            return crouchAnimation.getKeyFrame(crouchStateTime, false);
        }
        if (movementState.getVelocityX() != 0) {
            return runAnimation.getKeyFrame(stateTime, true);
        }
        return facingRight ? idleFrameEast : idleFrameWest;
    }

    @Override
    public void dispose() {
        for (Texture t : allTextures) {
            t.dispose();
        }
        statusEffects.clearAll();
        damageText.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        // Collision responses handled by the other entity (Fire, Pickup, etc.)
    }

    /**
     * Check if the top of the player touches a solid surface above.
     */
    public boolean touchesCeiling(EntityManager entityManager) {
        float playerTop = this.getY() + this.height;
        float tolerance = 6f;

        for (Entity e : entityManager.getAll()) {
            if (!(e instanceof Solid)) continue;
            CollidableEntity solid = (CollidableEntity) e;
            com.badlogic.gdx.math.Rectangle box = solid.getHitbox();

            float platformBottom = box.y;
            float platformLeft = box.x;
            float platformRight = box.x + box.width;
            float playerCenterX = this.getX();

            boolean underPlatform = playerTop >= platformBottom - tolerance &&
                    playerTop <= platformBottom + tolerance;
            boolean horizontallyAligned = playerCenterX >= platformLeft - width / 2f &&
                    playerCenterX <= platformRight + width / 2f;

            if (underPlatform && horizontallyAligned) {
                return true;
            }
        }
        return false;
    }
}

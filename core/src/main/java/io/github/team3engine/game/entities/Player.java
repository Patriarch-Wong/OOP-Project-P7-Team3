package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.game.interfaces.Damageable;
import io.github.team3engine.game.interfaces.Solid;
import io.github.team3engine.engine.movement.MovementConfig;
import io.github.team3engine.engine.movement.MovementState;
import io.github.team3engine.game.status.StatusEffectManager;
import io.github.team3engine.game.interfaces.Followable;
import io.github.team3engine.game.ui.FloatingText;
import io.github.team3engine.game.status.DamageReductionEffect;
import io.github.team3engine.game.status.SlowEffect;
import io.github.team3engine.game.status.StatusEffectMath;

import java.util.List;

public class Player extends CollidableEntity implements Damageable, Followable {
    private final float width;
    private final float height;
    private float baseSpeed = 220f;
    private final Texture texture;
    private final float worldWidth;
    private final float worldHeight;

    // Animation
    private final PlayerAnimator animator;

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
    private final MovementConfig movementConfig;
    private boolean grounded = true;
    private boolean crouching = false;
    private float jumpCooldownRemaining = 0f;
    private boolean jumpRequested = false;
    private float externalSpeedMultiplier = 1f;

    // NPC carry
    private boolean carryingNPC = false;
    private int rescuedCount = 0;

    private static final float WALK_SPEED = 300f;
    private static final float CRAWL_SPEED = 120f;
    private static final float ACCELERATION = 700f;
    private static final float DECELERATION = 700f;
    private static final float GRAVITY = -1100f;
    private static final float MAX_FALL_SPEED = -600f;
    private static final float JUMP_FORCE = 550f;
    private static final float JUMP_COOLDOWN_DURATION = 0.6f;

    public Player(String id, float x, float y, float width, float height,
                  float maxHp, float worldWidth, float worldHeight) {
        super(id);
        this.width = width;
        this.height = height;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.movementState = new MovementState();
        this.movementConfig = new MovementConfig(
                WALK_SPEED, MAX_FALL_SPEED, ACCELERATION, DECELERATION, GRAVITY);
        this.statusEffects = new StatusEffectManager(this);
        this.texture = new Texture("player.png");
        this.damageText = new FloatingText(1.2f, new Color(1f, 0.2f, 0.2f, 1f), 1.2f, new Vector2(0f, 20f));
        this.animator = new PlayerAnimator();
        setPos(x, y);
        updateHitbox();
    }

    // --- Damageable ---

    @Override
    public void takeDamage(float amount) {
        if (!isAlive() || isInvincible()) return;

        // Apply damage reduction from all active effects (multiplicative stacking)
        List<DamageReductionEffect> reductions = statusEffects.getAllEffects(DamageReductionEffect.class);
        float finalDamage = StatusEffectMath.applyDamageReductions(amount, reductions);

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

    public void kill() {
        hp = 0f;
        invincibilityTimer = 0f;
    }

    // --- Status Effects ---

    public StatusEffectManager getStatusEffects() { return statusEffects; }

    /** Get effective move speed after applying slow effects. */
    public float getEffectiveSpeed() {
        float speed = baseSpeed;
        List<SlowEffect> slows = statusEffects.getAllEffects(SlowEffect.class);
        speed *= StatusEffectMath.strongestSlowMultiplier(slows);
        return speed;
    }

    // --- Movement ---

    public MovementState getMovementState() { return movementState; }
    public MovementConfig getMovementConfig() { return movementConfig; }

    public float getBaseSpeed() { return baseSpeed; }

    public void setBaseSpeed(float speed) { this.baseSpeed = speed; }

    public boolean consumeJumpRequest() {
        boolean requested = jumpRequested;
        jumpRequested = false;
        return requested;
    }

    public boolean isCrouching() {
        return crouching;
    }

    public void setCrouching(boolean crouching) {
        this.crouching = crouching && grounded;
        refreshSpeedMultiplier();
        updateHitbox();
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
        if (grounded && movementState.getVelocityY() < 0f) {
            movementState.setVelocityY(0f);
        }
        if (!grounded) {
            this.crouching = false;
            refreshSpeedMultiplier();
        }
        updateHitbox();
    }

    public boolean canJump() {
        return grounded && jumpCooldownRemaining <= 0f && !crouching;
    }

    public void requestJump() {
        if (!canJump()) {
            return;
        }
        jumpRequested = true;
        jumpCooldownRemaining = JUMP_COOLDOWN_DURATION;
    }

    public void tickJumpCooldown(float dt) {
        if (jumpCooldownRemaining <= 0f) {
            jumpCooldownRemaining = 0f;
            return;
        }
        jumpCooldownRemaining = Math.max(0f, jumpCooldownRemaining - dt);
    }

    public float getJumpCooldownRemaining() {
        return jumpCooldownRemaining;
    }

    public void setJumpCooldownRemaining(float jumpCooldownRemaining) {
        this.jumpCooldownRemaining = Math.max(0f, jumpCooldownRemaining);
    }

    public void resetMovementRules() {
        grounded = true;
        crouching = false;
        jumpRequested = false;
        jumpCooldownRemaining = 0f;
        externalSpeedMultiplier = 1f;
        refreshSpeedMultiplier();
        updateHitbox();
    }

    public void updateMovementRules(MovementInput input, float dt) {
        if (input == null) {
            tickJumpCooldown(dt);
            return;
        }
        updateMovementRules(input.isJumpIntent(), input.isCrouchIntent(), dt);
    }

    public void updateMovementRules(boolean jumpIntent, boolean crouchIntent, float dt) {
        tickJumpCooldown(dt);
        setCrouching(crouchIntent);
        if (jumpIntent) {
            requestJump();
        }
    }

    public boolean applyJumpIfRequested() {
        if (!consumeJumpRequest()) {
            return false;
        }
        movementState.setVelocityY(JUMP_FORCE);
        setGrounded(false);
        return true;
    }

    public float getJumpForce() {
        return JUMP_FORCE;
    }

    public void setExternalSpeedMultiplier(float multiplier) {
        externalSpeedMultiplier = Math.max(0f, multiplier);
        refreshSpeedMultiplier();
    }

    private void refreshSpeedMultiplier() {
        float crouchMultiplier = crouching ? (CRAWL_SPEED / WALK_SPEED) : 1f;
        movementState.setSpeedMultiplier(externalSpeedMultiplier * crouchMultiplier);
    }

    // --- NPC Carry ---

    public boolean isCarryingNPC() { return carryingNPC; }

    public void pickUpNPC() { this.carryingNPC = true; }

    public void rescueNPC() {
        this.carryingNPC = false;
        this.rescuedCount++;
    }

    public int getRescuedCount() { return rescuedCount; }

    // --- Dimensions ---

    public float getWidth() { return getDrawWidth(); }
    public boolean isFacingRight() { return animator.isFacingRight(); }

    public float getHeight() { return height; }

    public float getHitboxHeight() {
        return height * (crouching ? CROUCHING_HEIGHT_RATIO : HITBOX_HEIGHT_RATIO);
    }

    // --- Entity overrides ---

    // Fraction of sprite height the character actually occupies (bottom-anchored).
    // The top ~25% of each 48x48 frame is empty space above the head.
    private static final float HITBOX_HEIGHT_RATIO = 0.75f;
    private static final float CROUCHING_HEIGHT_RATIO = 0.45f;

    @Override
    protected void updateHitbox() {
        float hitboxWidth = getDrawWidth();
        float hitboxHeight = getHitboxHeight();
        hitbox.setPosition(position.x - hitboxWidth / 2f, position.y);
        hitbox.setSize(hitboxWidth, hitboxHeight);
    }

    /** Returns the rendered sprite width based on the current animation frame's aspect ratio. */
    private float getDrawWidth() {
        // Before animator is initialized (during constructor), fall back to width field
        if (animator == null || movementState == null) {
            return width;
        }
        TextureRegion frame = animator.getCurrentFrame(this);
        float frameAspect = (float) frame.getRegionWidth() / frame.getRegionHeight();
        return height * frameAspect;
    }

    @Override
    public void update(float dt) {
        // Update animations
        animator.update(this, dt);
        
        // Tick status effects
        statusEffects.update(dt);

        // Tick invincibility
        if (invincibilityTimer > 0f) {
            invincibilityTimer -= dt;
        }

        // Clamp to world bounds using actual draw width
        float halfW = getDrawWidth() / 2f;
        position.x = Math.max(halfW, Math.min(worldWidth - halfW, position.x));
        position.y = Math.min(worldHeight - getHitboxHeight(), position.y);
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
        float drawWidth = getDrawWidth();
        float drawHeight = height;

        TextureRegion frame = animator.getCurrentFrame(this);
        batch.draw(frame, position.x - drawWidth / 2f, position.y, drawWidth, drawHeight);
        damageText.render(batch, position.x, position.y + height);
    }

    @Override
    public void dispose() {
        texture.dispose();
        statusEffects.clearAll();
        animator.dispose();
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
        float playerTop = this.getY() + getHitboxHeight();
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
            float halfDrawW = getDrawWidth() / 2f;
            boolean horizontallyAligned = playerCenterX >= platformLeft - halfDrawW &&
                    playerCenterX <= platformRight + halfDrawW;

            if (underPlatform && horizontallyAligned) {
                return true;
            }
        }
        return false;
    }

}

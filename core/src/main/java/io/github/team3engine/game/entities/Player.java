package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Damageable;
import io.github.team3engine.engine.interfaces.Solid;
import io.github.team3engine.engine.movement.MovementState;
import io.github.team3engine.engine.status.StatusEffectManager;
import io.github.team3engine.game.status.DamageReductionEffect;
import io.github.team3engine.game.status.SlowEffect;

import java.util.List;

public class Player extends CollidableEntity implements Damageable {
    private final float width;
    private final float height;
    private float baseSpeed = 220f;
    private final ShapeRenderer shapeRenderer;
    private Color color;
    private final float screenWidth;
    private final float screenHeight;

    // HP system
    private float hp;
    private float maxHp;
    private float invincibilityTimer = 0f;
    private static final float INVINCIBILITY_DURATION = 3.0f;

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
        this.shapeRenderer = new ShapeRenderer();
        this.color = new Color(0.2f, 0.5f, 0.9f, 1f); // Blue player
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

        hp = Math.max(0f, hp - amount);
        invincibilityTimer = INVINCIBILITY_DURATION;
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

    @Override
    public void update(float dt) {
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

        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        // Draw body (rectangle centered on position.x, bottom at position.y)
        shapeRenderer.rect(position.x - width / 2f, position.y, width, height);
        // Draw head (small circle on top)
        float headRadius = width * 0.35f;
        shapeRenderer.circle(position.x, position.y + height + headRadius * 0.5f, headRadius);
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        statusEffects.clearAll();
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

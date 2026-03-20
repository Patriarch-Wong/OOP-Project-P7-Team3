package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.game.interfaces.Followable;

/**
 * NPC that can be rescued.
 *
 * WAITING   — orange shape + bobbing "HELP!" label
 * FOLLOWING — smoothly lerps behind the player (MapleStory pet style).
 *             All follow behaviour is configurable via constructor.
 */
public class NPC extends CollidableEntity implements io.github.team3engine.game.interfaces.Damageable {

    public enum State { WAITING, FOLLOWING, DEAD }

    private static final float WIDTH      = 20f;
    private static final float HEIGHT     = 36f;
    private static final float ARROW_SIZE = 8f;
    private static final float LABEL_GAP  = 6f;
    private static final float HEALTH_BAR_WIDTH = 30f;
    private static final float HEALTH_BAR_HEIGHT = 4f;
    private static final float HEALTH_BAR_OFFSET_Y = 8f;
    private static final float INVINCIBILITY_DURATION = 0.5f;

    private State state = State.WAITING;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont    font;
    private final GlyphLayout   layout;
    private final PlayerAnimator animator;

    private float bobTimer = 0f;
    private final String name;

    // NPC Health system
    private float hp;
    private float maxHp;
    private float invincibilityTimer = 0f;
    private boolean wasJustDamaged = false;

    // Configurable follow behaviour
    private final float followOffsetX;  // horizontal distance behind player
    private final float followOffsetY;  // vertical offset
    private final float lerpSpeed;      // how fast NPC chases target (higher = snappier)

    private Followable followTarget;
    private float  prevX      = 0f;
    private boolean facingRight = true;
    private boolean isMoving    = false;

    /**
     * @param id            entity id
     * @param x             spawn x
     * @param y             spawn y
     * @param name          display name
     * @param followOffsetX horizontal distance to trail behind player
     * @param followOffsetY vertical offset from player position
     * @param lerpSpeed     follow smoothness — 4 = floaty, 8 = snappy
     * @param maxHp         maximum health for the NPC
     */
    public NPC(String id, float x, float y, String name,
               float followOffsetX, float followOffsetY, float lerpSpeed, float maxHp) {
        super(id);
        this.name          = name;
        this.followOffsetX = followOffsetX;
        this.followOffsetY = followOffsetY;
        this.lerpSpeed     = lerpSpeed;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.shapeRenderer = new ShapeRenderer();
        this.font          = new BitmapFont();
        this.layout        = new GlyphLayout();
        this.animator      = new PlayerAnimator(
            "npc/rotations/east.png", "npc/rotations/west.png",
            "npc/running-6-frames/east/frame_", 6, 0.1f
        );
        setPos(x, y);
        updateHitbox();
    }

    /** Convenience constructor — sensible defaults for the fire escape game. */
    public NPC(String id, float x, float y, String name) {
        this(id, x, y, name, 30f, 0f, 6f, 100f);
    }

    /** Constructor with custom maxHp. */
    public NPC(String id, float x, float y, String name, float maxHp) {
        this(id, x, y, name, 30f, 0f, 6f, maxHp);
    }

    // --- Health System (Damageable interface) ---

    @Override
    public void takeDamage(float amount) {
        if (!isAlive() || isInvincible()) return;

        hp = Math.max(0f, hp - amount);
        invincibilityTimer = INVINCIBILITY_DURATION;
        wasJustDamaged = true;

        if (hp <= 0f) {
            state = State.DEAD;
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
    public boolean isAlive() { return hp > 0f && state != State.DEAD; }

    @Override
    public boolean isInvincible() { return invincibilityTimer > 0f; }

    public boolean hasDied() { return state == State.DEAD; }

    public String  getName()     { return name; }
    public State   getState()    { return state; }
    public boolean isFollowing() { return state == State.FOLLOWING; }

    public void startFollowing(Followable target) {
        this.followTarget = target;
        this.state        = State.FOLLOWING;
        this.prevX        = position.x;
        updateHitbox();
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x - WIDTH / 2f, position.y);
        hitbox.setSize(WIDTH, HEIGHT);
    }

    @Override
    public void update(float dt) {
        bobTimer += dt;

        // Tick invincibility timer
        if (invincibilityTimer > 0f) {
            invincibilityTimer -= dt;
        }
        wasJustDamaged = false;

        // Check for death
        if (state != State.DEAD && hp <= 0f) {
            state = State.DEAD;
        }

        // Don't update position/rendering if dead
        if (state == State.DEAD) {
            return;
        }

        isMoving = false;
        if (state == State.FOLLOWING && followTarget != null) {
            // Target point behind the player based on player's current facing
            float targetX = followTarget.isFacingRight()
                    ? followTarget.getX() - followOffsetX
                    : followTarget.getX() + followOffsetX;
            float targetY = followTarget.getY() + followOffsetY;

            // Smooth lerp
            float alpha = Math.min(lerpSpeed * dt, 1f);
            position.x += (targetX - position.x) * alpha;
            position.y += (targetY - position.y) * alpha;

            // Derive facing from actual movement
            float dx = position.x - prevX;
            if (dx > 0.5f)       facingRight = true;
            else if (dx < -0.5f) facingRight = false;
            prevX = position.x;

            isMoving = Math.abs(dx) > 0.5f;
        }

        animator.update(dt, isMoving, facingRight);
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        if (state == State.DEAD) {
            return;
        }

        if (state == State.WAITING) {
            renderWaiting(batch);
        } else {
            renderFollowing(batch);
        }
    }

    // ── Waiting ───────────────────────────────────────────────────────────

    private void renderWaiting(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Health bar above waiting NPC
        drawHealthBar(batch, position.y + drawHeight + 4f);

        // Bobbing "HELP!" label above sprite
        float helpBob = (float) Math.sin(bobTimer * 4f) * 3f;
        String helpText = "HELP!";
        layout.setText(font, helpText);
        font.setColor(1f, 0.8f, 0f, 1f);
        font.draw(batch, helpText,
                position.x - layout.width / 2f,
                position.y + drawHeight + HEALTH_BAR_HEIGHT + LABEL_GAP + 8f + helpBob);
        font.setColor(Color.WHITE);
    }

    // ── Following ─────────────────────────────────────────────────────────

    private void renderFollowing(SpriteBatch batch) {
        TextureRegion frame = animator.getCurrentFrame(isMoving);

        float frameAspect = (float) frame.getRegionWidth() / frame.getRegionHeight();
        float drawHeight = HEIGHT;
        float drawWidth  = drawHeight * frameAspect;

        batch.draw(frame, position.x - drawWidth / 2f, position.y, drawWidth, drawHeight);

        // Draw health bar above NPC
        drawHealthBar(batch, position.y + drawHeight + 4f);

        drawArrowAndLabel(batch, position.y + drawHeight + HEALTH_BAR_HEIGHT + 8f);
    }

    private void drawHealthBar(SpriteBatch batch, float y) {
        float healthBarX = position.x - HEALTH_BAR_WIDTH / 2f;

        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.8f);
        shapeRenderer.rect(healthBarX, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);

        // Health fill
        float hpFrac = hp / maxHp;
        Color healthColor = getHealthColor(hpFrac);
        shapeRenderer.setColor(healthColor);
        shapeRenderer.rect(healthBarX, y, HEALTH_BAR_WIDTH * hpFrac, HEALTH_BAR_HEIGHT);

        // Border
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 0.5f);
        shapeRenderer.rect(healthBarX, y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        shapeRenderer.end();

        batch.begin();
    }

    private Color getHealthColor(float hpFrac) {
        if (hpFrac > 0.6f) {
            return new Color(0.2f, 0.8f, 0.2f, 1f);
        } else if (hpFrac > 0.3f) {
            return new Color(1f, 0.8f, 0f, 1f);
        } else {
            return new Color(1f, 0.2f, 0.2f, 1f);
        }
    }

    private void drawArrowAndLabel(SpriteBatch batch, float topY) {
        String label = "NPC";
        layout.setText(font, label);
        float labelY = topY + LABEL_GAP + layout.height + ARROW_SIZE + 4f;
        font.setColor(0.4f, 1f, 0.4f, 1f);
        font.draw(batch, label, position.x - layout.width / 2f, labelY);
        font.setColor(Color.WHITE);

        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.4f, 1f, 0.4f, 1f);

        float arrowTopY = topY + LABEL_GAP + ARROW_SIZE;
        float arrowBotY = topY + LABEL_GAP;
        shapeRenderer.triangle(
            position.x - ARROW_SIZE / 2f, arrowTopY,
            position.x + ARROW_SIZE / 2f, arrowTopY,
            position.x,                   arrowBotY
        );
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        animator.dispose();
    }

    @Override
    public void onCollision(Collidable other) {}
}

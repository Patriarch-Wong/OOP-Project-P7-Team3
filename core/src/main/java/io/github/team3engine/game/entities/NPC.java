package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;

/**
 * NPC that can be rescued.
 *
 * WAITING   — orange shape + bobbing "HELP!" label
 * FOLLOWING — smoothly lerps behind the player (MapleStory pet style).
 *             All follow behaviour is configurable via constructor.
 */
public class NPC extends CollidableEntity {

    public enum State { WAITING, FOLLOWING }

    private static final float WIDTH      = 16f;
    private static final float HEIGHT     = 28f;
    private static final float ARROW_SIZE = 8f;
    private static final float LABEL_GAP  = 6f;

    private State state = State.WAITING;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont    font;
    private final GlyphLayout   layout;

    private float bobTimer = 0f;
    private final String name;

    // Configurable follow behaviour
    private final float followOffsetX;  // horizontal distance behind player
    private final float followOffsetY;  // vertical offset
    private final float lerpSpeed;      // how fast NPC chases target (higher = snappier)

    private Player followTarget;
    private float  prevX      = 0f;
    private boolean facingRight = true;

    /**
     * @param id            entity id
     * @param x             spawn x
     * @param y             spawn y
     * @param name          display name
     * @param followOffsetX horizontal distance to trail behind player
     * @param followOffsetY vertical offset from player position
     * @param lerpSpeed     follow smoothness — 4 = floaty, 8 = snappy
     */
    public NPC(String id, float x, float y, String name,
               float followOffsetX, float followOffsetY, float lerpSpeed) {
        super(id);
        this.name          = name;
        this.followOffsetX = followOffsetX;
        this.followOffsetY = followOffsetY;
        this.lerpSpeed     = lerpSpeed;
        this.shapeRenderer = new ShapeRenderer();
        this.font          = new BitmapFont();
        this.layout        = new GlyphLayout();
        setPos(x, y);
        updateHitbox();
    }

    /** Convenience constructor — sensible defaults for the fire escape game. */
    public NPC(String id, float x, float y, String name) {
        this(id, x, y, name, 30f, 0f, 6f);
    }

    public String  getName()     { return name; }
    public State   getState()    { return state; }
    public boolean isFollowing() { return state == State.FOLLOWING; }

    public void startFollowing(Player player) {
        this.followTarget = player;
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
        }

        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
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

        shapeRenderer.setColor(0.95f, 0.61f, 0.07f, 1f);
        shapeRenderer.rect(position.x - WIDTH / 2f, position.y, WIDTH, HEIGHT);

        float headRadius = WIDTH * 0.4f;
        shapeRenderer.circle(position.x, position.y + HEIGHT + headRadius * 0.3f, headRadius);
        shapeRenderer.end();

        batch.begin();
        float helpBob = (float) Math.sin(bobTimer * 4f) * 3f;
        String helpText = "HELP!";
        layout.setText(font, helpText);
        font.setColor(1f, 0.8f, 0f, 1f);
        font.draw(batch, helpText,
                position.x - layout.width / 2f,
                position.y + HEIGHT + headRadius * 2f + 14f + helpBob);
        font.setColor(Color.WHITE);
    }

    // ── Following ─────────────────────────────────────────────────────────

    private void renderFollowing(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Green-tinted body to distinguish from player
        shapeRenderer.setColor(0.3f, 0.8f, 0.3f, 1f);
        shapeRenderer.rect(position.x - WIDTH / 2f, position.y, WIDTH, HEIGHT);

        float headRadius = WIDTH * 0.4f;
        shapeRenderer.setColor(0.5f, 0.9f, 0.5f, 1f);
        shapeRenderer.circle(position.x, position.y + HEIGHT + headRadius * 0.3f, headRadius);
        shapeRenderer.end();

        batch.begin();
        drawArrowAndLabel(batch, position.y + HEIGHT + headRadius * 2f);
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
    }

    @Override
    public void onCollision(Collidable other) {}
}

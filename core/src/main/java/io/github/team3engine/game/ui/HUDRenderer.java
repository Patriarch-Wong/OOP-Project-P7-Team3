package io.github.team3engine.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Unified HUD renderer for all in-game overlays.
 * Handles: HP bar (League-style ghost + blink), buffs, carrying tag, objective.
 * Timer / Score / Rescued are handled by BaseScene.renderHUD() top-right.
 */
public class HUDRenderer {

    private static final float PAD          = 8f;
    private static final float BAR_W        = 160f;
    private static final float BAR_H        = 14f;
    private static final float LABEL_OFFSET = 4f;

    private static final float GHOST_DELAY    = 0.6f;
    private static final float GHOST_DRAIN    = 0.35f;
    private static final float BLINK_DURATION = 0.4f;
    private static final Color CARRYING_COLOR = new Color(0.4f, 1f, 0.4f, 1f);
    private static final Color BUFF_COLOR = new Color(0.9f, 0.8f, 0.3f, 1f);

    private float ghostHp;
    private float ghostDelay;
    private float blinkTimer;
    private float lastHp;

    private final ShapeRenderer shape;
    private final BitmapFont    font;
    private final GlyphLayout   layout;
    private final Color hpColor = new Color(Color.WHITE);

    private final float barX;

    public HUDRenderer(BitmapFont sharedFont) {
        this.font   = sharedFont;
        this.layout = new GlyphLayout();
        this.shape  = new ShapeRenderer();
        this.barX   = 10f;
    }

    public void init(float maxHp) {
        ghostHp    = maxHp;
        lastHp     = maxHp;
        ghostDelay = 0f;
        blinkTimer = 0f;
    }

    public void update(float delta, float currentHp) {
        if (currentHp < lastHp) {
            ghostDelay = GHOST_DELAY;
            blinkTimer = BLINK_DURATION;
        }
        lastHp = currentHp;

        if (ghostDelay > 0f) {
            ghostDelay -= delta;
        } else {
            if (ghostHp > currentHp) {
                ghostHp -= ghostHp * GHOST_DRAIN * delta;
                if (ghostHp < currentHp) ghostHp = currentHp;
            } else {
                ghostHp = currentHp;
            }
        }

        if (blinkTimer > 0f) blinkTimer -= delta;
    }

    /**
     * Draw bottom-left HUD: HP bar, HP numbers, buffs, carrying tag, objective.
     * Rescued / Timer / Score are drawn by BaseScene.renderHUD() separately.
     */
    public void render(SpriteBatch batch,
                       float currentHp, float maxHp,
                       String buffs, boolean carrying,
                       String objective) {

        int sw = Gdx.graphics.getWidth();
        int sh = Gdx.graphics.getHeight();
        float barY = sh - 50f;

        // ── Shapes ───────────────────────────────────────────────────────
        batch.end();
        enableBlend();
        shape.setProjectionMatrix(batch.getProjectionMatrix());
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Background
        shape.setColor(0.15f, 0.15f, 0.15f, 0.85f);
        shape.rect(barX, barY, BAR_W, BAR_H);

        // Ghost bar
        float ghostFrac = Math.min(ghostHp / maxHp, 1f);
        shape.setColor(0.65f, 0.05f, 0.05f, 1f);
        shape.rect(barX, barY, BAR_W * ghostFrac, BAR_H);

        // Current HP bar
        float hpFrac = Math.max(currentHp / maxHp, 0f);
        shape.setColor(getHpColor(hpFrac, blinkTimer > 0f));
        shape.rect(barX, barY, BAR_W * hpFrac, BAR_H);

        // Border
        shape.end();
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(1f, 1f, 1f, 0.6f);
        shape.rect(barX, barY, BAR_W, BAR_H);
        shape.end();

        disableBlend();
        batch.begin();

        // ── Text ─────────────────────────────────────────────────────────
        font.setColor(Color.WHITE);

        // HP numbers above bar
        float labelY = barY + BAR_H + LABEL_OFFSET + font.getCapHeight();
        font.draw(batch, (int) currentHp + " / " + (int) maxHp, barX, labelY);

        // Carrying tag right of HP numbers
        if (carrying) {
            font.setColor(CARRYING_COLOR);
            font.draw(batch, "  [Carrying NPC]", barX + 90f, labelY);
            font.setColor(Color.WHITE);
        }

        // Buffs below bar
        if (buffs != null && !buffs.isEmpty()) {
            font.setColor(BUFF_COLOR);
            font.draw(batch, buffs, barX, barY - 4f);
            font.setColor(Color.WHITE);
        }

        // Objective — top centre
        if (objective != null && !objective.isEmpty()) {
            layout.setText(font, objective);
            font.draw(batch, objective, (sw - layout.width) / 2f, sh - PAD - font.getCapHeight());
        }

        font.setColor(Color.WHITE);
    }

    private Color getHpColor(float frac, boolean blinking) {
        if (blinking) {
            hpColor.set(1f, 0.1f, 0.1f, 1f);
            return hpColor;
        }
        if (frac > 0.5f) {
            float t = (frac - 0.5f) * 2f;
            hpColor.set(1f - t, 1f, 0f, 1f);
            return hpColor;
        } else {
            float t = frac * 2f;
            hpColor.set(1f, t, 0f, 1f);
            return hpColor;
        }
    }

    private void enableBlend() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void disableBlend() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        shape.dispose();
    }
}

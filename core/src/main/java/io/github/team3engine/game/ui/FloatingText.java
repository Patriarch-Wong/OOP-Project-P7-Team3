package io.github.team3engine.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FloatingText {
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final Color color;
    private final float duration;

    private String text;
    private float timer;
    private final Vector2 offset;

    public FloatingText(float duration, Color color, float fontScale, Vector2 offset) {
        this.duration = duration;
        this.color = new Color(color);
        this.offset = new Vector2(offset);
        this.font = new BitmapFont();
        this.font.getData().setScale(fontScale);
        this.layout = new GlyphLayout();
        this.timer = 0f;
    }

    public void show(String text) {
        this.text = text;
        this.timer = duration;
    }

    public boolean isVisible() {
        return timer > 0f;
    }

    public void update(float dt) {
        if (timer > 0f) {
            timer = Math.max(0f, timer - dt);
        }
    }

    public void render(SpriteBatch batch, float anchorX, float anchorY) {
        if (!isVisible()) return;

        float alpha = timer / duration;
        font.setColor(color.r, color.g, color.b, alpha);
        layout.setText(font, text);
        float textX = anchorX + offset.x - layout.width / 2f;
        float textY = anchorY + offset.y;
        font.draw(batch, text, textX, textY);
    }

    public void dispose() {
        font.dispose();
    }
}

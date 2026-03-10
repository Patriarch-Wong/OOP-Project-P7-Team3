package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Pickup;
import io.github.team3engine.game.status.DamageReductionEffect;

/**
 * Pickup that grants 50% damage reduction for 10 seconds.
 * Rendered as a blue bobbing circle.
 */
public class MaskPickup extends CollidableEntity implements Pickup {
    private static final float SIZE = 22f;
    private static final float REDUCTION = 0.5f;
    private static final float DURATION = 10f;

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private float bobTimer = 0f;
    private final float baseY;

    public MaskPickup(String id, float x, float y) {
        super(id);
        this.baseY = y;
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.layout = new GlyphLayout();
        setPos(x, y);
        updateHitbox();
    }

    @Override
    public void onPickup(Entity collector) {
        if (collector instanceof Player) {
            Player player = (Player) collector;
            player.getStatusEffects().apply(new DamageReductionEffect(REDUCTION, DURATION, "Mask"));
        }
        destroy();
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x - SIZE, position.y - SIZE);
        hitbox.setSize(SIZE * 2, SIZE * 2);
    }

    @Override
    public void update(float dt) {
        bobTimer += dt;
        position.y = baseY + (float) Math.sin(bobTimer * 3f) * 4f;
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());

        // Outer glow ring
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.4f, 0.7f, 1f, 0.4f);
        shapeRenderer.circle(position.x, position.y, SIZE + 6f);
        // Inner circle
        shapeRenderer.setColor(0.2f, 0.58f, 0.86f, 1f);
        shapeRenderer.circle(position.x, position.y, SIZE);
        shapeRenderer.end();

        // Label
        batch.begin();
        layout.setText(font, "MASK");
        font.setColor(0.3f, 0.7f, 1f, 1f);
        font.draw(batch, "MASK", position.x - layout.width / 2f, position.y + SIZE + 18f);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        // Game logic handled by CollisionMediator
    }
}

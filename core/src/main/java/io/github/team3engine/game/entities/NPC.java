package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.game.status.SlowEffect;

/**
 * NPC that can be rescued. On contact with Player:
 * - NPC is "collected" (destroyed from world)
 * - Player gets a SlowEffect (carrying penalty)
 */
public class NPC extends CollidableEntity {
    private static final float WIDTH = 16f;
    private static final float HEIGHT = 28f;
    private static final float SLOW_FACTOR = 0.3f;

    private final ShapeRenderer shapeRenderer;
    private float bobTimer = 0f;
    private final String name;

    public NPC(String id, float x, float y, String name) {
        super(id);
        this.name = name;
        this.shapeRenderer = new ShapeRenderer();
        setPos(x, y);
        updateHitbox();
    }

    public String getName() { return name; }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x - WIDTH / 2f, position.y);
        hitbox.setSize(WIDTH, HEIGHT);
    }

    @Override
    public void update(float dt) {
        bobTimer += dt;
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Orange body
        shapeRenderer.setColor(0.95f, 0.61f, 0.07f, 1f);
        shapeRenderer.rect(position.x - WIDTH / 2f, position.y, WIDTH, HEIGHT);

        // Head
        float headRadius = WIDTH * 0.4f;
        shapeRenderer.circle(position.x, position.y + HEIGHT + headRadius * 0.3f, headRadius);

        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Player && !isDestroyed()) {
            Player player = (Player) other;
            if (!player.isCarryingNPC()) {
                player.pickUpNPC();
                player.getStatusEffects().apply(new SlowEffect(SLOW_FACTOR, Float.MAX_VALUE));
                destroy();
            }
        }
    }
}

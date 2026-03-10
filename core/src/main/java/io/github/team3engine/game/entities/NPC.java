package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;

/**
 * NPC that can be rescued. On contact with Player:
 * - NPC is "collected" (destroyed from world)
 * - Player gets a SlowEffect (carrying penalty)
 */
public class NPC extends CollidableEntity {
    private static final float WIDTH = 16f;
    private static final float HEIGHT = 28f;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private float bobTimer = 0f;
    private final String name;

    public NPC(String id, float x, float y, String name) {
        super(id);
        this.name = name;
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.layout = new GlyphLayout();
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

        // "HELP!" label bobbing above head
        batch.begin();
        float helpBob = (float) Math.sin(bobTimer * 4f) * 3f;
        String helpText = "HELP!";
        layout.setText(font, helpText);
        font.setColor(1f, 0.8f, 0f, 1f);
        font.draw(batch, helpText, position.x - layout.width / 2f,
                position.y + HEIGHT + headRadius * 2f + 14f + helpBob);
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

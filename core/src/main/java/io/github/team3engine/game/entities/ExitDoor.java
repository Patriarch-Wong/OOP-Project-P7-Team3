package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.io.IOManager;

/**
 * Exit door. On collision with Player:
 * - If carrying NPC: rescues NPC, removes slow, broadcasts NPC_RESCUED
 * - If all NPCs rescued (rescuedCount >= requiredRescues): broadcasts PLAYER_WIN
 */
public class ExitDoor extends CollidableEntity {
    private final float width;
    private final float height;
    private final int requiredRescues;
    private final IOManager io;
    private final ShapeRenderer shapeRenderer;
    private float glowTimer = 0f;

    public ExitDoor(String id, float x, float y, float width, float height,
                    int requiredRescues, IOManager io) {
        super(id);
        this.width = width;
        this.height = height;
        this.requiredRescues = requiredRescues;
        this.io = io;
        this.shapeRenderer = new ShapeRenderer();
        setPos(x, y);
        updateHitbox();
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(width, height);
    }

    @Override
    public void update(float dt) {
        glowTimer += dt;
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float pulse = 0.6f + 0.2f * (float) Math.sin(glowTimer * 3f);
        shapeRenderer.setColor(0.18f, pulse, 0.44f, 1f);
        shapeRenderer.rect(position.x, position.y, width, height);

        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.18f, 0.8f, 0.44f, 1f);
        shapeRenderer.rect(position.x, position.y, width, height);
        shapeRenderer.end();

        batch.begin();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public void onCollision(Collidable other) {
        // Game logic handled by CollisionMediator
    }

    public int getRequiredRescues() { return requiredRescues; }
}

package io.github.team3engine.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.interfaces.Collidable;
import io.github.team3engine.io.IOManager;

/**
 * A winning objective entity - triggers victory sound and event on contact.
 */
public class WinBox extends CollidableEntity {
    private final float size;
    private final ShapeRenderer shapeRenderer;
    private Color color;
    
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private boolean hasWon = false; // Flag to prevent sound spamming

    public WinBox(String id, float size, IOManager ioManager, AudioManager audioManager) {
        super(id);
        this.size = size;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
        this.shapeRenderer = new ShapeRenderer();
        this.color = new Color(0.2f, 0.8f, 0.2f, 1f);
        
        setPos(550, 60); // Positioned slightly above ground
        updateHitbox();
    }

    @Override
    protected void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
        hitbox.setSize(size, size);
    }

    @Override
    public void update(float dt) {
        updateHitbox();
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(position.x, position.y, size, size);
        shapeRenderer.end();
        batch.begin();
    }

    @Override
    public void onCollision(Collidable other) {
        if (!(other instanceof Circle)) return;

        Circle player = (Circle) other;

        // --- 1. THE SOUND & EVENT (Only once) ---
        if (!hasWon) {
            if (audioManager != null) {
                audioManager.play("victory.mp3");
            }
            if (ioManager != null) {
                ioManager.broadcast("PLAYER_WIN");
            }
            hasWon = true; 
            System.out.println("Victory triggered!");
        }

        // --- 2. THE PHYSICS (Every frame) ---
        // This part prevents the ball from bypassing/phasing through the box
        float aLeft = hitbox.x;
        float aRight = hitbox.x + hitbox.width;
        float aBottom = hitbox.y;
        float aTop = hitbox.y + hitbox.height;

        float bLeft = player.getHitbox().x;
        float bRight = player.getHitbox().x + player.getHitbox().width;
        float bBottom = player.getHitbox().y;
        float bTop = player.getHitbox().y + player.getHitbox().height;

        float overlapX = Math.min(aRight, bRight) - Math.max(aLeft, bLeft);
        float overlapY = Math.min(aTop, bTop) - Math.max(aBottom, bBottom);

        if (overlapX > 0 && overlapY > 0) {
            if (overlapX < overlapY) {
                float dx = (player.getPos().x < (hitbox.x + hitbox.width/2)) ? -overlapX : overlapX;
                player.setPos(player.getPos().x + dx, player.getPos().y);
                player.setVelocity(0f, player.getVelocity().y);
            } else {
                float dy = (player.getPos().y < (hitbox.y + hitbox.height/2)) ? -overlapY : overlapY;
                player.setPos(player.getPos().x, player.getPos().y + dy);
                player.setVelocity(player.getVelocity().x, 0f);
            }
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
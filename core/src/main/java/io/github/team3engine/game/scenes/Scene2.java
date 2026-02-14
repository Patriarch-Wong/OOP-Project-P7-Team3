package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.*;
import io.github.team3engine.game.entities.*;
import io.github.team3engine.game.inputs.PlayerInput;

/**
 * Inverse of Scene1: mirrored layout. Winning here returns to Scene1.
 */
public class Scene2 extends BaseScene {
    private static final float MAX_DELTA = 0.07f;

    private Texture image;
    private Texture platformTex;
    private Circle player;
    private MovementInput movementInput;

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final MovementManager movementManager;

    private PlayerInput playerInput;

    public Scene2(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager, IOManager ioManager, AudioManager audioManager,
                  EntityManager entityManager, CollisionManager collisionManager, MovementManager movementManager) {
        super(batch, sharedFont);
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.movementManager = movementManager;
    }

    @Override
    protected InputProcessor getInputProcessorForScene() {
        return ioManager;
    }

    @Override
    protected void onShow() {
        playerInput = new PlayerInput();
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);

        float gw = Gdx.graphics.getWidth();
        float gh = Gdx.graphics.getHeight();

        Bucket bucket = new Bucket("bucket", gw / 2f, 20f);
        entityManager.addEntity(bucket);

        player = new Circle("player_circle", gw / 2f, gh / 2f, 30f, playerInput, ioManager);
        entityManager.addEntity(player);
        
        // Reset movement state when scene starts
        player.getMovementState().reset();
        
        // Create movement input tied to this player's movement state
        movementInput = new MovementInput(player.getMovementState(), ioManager, playerInput);

        float bulletX = gw * 0.5f;
        float bulletY = gh * 0.75f;
        Bullet singleBullet = new Bullet("bullet_single", bulletX, bulletY, null, audioManager);
        singleBullet.setVelocity(0f, 0f);
        entityManager.addEntity(singleBullet);

        float scaleX = gw / 19f;
        float scaleY = gh / 12f;
        platformTex = new Texture(Gdx.files.internal("platform.png"));

        Platform p1 = new Platform("platform_1", 1f * scaleX, 1f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p1);
        Platform p2 = new Platform("platform_2", 6f * scaleX, 5f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p2);
        Platform p3h = new Platform("platform_3_h", 11f * scaleX, 9f * scaleY, 8f * scaleX, 1f * scaleY, platformTex);
        Platform p3v = new Platform("platform_3_v", 11f * scaleX, 8f * scaleY, 1f * scaleX, 1f * scaleY, platformTex);
        entityManager.addEntity(p3h);
        entityManager.addEntity(p3v);

        WinBox winBox = new WinBox("win_box", 50f);
        entityManager.addEntity(winBox);

        collisionManager.register(player);
        collisionManager.register(bucket);
        collisionManager.register(singleBullet);
        collisionManager.register(p1);
        collisionManager.register(p2);
        collisionManager.register(p3h);
        collisionManager.register(p3v);
        collisionManager.register(winBox);

        image = new Texture("libgdx.png");
    }

    @Override
    protected void onHide() {
        entityManager.disposeAll();
        collisionManager.clear();
        if (playerInput != null) {
            ioManager.removeInputListener(playerInput);
            playerInput = null;
        }
        movementInput = null;
        player = null;
        if (image != null) {
            image.dispose();
            image = null;
        }
        if (platformTex != null) {
            platformTex.dispose();
            platformTex = null;
        }
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, MAX_DELTA);
        update(delta);
        
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        if (image != null) {
            batch.draw(image, 140, 210);
        }

        entityManager.renderAll(batch);
        batch.end();
        drawStageAndUI(delta);

        // Apply movement using the player's movement state
        movementManager.applyMovement(player, player.getMovementState(), movementInput, delta);
        // syncs player to prevent falling through other entity like platform
        player.update(0f);

        checkFallCondition();
        checkGroundDetection();
    }

    @Override
    public void update(float delta) {
        entityManager.updateAll(delta);
        playerInput.update(delta);
        movementInput.update();
        collisionManager.update(delta);
    }

    // local methods
    private void checkFallCondition() {
        Array<Collidable[]> collisionPairs = collisionManager.resolveCollisions();
        // If player is jumping, not moving horizontally, and collides with a platform, make them fall
        for (Collidable[] pair : collisionPairs) {
            if (pair == null || pair.length < 2)
                continue;

            Collidable a = pair[0], b = pair[1];

            if (a != player && b != player)
                continue;
            Collidable other = (a == player) ? b : a;
            if (!(other instanceof Platform))
                continue;
            if (movementManager.isMovingUpward(player.getMovementState()) && 
                player.getY() + player.getRadius() <= other.getHitbox().y + 2f) {
                movementManager.cancelUpwardVelocity(player.getMovementState());
                break;
            }
        }
    }

    private void checkGroundDetection() {
        // Ground detection after resolve: only set grounded when circle has actually
        // landed (bottom at or just below platform top), not when still above
        boolean isOnFloor = player.getY() <= player.getRadius() + 1f;
        boolean isOnPlatform = false;
        float circleBottom = player.getY() - player.getRadius();
        float sinkTolerance = 5f;  // Increased tolerance to handle landing better

        for (Entity e : entityManager.getAll()) {
            if (e instanceof Platform) {
                Platform platform = (Platform) e;
                float platformTop = platform.getY() + platform.getHeight();
                float platformLeft = platform.getX();
                float platformRight = platform.getX() + platform.getWidth();
                float circleCenterX = player.getX();
                boolean landed = circleBottom <= platformTop + sinkTolerance
                        && circleBottom >= platformTop - sinkTolerance;
                boolean overPlatform = circleCenterX >= platformLeft - player.getRadius()
                        && circleCenterX <= platformRight + player.getRadius();
                if (landed && overPlatform) {
                    isOnPlatform = true;
                    break;
                }
            }
        }

        if (isOnFloor || isOnPlatform) {
            movementManager.setGrounded(player.getMovementState(), true);
        } else {
            movementManager.setGrounded(player.getMovementState(), false);
        }

        if (player.touchesCeiling(entityManager)) {
            movementManager.hitCeiling(player.getMovementState());
        }
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "SCENE 1 - Reach the green box!", 100, 400);
        font.draw(batch, "Win to go to Scene 2.", 100, 350);
    }
}
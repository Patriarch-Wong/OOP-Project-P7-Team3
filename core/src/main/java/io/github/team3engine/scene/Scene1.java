package io.github.team3engine.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.collision.CollisionManager;
import io.github.team3engine.entity.*;
import io.github.team3engine.interfaces.Collidable;
import io.github.team3engine.interfaces.Renderable;
import io.github.team3engine.interfaces.Updatable;
import io.github.team3engine.io.IOManager;
import io.github.team3engine.io.PlayerInput;
import io.github.team3engine.GameEngine;

public class Scene1 extends BaseScene {
    private static final float MAX_DELTA = 0.1f;

    private Texture image;
    private Texture platformTex;
    private Circle player;
    private MovementInput movementInput;
    private Array<Updatable> gameUpdatables;
    private Array<Renderable> gameRenderables;

    public Scene1(GameEngine engine, SpriteBatch batch) {
        super(engine, batch);
    }

    @Override
    protected InputProcessor getInputProcessorForScene() {
        return engine.getIOManager();
    }

    @Override
    protected void onShow() {
        IOManager ioManager = engine.getIOManager();
        AudioManager audioManager = engine.getAudioManager();
        EntityManager entityManager = engine.getEntityManager();
        CollisionManager collisionManager = engine.getCollisionManager();

        PlayerInput playerInput = new PlayerInput();
        ioManager.addInputListener(playerInput);

        movementInput = new MovementInput();

        player = new Circle("player_circle", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 30f,
                playerInput, ioManager);
        entityManager.addEntity(player);

        Bucket bucket = new Bucket("bucket", Gdx.graphics.getWidth() / 2f, 20f);
        entityManager.addEntity(bucket);

        float bulletX = Gdx.graphics.getWidth() * 0.5f;
        float bulletY = Gdx.graphics.getHeight() * 0.75f;
        Bullet singleBullet = new Bullet("bullet_single", bulletX, bulletY, null, audioManager);
        singleBullet.setVelocity(0f, 0f);
        entityManager.addEntity(singleBullet);

        float gw = Gdx.graphics.getWidth();
        float gh = Gdx.graphics.getHeight();
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

        collisionManager.register(player);
        collisionManager.register(bucket);
        collisionManager.register(singleBullet);
        collisionManager.register(p1);
        collisionManager.register(p2);
        collisionManager.register(p3h);
        collisionManager.register(p3v);

        WinBox winBox = new WinBox("win_box", 50f, ioManager, audioManager);
        entityManager.addEntity(winBox);
        collisionManager.register(winBox);

        gameUpdatables = new Array<>();
        gameUpdatables.add(entityManager);
        gameUpdatables.add(collisionManager);

        gameRenderables = new Array<>();
        gameRenderables.add(entityManager);

        image = new Texture("libgdx.png");
    }

    @Override
    protected void onHide() {
        engine.getEntityManager().disposeAll();
        engine.getCollisionManager().clear();
        if (image != null) {
            image.dispose();
            image = null;
        }
        if (platformTex != null) {
            platformTex.dispose();
            platformTex = null;
        }
        player = null;
        movementInput = null;
        gameUpdatables = null;
        gameRenderables = null;
    }

    @Override
    public void render(float delta) {
        float deltaTime = Math.min(delta, MAX_DELTA);
        EntityManager entityManager = engine.getEntityManager();
        MovementManager movementManager = engine.getMovementManager();
        CollisionManager collisionManager = engine.getCollisionManager();

        if (!SceneManager.getInstance().isPaused() && player != null) {
            movementInput.update();
            movementManager.applyMovement(player, movementInput, deltaTime);
            for (int i = 0; i < gameUpdatables.size; i++) {
                gameUpdatables.get(i).update(deltaTime);
            }
            Array<Collidable[]> collisionPairs = collisionManager.resolveCollisions();

            for (Collidable[] pair : collisionPairs) {
                if (pair == null || pair.length < 2) continue;
                Collidable a = pair[0], b = pair[1];
                if (a != player && b != player) continue;
                Collidable other = (a == player) ? b : a;
                if (!(other instanceof Platform)) continue;
                if (movementManager.isMovingUpward() && !movementManager.hasHorizontalMotion()) {
                    movementManager.cancelUpwardVelocity();
                    break;
                }
            }

            boolean isOnFloor = player.getPos().y <= player.getRadius() + 1f;
            boolean isOnPlatform = false;
            float circleBottom = player.getPos().y - player.getRadius();
            float sinkTolerance = 3f;

            for (Entity e : entityManager.getAll()) {
                if (e instanceof Platform) {
                    Platform platform = (Platform) e;
                    float platformTop = platform.getPos().y + platform.getHeight();
                    float platformLeft = platform.getPos().x;
                    float platformRight = platform.getPos().x + platform.getWidth();
                    float circleCenterX = player.getPos().x;
                    boolean landed = circleBottom <= platformTop + sinkTolerance && circleBottom >= platformTop - sinkTolerance;
                    boolean overPlatform = circleCenterX >= platformLeft - player.getRadius() && circleCenterX <= platformRight + player.getRadius();
                    if (landed && overPlatform) {
                        isOnPlatform = true;
                        break;
                    }
                }
            }

            if (isOnFloor || isOnPlatform) {
                movementManager.setGrounded(true);
            } else {
                movementManager.setGrounded(false);
            }

            if (touchesCeiling(player, entityManager)) {
                movementManager.hitCeiling();
            }
        }

        clearScreen(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        if (image != null) {
            batch.draw(image, 140, 210);
        }
        for (int i = 0; i < gameRenderables.size; i++) {
            gameRenderables.get(i).render(batch);
        }
        batch.end();
        drawStageAndUI(delta);
    }

    private boolean touchesCeiling(Circle player, EntityManager entityManager) {
        float circleTop = player.getPos().y + player.getRadius();
        float tolerance = 6f;
        for (Entity e : entityManager.getAll()) {
            if (e instanceof Platform) {
                Platform platform = (Platform) e;
                float platformBottom = platform.getPos().y;
                float platformLeft = platform.getPos().x;
                float platformRight = platform.getPos().x + platform.getWidth();
                float circleX = player.getPos().x;
                boolean underPlatform = circleTop >= platformBottom - tolerance && circleTop <= platformBottom + tolerance;
                boolean horizontallyAligned = circleX >= platformLeft - player.getRadius() && circleX <= platformRight + player.getRadius();
                if (underPlatform && horizontallyAligned) return true;
            }
        }
        return false;
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "SCENE 1 - Reach the green box!", 100, 400);
        font.draw(batch, "Win to go to Scene 2.", 100, 350);
    }
}

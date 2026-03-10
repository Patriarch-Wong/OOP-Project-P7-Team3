package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.*;
import io.github.team3engine.game.entities.*;
import io.github.team3engine.game.inputs.PlayerInput;
import io.github.team3engine.game.physics.GroundDetector;

//Inverse layout of scene1
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
    private GroundDetector groundDetector;
    private final BitmapFont font;

    private final int screenWidth;
    private final int screenHeight;

    public Scene2(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager, IOManager ioManager, AudioManager audioManager,
                  EntityManager entityManager, CollisionManager collisionManager, MovementManager movementManager,
                  int screenWidth, int screenHeight) {
        super(batch);
        this.font = sharedFont;
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.movementManager = movementManager;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
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

        float gw = screenWidth;
        float gh = screenHeight;

        Bucket bucket = new Bucket("bucket", gw / 2f, 20f, gw, gh);
        entityManager.addEntity(bucket);

        player = new Circle("player_circle", gw / 2f, gh / 2f, 30f, gw, gh);
        entityManager.addEntity(player);
        
        // Reset movement state when scene starts
        player.getMovementState().reset();
        
        // Create movement input tied to this player's movement state
        movementInput = new MovementInput(player.getMovementState(), ioManager, playerInput);
        groundDetector = new GroundDetector(movementManager, collisionManager, entityManager);

        float scaleX = gw / 19f;
        float scaleY = gh / 12f;
        platformTex = new Texture(Gdx.files.internal("platform.png"));

        // Mirror platform positions: x -> gw - x - width
        float p1w = 8f * scaleX, p1h = 1f * scaleY;
        Platform p1 = new Platform("platform_1", gw - (1f * scaleX) - p1w, 1f * scaleY, p1w, p1h, platformTex);
        entityManager.addEntity(p1);
        float p2w = 8f * scaleX, p2h = 1f * scaleY;
        Platform p2 = new Platform("platform_2", gw - (6f * scaleX) - p2w, 5f * scaleY, p2w, p2h, platformTex);
        entityManager.addEntity(p2);
        float p3hw = 8f * scaleX, p3hh = 1f * scaleY;
        Platform p3h = new Platform("platform_3_h", gw - (11f * scaleX) - p3hw, 9f * scaleY, p3hw, p3hh, platformTex);
        float p3vw = 1f * scaleX, p3vh = 1f * scaleY;
        Platform p3v = new Platform("platform_3_v", gw - (11f * scaleX) - p3vw, 8f * scaleY, p3vw, p3vh, platformTex);
        entityManager.addEntity(p3h);
        entityManager.addEntity(p3v);

        WinBox winBox = new WinBox("win_box", gw - 550 - 50, 60, 50f, ioManager);
        entityManager.addEntity(winBox);

        collisionManager.register(player);
        collisionManager.register(bucket);
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

    private void checkFallCondition() {
        groundDetector.checkFallCondition(player);
    }

    private void checkGroundDetection() {
        groundDetector.checkGroundDetection(player);
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "SCENE 1 - Reach the green box!", 100, 400);
        font.draw(batch, "Win to go to Scene 2.", 100, 350);
    }
}
package io.github.team3engine.game.scenes.demo;

import io.github.team3engine.game.scenes.GameOverScene;
import io.github.team3engine.game.scenes.GameplayScene;
import io.github.team3engine.game.scenes.SceneType;
import io.github.team3engine.game.scenes.ScoreBoardScene;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.collision.CollisionMediator;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.game.entities.*;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.score.ScoreContext;
import io.github.team3engine.game.score.ScoreManager;
import io.github.team3engine.game.scene.Timer;
import io.github.team3engine.game.inputs.PlayerInput;
import io.github.team3engine.game.physics.GroundDetector;
import io.github.team3engine.game.factories.*;

public class Scene1 extends BaseScene implements GameplayScene {
    private static final float MAX_DELTA = 0.07f;

    private Texture image;
    private Texture platformTex;
    private Circle player;
    private MovementInput movementInput;

    // Bucket with AI movement
    private Bucket bucket;
    private AIMovement bucketAI;

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final MovementManager movementManager;
    private final ScoreManager scoreManager;

    private PlayerInput playerInput;
    private GroundDetector groundDetector;
    private final BitmapFont font;

    private final int screenWidth;
    private final int screenHeight;

    private CollisionMediator mediator;
    private Map<String, Float> hazardCooldowns = new HashMap<>();
    private com.badlogic.gdx.utils.Timer.Task fireResetTask;

    // Timer managed locally
    private Timer timer;
    private BitmapFont timerFont;
    private GlyphLayout timerLayout;

    public Scene1(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager, IOManager ioManager,
            AudioManager audioManager,
            EntityManager entityManager, CollisionManager collisionManager, MovementManager movementManager,
            int screenWidth, int screenHeight, ScoreManager scoreManager) {
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
        this.scoreManager = scoreManager;
    }

    @Override
    protected InputProcessor getInputProcessorForScene() {
        return ioManager;
    }

    @Override
    protected void onShow() {
        super.onShow();
        // Set up timer
        timer = new Timer(60f);
        if (timerFont == null) {
            timerFont = new BitmapFont();
            timerLayout = new GlyphLayout();
        }
        timer.start();

        playerInput = new PlayerInput();
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);

        float gw = screenWidth;
        float gh = screenHeight;

        // Create bucket with AI movement
        bucket = new Bucket("bucket", gw / 2f, 0f, gw, gh);
        entityManager.addEntity(bucket);

        // Create AI input for the bucket
        // Boundaries: 0 to screen width, accounting for bucket width
        bucketAI = new AIMovement(380, 450, bucket.getWidth());

        // Optional: Enable random direction changes every 2 seconds
        // bucketAI.enableTimedDirectionChanges(2f);

        player = new Circle("player_circle", gw / 2f, gh / 2f, 30f, gw, gh);
        entityManager.addEntity(player);

        // Reset movement state when scene starts
        player.getMovementState().reset();
        bucket.getMovementState().reset();

        // Create movement input tied to this player's movement state
        movementInput = new MovementInput(player.getMovementState(), ioManager, playerInput);
        groundDetector = new GroundDetector(movementManager, collisionManager, entityManager);

        float scaleX = gw / 19f;
        float scaleY = gh / 12f;
        String platformTexturePath = "platform.png";

        Platform p1 = StaticEntityFactory.createEntity(Platform.class, "platform_1", 1f * scaleX, 1f * scaleY,
                8f * scaleX, 1f * scaleY, platformTexturePath);
        Platform p2 = StaticEntityFactory.createEntity(Platform.class, "platform_2", 6f * scaleX, 5f * scaleY,
                8f * scaleX, 1f * scaleY, platformTexturePath);
        Platform p3h = StaticEntityFactory.createEntity(Platform.class, "platform_3_h", 11f * scaleX, 9f * scaleY,
                8f * scaleX, 1f * scaleY, platformTexturePath);
        Platform p3v = StaticEntityFactory.createEntity(Platform.class, "platform_3_v", 11f * scaleX, 8f * scaleY,
                1f * scaleX, 1f * scaleY, platformTexturePath);

        entityManager.addEntity(p1);
        entityManager.addEntity(p2);
        entityManager.addEntity(p3h);
        entityManager.addEntity(p3v);

        WinBox winBox = StaticEntityFactory.createEntity(WinBox.class, "win_box", 500, 60, 50f, 50f, null);
        entityManager.addEntity(winBox);

        SpriteEntity fire1 = SpriteEntityFactory.animated(
                "hazard_fire_1",
                3f * scaleX, 2f * scaleY,
                "ui/sprites/fire_spritesheet.png",
                8, 1, 0.2f,
                2.5f, 2.5f,
                true);

        entityManager.addEntity(fire1);

        collisionManager.register(player);
        collisionManager.register(bucket);
        collisionManager.register(p1);
        collisionManager.register(p2);
        collisionManager.register(p3h);
        collisionManager.register(p3v);
        collisionManager.register(winBox);
        collisionManager.register(fire1);

        image = new Texture("libgdx.png");

        ioManager.registerEvent(GameEvents.PLAYER_HIT_FIRE, () -> {
            Gdx.app.log("Game", "Player hit fire!");
            Color currentPlayerColor = player.getColor().cpy();
            player.setColor(Color.RED);
            audioManager.play("oof.mp3");

            // Reset color after 0.4 seconds
            com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                @Override
                public void run() {
                    player.setColor(currentPlayerColor);
                }
            }, 0.4f);
            // add visual feedback, reduce health, etc.
        });
        // register scene specific events in scene
        ioManager.registerEvent(GameEvents.PLAYER_WIN, () -> {
            Gdx.app.log("Game", "Player won!");
            audioManager.play("victory.mp3");
            onPlayerEscaped();
            Gdx.app.postRunnable(sceneManager::reloadCurrentScene);
        });

        // --- Collision rules (Mediator pattern) ---
        mediator = new CollisionMediator();

        // Fire and player collision rle
        mediator.addIdRule("hazard_fire_1", "player_circle", (a, playerId) -> {
            float cooldown = hazardCooldowns.getOrDefault("fire", 0f);
            if (cooldown <= 0f) {
                ioManager.broadcast(GameEvents.PLAYER_HIT_FIRE);
                hazardCooldowns.put("fire", 2.0f);
            }
        });

        // winbox and player collision rule
        mediator.addIdRule("win_box", "player_circle", (a, playerId) -> {
            ioManager.broadcast(GameEvents.PLAYER_WIN);
        });
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
        bucketAI = null;
        player = null;
        bucket = null;
        if (image != null) {
            image.dispose();
            image = null;
        }
        if (platformTex != null) {
            platformTex.dispose();
            platformTex = null;
        }

        ioManager.clearEvent(GameEvents.PLAYER_HIT_FIRE);
        ioManager.clearEvent(GameEvents.PLAYER_WIN);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, MAX_DELTA);
        update(delta);

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(Color.WHITE);
        if (image != null) {
            batch.draw(image, 140, 210);
        }

        entityManager.renderAll(batch);
        batch.end();
        drawStageAndUI(delta);

        // Apply movement to player using the player's movement state
        movementManager.applyMovement(player, player.getMovementState(), movementInput, delta);
        player.update(0f);

        // Apply AI movement to bucket
        bucketAI.update(bucket.getX(), delta);
        movementManager.applyMovement(bucket, bucket.getMovementState(), bucketAI, delta);
        bucket.update(0f);

        checkFallCondition();
        checkGroundDetection();
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        // Tick the timer
        if (timer != null) {
            timer.update(dt);
            if (timer.isFinished()) {
                onTimerFinished();
            }
        }

        entityManager.updateAll(dt);
        playerInput.update(dt);
        movementInput.update();
        collisionManager.update(dt);

        // Platform.onCollision() still runs for physics pushout
        Array<Collidable[]> pairs = collisionManager.resolveCollisions();
        // Game rules dispatched through mediator (no instanceof in entities)
        mediator.resolve(pairs);

        if (!hazardCooldowns.isEmpty()) {
            java.util.List<String> keys = new java.util.ArrayList<>(hazardCooldowns.keySet());
            for (String key : keys) {
                float time = hazardCooldowns.get(key);
                if (time > 0) {
                    time -= dt;
                    if (time < 0)
                        time = 0;
                    hazardCooldowns.put(key, time);
                }
            }
        }
    }

    private void checkFallCondition() {
        groundDetector.checkFallCondition(player);
    }

    private void checkGroundDetection() {
        groundDetector.checkGroundDetection(player);
    }

    private void onPlayerEscaped() {
        timer.stop();
        ScoreContext context = new ScoreContext("PLAYER_ESCAPED");
        context.put("timeRemaining", timer.getTimeRemaining());
        context.put("objectiveComplete", true);
        scoreManager.applyRules(context);
        Gdx.app.log("Score", "Final Score: " + scoreManager.getFinalScore());
    }

    private void onTimerFinished() {
        Gdx.app.log("Game", "Time's up!");
        // Demo scene behavior: no Game Over routing here.
    }

    @Override
    protected void renderUI() {
        font.draw(batch, "SCENE 1 - Reach the green box!", 100, 400);
        font.draw(batch, "Win to go to Scene 2.", 100, 350);
        font.draw(batch, "Score: " + scoreManager.getScore(), 100, 300);

        // Render timer top-right
        if (timer != null && timerFont != null) {
            int seconds = (int) Math.ceil(timer.getTimeRemaining());
            int minutes = seconds / 60;
            int secs = seconds % 60;
            String timeText = String.format("Time: %d:%02d", minutes, secs);
            float x = Gdx.graphics.getWidth() - 10f;
            float y = Gdx.graphics.getHeight() - 10f;

            timerLayout.setText(timerFont, timeText);
            timerFont.setColor(seconds <= 10 ? Color.RED : Color.WHITE);
            timerFont.draw(batch, timeText, x - timerLayout.width, y);
            timerFont.setColor(Color.WHITE);
        }
    }

    @Override
    public void dispose() {
        if (timerFont != null) { timerFont.dispose(); timerFont = null; }
        super.dispose();
    }
}

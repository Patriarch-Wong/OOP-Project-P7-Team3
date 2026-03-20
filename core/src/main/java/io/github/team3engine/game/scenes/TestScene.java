package io.github.team3engine.game.scenes;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.collision.CollisionManager;
import io.github.team3engine.engine.collision.CollisionMediator;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.game.entities.ExitDoor;
import io.github.team3engine.game.entities.Fire;
import io.github.team3engine.game.entities.MaskPickup;
import io.github.team3engine.game.entities.MovementInput;
import io.github.team3engine.game.entities.NPC;
import io.github.team3engine.game.entities.Platform;
import io.github.team3engine.game.entities.Player;
import io.github.team3engine.game.entities.WetTowelPickup;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.inputs.PlayerInput;
import io.github.team3engine.game.interfaces.Damageable;
import io.github.team3engine.game.interfaces.ScoreRule;
import io.github.team3engine.game.physics.GroundDetector;
import io.github.team3engine.game.score.ScoreContext;
import io.github.team3engine.game.score.ScoreManager;
import io.github.team3engine.game.status.SlowEffect;
import io.github.team3engine.game.status.StatusEffect;
import io.github.team3engine.game.status.StatusEffectMath;
import io.github.team3engine.game.ui.HUDRenderer;
import io.github.team3engine.game.util.Timer;

public class TestScene extends BaseScene implements GameplayScene {
    private static final String CARRY_SLOW_KEY = "slow:carry_npc";
    private static final float MAX_DELTA = 0.07f;
    private static final float FIRE_GROW_X_PER_SEC = 0.18f;
    private static final float FIRE_GROW_Y_PER_SEC = 0.06f;
    private static final float FIRE_MAX_SCALE_X = 2f;
    private static final float FIRE_MAX_SCALE_Y = 2f;
    private static final float FALL_DEATH_BUFFER = 0f;
    
    private static final float CAMERA_LERP = 5f;
    
    private OrthographicCamera camera;
    private Player player;
    private MovementInput movementInput;
    private PlayerInput playerInput;
    private GroundDetector groundDetector;
    private CollisionMediator mediator;
    private Texture platformTex;
    private Texture backgroundTex;
    private final Array<Fire> fires = new Array<>();
    private HUDRenderer hud;
    private LevelConfig levelConfig;
    private NPC npc;

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final MovementManager movementManager;
    private final BitmapFont font;
    private final ScoreManager scoreManager;

    private final int screenWidth;
    private final int screenHeight;
    private boolean deathHandled;
    private boolean rulesRegistered = false;
    private final List<ScoreRule> scoreRules;

    // Timer managed locally
    private Timer timer;
    private BitmapFont timerFont;
    private GlyphLayout timerLayout;

    public TestScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager,
                     IOManager ioManager, AudioManager audioManager, EntityManager entityManager,
                     CollisionManager collisionManager, MovementManager movementManager,
                     int screenWidth, int screenHeight,
                     List<ScoreRule> scoreRules, ScoreManager scoreManager) {
        this(batch, sharedFont, sceneManager, ioManager, audioManager, entityManager,
             collisionManager, movementManager, screenWidth, screenHeight, scoreRules, scoreManager, 1);
    }

    public TestScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager,
                     IOManager ioManager, AudioManager audioManager, EntityManager entityManager,
                     CollisionManager collisionManager, MovementManager movementManager,
                     int screenWidth, int screenHeight,
                     List<ScoreRule> scoreRules, ScoreManager scoreManager, int levelNumber) {
        super(batch);
        this.font = sharedFont;
        this.scoreRules = scoreRules;
        this.scoreManager = scoreManager;
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.movementManager = movementManager;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.levelConfig = LevelConfig.getAllLevels()[levelNumber - 1];
    }

    public void setLevel(int levelNumber) {
        Gdx.app.log("TestScene", "setLevel called with: " + levelNumber);
        LevelConfig[] allLevels = LevelConfig.getAllLevels();
        if (levelNumber < 1 || levelNumber > allLevels.length) {
            Gdx.app.error("TestScene", "Invalid level number: " + levelNumber + ", max is " + allLevels.length);
            return;
        }
        this.levelConfig = allLevels[levelNumber - 1];
        Gdx.app.log("TestScene", "Level set to: " + levelConfig.levelNumber + " - " + levelConfig.displayName);
    }

    public void resetForNewGame() {
        rulesRegistered = false;
        setLevel(1);
    }

    @Override
    protected InputProcessor getInputProcessorForScene() {
        return ioManager;
    }

    @Override
    protected void onShow() {
        super.onShow();
        try {
            Gdx.app.log("TestScene", "onShow started, level=" + levelConfig.levelNumber);
            timer = new Timer(levelConfig.timerDuration);
            if (timerFont == null) {
                timerFont = new BitmapFont();
                timerFont.getData().setScale(1.5f);
                timerLayout = new GlyphLayout();
            }
            timer.start();

            if (hud == null) hud = new HUDRenderer(font);
            hud.init(levelConfig.playerMaxHp);
            fires.clear();
            deathHandled = false;
            Gdx.app.log("TestScene", "onShow completed, level=" + levelConfig.levelNumber);
        } catch (Exception e) {
            Gdx.app.error("TestScene", "Error in onShow", e);
            e.printStackTrace();
        }

        // --- Register score rules (only once) ---
        // Score accumulates across levels and retries
        if (!rulesRegistered && scoreRules != null) {
            for (ScoreRule rule : scoreRules) {
                scoreManager.addRule(rule);
            }
            rulesRegistered = true;
        }

        playerInput = new PlayerInput();
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);

        // --- Player ---
        player = new Player("player", levelConfig.playerStartX, levelConfig.playerStartY, 20f, 36f, levelConfig.playerMaxHp, levelConfig.worldWidth, levelConfig.worldHeight);
        entityManager.addEntity(player);
        player.getMovementState().reset();
        player.resetMovementRules();
        movementInput = new MovementInput(player, ioManager, playerInput);
        groundDetector = new GroundDetector(movementManager, collisionManager, entityManager);

        // --- Camera ---
        camera = new OrthographicCamera(screenWidth, screenHeight);
        camera.position.set(player.getX() + player.getWidth()/2f, player.getY() + player.getHeight()/2f, 0);
        camera.update();

        // --- Platforms ---
        platformTex = new Texture(Gdx.files.internal("platform.png"));

        // --- Background ---
        backgroundTex = new Texture(Gdx.files.internal("burning_bg.png"));

        // Ground segments
        for (int i = 0; i < levelConfig.groundSegmentsX.length; i++) {
            Platform ground = new Platform("ground" + i, levelConfig.groundSegmentsX[i], 0, levelConfig.groundSegmentsWidth[i], 40f, platformTex);
            entityManager.addEntity(ground);
            collisionManager.register(ground);
        }

        // Elevated platforms
        for (int i = 0; i < levelConfig.platformX.length; i++) {
            Platform p = new Platform("plat_" + i, levelConfig.platformX[i], levelConfig.platformY[i], levelConfig.platformWidth[i], 16f, platformTex);
            entityManager.addEntity(p);
            collisionManager.register(p);
        }

        // --- Fire hazards ---
        Texture fire_texture = new Texture(Gdx.files.internal("ui/sprites/fire_spritesheet.png"));

        // Ground fires - placed on top of ground (y = groundHeight)
        for (int i = 0; i < levelConfig.groundFireX.length; i++) {
            Fire fire = new Fire("ground_fire_" + i, levelConfig.groundFireX[i], 38f, 22f, 32f, fire_texture, 8, 1, false);
            trackFire(fire);
            entityManager.addEntity(fire);
            collisionManager.register(fire);
        }

        // Ceiling fires
        for (int i = 0; i < levelConfig.ceilingFireX.length; i++) {
            Fire ceilingFire = new Fire("ceiling_fire_" + i, levelConfig.ceilingFireX[i], levelConfig.ceilingFireY[i], 20f, 30f, fire_texture, 8, 1, true);
            trackFire(ceilingFire);
            entityManager.addEntity(ceilingFire);
            collisionManager.register(ceilingFire);
        }

        // --- Pickups ---
        for (int i = 0; i < levelConfig.towelX.length; i++) {
            WetTowelPickup towel = new WetTowelPickup("towel" + i, levelConfig.towelX[i], levelConfig.towelY[i]);
            entityManager.addEntity(towel);
            collisionManager.register(towel);
        }

        for (int i = 0; i < levelConfig.maskX.length; i++) {
            MaskPickup mask = new MaskPickup("mask" + i, levelConfig.maskX[i], levelConfig.maskY[i]);
            mask.setOnTimerExtend(() -> {
                if (timer != null) timer.addTime(mask.getTimerExtend());
            });
            entityManager.addEntity(mask);
            collisionManager.register(mask);
        }

        // --- NPC ---
        npc = new NPC("npc_child", levelConfig.npcX, levelConfig.npcY, "Child", levelConfig.npcMaxHp);
        entityManager.addEntity(npc);
        collisionManager.register(npc);

        // --- Exit door ---
        ExitDoor exit = new ExitDoor("exit_door", levelConfig.exitX, levelConfig.exitY, 40f, 60f, 1, ioManager);
        entityManager.addEntity(exit);
        collisionManager.register(exit);

        // --- Register collisions ---
        collisionManager.register(player);

        // --- Collision rules ---
        mediator = new CollisionMediator();

        mediator.addRule(Fire.class, Damageable.class, (fire, target) -> {
            if (!target.isInvincible()) {
                target.takeDamage(fire.getDamage());
                ioManager.broadcast(GameEvents.PLAYER_HIT_FIRE);
            }
        });

        mediator.addRule(MaskPickup.class, Player.class, (pickup, playerTarget) -> {
            if (!pickup.isDestroyed()) {
                pickup.onPickup(playerTarget);
                ioManager.broadcast(GameEvents.ITEM_COLLECTED);
            }
        });

        mediator.addRule(WetTowelPickup.class, Player.class, (pickup, p) -> {
            if (!pickup.isDestroyed()) {
                pickup.onPickup(p);
                ioManager.broadcast(GameEvents.ITEM_COLLECTED);
            }
        });

        mediator.addRule(NPC.class, Player.class, (npcEntity, p) -> {
            if (npcEntity.getState() == NPC.State.WAITING && !p.isCarryingNPC()) {
                p.pickUpNPC();
                p.getStatusEffects().apply(new SlowEffect(CARRY_SLOW_KEY, 0.3f, Float.MAX_VALUE));
                npcEntity.startFollowing(p);
            }
        });

        mediator.addRule(ExitDoor.class, Player.class, (door, p) -> {
            if (p.isCarryingNPC()) {
                boolean npcSurvived = npc != null && npc.isAlive();
                int actualRescued = npcSurvived ? 1 : 0;
                
                p.rescueNPC();
                p.getStatusEffects().removeByKey(CARRY_SLOW_KEY);
                ioManager.broadcast(GameEvents.NPC_RESCUED);
            }
            // if (p.getRescuedCount() >= door.getRequiredRescues()) {
                ioManager.broadcast(GameEvents.PLAYER_WIN);
            // }
        });

        // --- Events ---
        ioManager.registerEvent(GameEvents.PLAYER_WIN, () -> {
            Gdx.app.log("Game", "Player won level " + levelConfig.levelNumber + "!");
            audioManager.play("victory.mp3");

            boolean npcSurvived = npc != null && npc.isAlive();
            int actualRescued = npcSurvived ? 1 : 0;

            ScoreContext context = new ScoreContext("PLAYER_ESCAPED");
            context.put("objectiveComplete", true);
            context.put("npcsRescued", actualRescued);
            context.put("timeRemaining", timer.getTimeRemaining());
            context.put("npcSurvived", npcSurvived);
            context.put("npcHealthRemaining", npcSurvived ? npc.getHp() / npc.getMaxHp() : 0f);
            context.put("levelNumber", levelConfig.levelNumber);
            scoreManager.applyRules(context);

            Gdx.app.log("Score", "Final Score: " + scoreManager.getFinalScore());

            ScoreBoardScene board = (ScoreBoardScene) sceneManager.getScene(SceneType.SCORE_BOARD.name());
            if (board != null) {
                if (levelConfig.levelNumber < 3) {
                    int nextLvl = levelConfig.levelNumber + 1;
                    Gdx.app.log("Game", "Setting up next level: " + nextLvl);
                    board.setNextScene(SceneType.TEST_SCENE.name());
                    board.setNextLevel(nextLvl);
                } else {
                    board.setNextScene(SceneType.CONGRATULATION.name());
                }
            }
            Gdx.app.postRunnable(() -> sceneManager.setScene(SceneType.SCORE_BOARD.name()));
        });

        ioManager.registerEvent(GameEvents.NPC_RESCUED, () -> {
            Gdx.app.log("Game", "NPC rescued!");
        });
    }

    @Override
    protected void onHide() {
        entityManager.disposeAll();
        collisionManager.clear();
        fires.clear();
        if (playerInput != null) {
            ioManager.removeInputListener(playerInput);
            playerInput = null;
        }
        movementInput = null;
        groundDetector = null;
        player = null;
        npc = null;
        if (mediator != null) {
            mediator.clear();
            mediator = null;
        }
        if (platformTex != null) {
            platformTex.dispose();
            platformTex = null;
        }
        if (backgroundTex != null) {
            backgroundTex.dispose();
            backgroundTex = null;
        }
        ioManager.clearEvent(GameEvents.PLAYER_WIN);
        ioManager.clearEvent(GameEvents.NPC_RESCUED);
        if (hud != null) { hud.dispose(); hud = null; }
        deathHandled = false;
    }

    @Override
    public void render(float delta) {
        try {
            delta = Math.min(delta, MAX_DELTA);

            Gdx.gl.glClearColor(0.1f, 0.05f, 0.0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCamera(delta);

        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.setColor(Color.WHITE);
        // Draw background first
        if (backgroundTex != null) {
                  batch.draw(backgroundTex, 0, 0, levelConfig.worldWidth, levelConfig.worldHeight);
        }
        entityManager.renderAll(batch);
        batch.end();
        drawStageAndUI(delta);

        if (player.applyJumpIfRequested()) {
            ioManager.broadcast(GameEvents.PLAYER_JUMP);
        }
        movementManager.applyMovement(
                player,
                player.getMovementState(),
                player.getMovementConfig(),
                movementInput,
                delta);

        groundDetector.checkFallCondition(player);
        groundDetector.checkGroundDetection(player);

        if (!deathHandled && hasPlayerFallenOutOfScreen()) {
            player.kill();
        }

        if (!deathHandled && !player.isAlive()) {
            deathHandled = true;
            ioManager.broadcast(GameEvents.PLAYER_DEAD);
        }
        } catch (Exception e) {
            Gdx.app.error("TestScene", "Error in render", e);
            e.printStackTrace();
        }
    }

    @Override
    public void update(float delta) {
        try {
            super.update(delta);
            delta = Math.min(delta, MAX_DELTA);

            // Tick the timer
            if (timer != null) {
                timer.update(delta);
                if (timer.isFinished()) {
                    onTimerFinished();
                }
            }

            if (hud != null && player != null) hud.update(delta, player.getHp());
            growFires(delta);
            entityManager.updateAll(delta);
            playerInput.update(delta);
            movementInput.update();
            player.updateMovementRules(movementInput, delta);
            collisionManager.update(delta);
            Array<Collidable[]> pairs = collisionManager.resolveCollisions();
            mediator.resolve(pairs);
            syncPlayerSpeedMultiplier();
        } catch (Exception e) {
            Gdx.app.error("TestScene", "Error in update", e);
            e.printStackTrace();
        }
    }

    private void onTimerFinished() {
        Gdx.app.log("Game", "Time's up! Game Over.");
        GameOverScene gameOverScene = (GameOverScene) sceneManager.getScene(SceneType.GAME_OVER.name());
        if (gameOverScene != null) {
            gameOverScene.setRetryScene(SceneType.TEST_SCENE.name());
            gameOverScene.setRetryLevel(levelConfig.levelNumber);
        }
        Gdx.app.postRunnable(() -> sceneManager.setScene(SceneType.GAME_OVER.name()));
    }

    @Override
    protected void renderUI() {
        if (hud == null || player == null) return;

        // Build buffs string
        StringBuilder buffs = new StringBuilder();
        for (StatusEffect effect : player.getStatusEffects().getAll()) {
            if (buffs.length() > 0) buffs.append("  |  ");
            buffs.append(effect.getName());
            if (effect.getRemainingTime() < Float.MAX_VALUE) {
                buffs.append(" ").append((int) Math.ceil(effect.getRemainingTime())).append("s");
            }
        }

        hud.render(batch,
            player.getHp(), player.getMaxHp(),
            buffs.toString(),
            player.isCarryingNPC(),
            "Rescue the NPC and reach the EXIT!"
        );

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
            y -= timerLayout.height + 6f;

            // Score line
            String scoreLine = "Score: " + scoreManager.getScore();
            timerLayout.setText(timerFont, scoreLine);
            timerFont.setColor(Color.WHITE);
            timerFont.draw(batch, scoreLine, x - timerLayout.width, y);
            y -= timerLayout.height + 6f;

            // Rescued line
            String rescuedLine = "Rescued: " + (player != null ? player.getRescuedCount() : 0) + "/1";
            timerLayout.setText(timerFont, rescuedLine);
            timerFont.setColor(Color.WHITE);
            timerFont.draw(batch, rescuedLine, x - timerLayout.width, y);

            timerFont.setColor(Color.WHITE);
        }
    }

    private void trackFire(Fire fire) {
        fire.setMaxScale(FIRE_MAX_SCALE_X, FIRE_MAX_SCALE_Y);
        fires.add(fire);
    }

    private void syncPlayerSpeedMultiplier() {
        if (player == null) {
            return;
        }
        float slowMultiplier = StatusEffectMath.strongestSlowMultiplier(
                player.getStatusEffects().getAllEffects(SlowEffect.class));
        player.setExternalSpeedMultiplier(slowMultiplier);
    }

    private void growFires(float delta) {
        float growX = FIRE_GROW_X_PER_SEC * delta;
        float growY = FIRE_GROW_Y_PER_SEC * delta;
        for (Fire fire : fires) {
            if (!fire.isActive() || fire.isDestroyed()) {
                continue;
            }
            fire.addScale(growX, growY);
        }
    }

    private void updateCamera(float delta) {
        if (player == null || camera == null) return;

        float targetX = player.getX() + player.getWidth() / 2f;
        float targetY = player.getY() + player.getHeight() / 2f;
        camera.position.lerp(new Vector3(targetX, targetY, 0), CAMERA_LERP * delta);

        float halfViewportW = screenWidth / 2f;
        float halfViewportH = screenHeight / 2f;
        camera.position.x = Math.max(halfViewportW, Math.min(levelConfig.worldWidth - halfViewportW, camera.position.x));
        camera.position.y = Math.max(halfViewportH, Math.min(levelConfig.worldHeight - halfViewportH, camera.position.y));

        camera.update();
    }

    private boolean hasPlayerFallenOutOfScreen() {
        return player != null && player.getY() + player.getHeight() < -FALL_DEATH_BUFFER;
    }

    @Override
    public void dispose() {
        if (timerFont != null) { timerFont.dispose(); timerFont = null; }
        super.dispose();
    }
}

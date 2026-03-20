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
import io.github.team3engine.engine.collision.CollisionMediator;
import io.github.team3engine.engine.entity.EntityManager;
import io.github.team3engine.engine.interfaces.Collidable;
import io.github.team3engine.engine.interfaces.Damageable;
import io.github.team3engine.engine.movement.MovementManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.engine.scoring.ScoreContext;
import io.github.team3engine.engine.interfaces.ScoreRule;
import io.github.team3engine.engine.scoring.ScoreManager;
import java.util.List;
import io.github.team3engine.engine.status.StatusEffect;
import io.github.team3engine.game.entities.*;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.inputs.PlayerInput;
import io.github.team3engine.game.physics.GroundDetector;
import io.github.team3engine.game.status.SlowEffect;
import io.github.team3engine.game.ui.HUDRenderer;

public class TestScene extends BaseScene implements GameplayScene {
    private static final float MAX_DELTA = 0.07f;
    private static final float FIRE_GROW_X_PER_SEC = 0.18f;
    private static final float FIRE_GROW_Y_PER_SEC = 0.06f;
    private static final float FIRE_MAX_SCALE_X = 2f;
    private static final float FIRE_MAX_SCALE_Y = 2f;

    private Player player;
    private MovementInput movementInput;
    private PlayerInput playerInput;
    private GroundDetector groundDetector;
    private CollisionMediator mediator;
    private Texture platformTex;
    private final Array<Fire> fires = new Array<>();
    private HUDRenderer hud;

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final MovementManager movementManager;
    private final BitmapFont font;

    private final int screenWidth;
    private final int screenHeight;
    private boolean deathHandled;
    private final List<ScoreRule> scoreRules;

    public TestScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager,
                     IOManager ioManager, AudioManager audioManager, EntityManager entityManager,
                     CollisionManager collisionManager, MovementManager movementManager,
                     int screenWidth, int screenHeight,
                     List<ScoreRule> scoreRules) {
        super(batch);
        this.font = sharedFont;
        this.scoreRules = scoreRules;
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
        super.onShow();
        enableTimer();
        if (hud == null) hud = new HUDRenderer(font);
        hud.init(100f);
        clearHudLines();
        addHudLine(() -> "Score: " + io.github.team3engine.engine.scoring.ScoreManager.getInstance().getScore());
        addHudLine(() -> "Rescued: " + (player != null ? player.getRescuedCount() : 0) + "/1");
        fires.clear();
        deathHandled = false;

        // --- Register score rules ---
        ScoreManager.getInstance().reset();
        if (scoreRules != null) {
            for (ScoreRule rule : scoreRules) {
                ScoreManager.getInstance().addRule(rule);
            }
        }

        playerInput = new PlayerInput();
        ioManager.addInputListener(playerInput);
        Gdx.input.setInputProcessor(ioManager);

        float gw = screenWidth;
        float gh = screenHeight;

        // --- Player ---
        player = new Player("player", gw * 0.1f, 60f, 20f * 1.5f, 36f*1.5f, 100f, gw, gh);
        entityManager.addEntity(player);
        player.getMovementState().reset();
        movementInput = new MovementInput(player.getMovementState(), ioManager, playerInput);
        groundDetector = new GroundDetector(movementManager, collisionManager, entityManager);

        // --- Platforms ---
        platformTex = new Texture(Gdx.files.internal("platform.png"));

        Platform ground = new Platform("ground", 0, 0, gw, 40f, platformTex);
        entityManager.addEntity(ground);
        Platform p1 = new Platform("plat_1", 80f, 130f, 200f, 16f, platformTex);
        entityManager.addEntity(p1);
        Platform p2 = new Platform("plat_2", 350f, 210f, 220f, 16f, platformTex);
        entityManager.addEntity(p2);
        Platform p3 = new Platform("plat_3", 620f, 300f, 200f, 16f, platformTex);
        entityManager.addEntity(p3);

        // --- Fire hazards ---
        Texture fire_texture = new Texture(Gdx.files.internal("ui/sprites/fire_spritesheet.png"));
        Fire fire1a = new Fire("fire_1a", 150f, 40f, 20f, 30f, fire_texture, 8, 1, false);
        Fire ceilingFire = new Fire("ceiling_fire_1a", 200f, 130f, 20f, 30f, fire_texture, 8, 1, true);
        Fire ceilingFire2 = new Fire("ceiling_fire_1b", 360f, 210f, 20f, 30f, fire_texture, 8, 1, true);

        // Fire fire1b = new Fire("fire_1b", 248f, 40f, 25f, 35f, fire_texture, 8, 1);
        // Fire fire1c = new Fire("fire_1c", 230f, 72f, 20f, 22f);
        trackFire(fire1a);
        trackFire(ceilingFire);
        trackFire(ceilingFire2);
        // trackFire(fire1b);
        // trackFire(fire1c);
        entityManager.addEntity(fire1a);
        entityManager.addEntity(ceilingFire);
        entityManager.addEntity(ceilingFire2);
        // entityManager.addEntity(fire1b);
        // entityManager.addEntity(fire1c);

        // Fire fire2a = new Fire("fire_2a", 680f, 40f, 35f, 40f);
        // Fire fire2b = new Fire("fire_2b", 713f, 40f, 28f, 32f);
        // Fire fire2c = new Fire("fire_2c", 695f, 78f, 22f, 24f);
        // trackFire(fire2a);
        // trackFire(fire2b);
        // trackFire(fire2c);
        // entityManager.addEntity(fire2a);
        // entityManager.addEntity(fire2b);
        // entityManager.addEntity(fire2c);

        // Fire fire3a = new Fire("fire_3a", 520f, 226f, 28f, 30f);
        // Fire fire3b = new Fire("fire_3b", 546f, 226f, 22f, 25f);
        // trackFire(fire3a);
        // trackFire(fire3b);
        // entityManager.addEntity(fire3a);
        // entityManager.addEntity(fire3b);

        // --- Pickups ---
        WetTowelPickup towel = new WetTowelPickup("towel", 180f, 175f);
        MaskPickup mask = new MaskPickup("mask", 550f, 75f);
        mask.setOnTimerExtend(() -> {
            if (getTimer() != null) getTimer().addTime(mask.getTimerExtend());
        });
        entityManager.addEntity(towel);
        entityManager.addEntity(mask);

        // --- NPC ---
        NPC npc = new NPC("npc_child", 350f, 55f, "Child");
        entityManager.addEntity(npc);

        // --- Exit door ---
        ExitDoor exit = new ExitDoor("exit_door", gw - 55f, 40f, 40f, 60f, 1, ioManager);
        entityManager.addEntity(exit);

        // --- Register collisions ---
        collisionManager.register(player);
        collisionManager.register(ground);
        collisionManager.register(p1);
        collisionManager.register(p2);
        collisionManager.register(p3);
        collisionManager.register(fire1a);
        collisionManager.register(ceilingFire);
        collisionManager.register(ceilingFire2);
        // collisionManager.register(fire1b);
        // collisionManager.register(fire1c);
        // collisionManager.register(fire2a);
        // collisionManager.register(fire2b);
        // collisionManager.register(fire2c);
        // collisionManager.register(fire3a);
        // collisionManager.register(fire3b);
        collisionManager.register(towel);
        collisionManager.register(mask);
        collisionManager.register(npc);
        collisionManager.register(exit);

        // --- Collision rules ---
        mediator = new CollisionMediator();

        mediator.addRule(Fire.class, Damageable.class, (fire, target) -> {
            if (!target.isInvincible()) {
                target.takeDamage(fire.getDamage());
            }
        });

        mediator.addRule(MaskPickup.class, Player.class, (pickup, playerTarget) -> {
            if (!pickup.isDestroyed()) {
                pickup.onPickup(playerTarget);
            }
        });

        mediator.addRule(WetTowelPickup.class, Player.class, (pickup, p) -> {
            if (!pickup.isDestroyed()) {
                pickup.onPickup(p);
            }
        });

        mediator.addRule(NPC.class, Player.class, (npcEntity, p) -> {
            if (npcEntity.getState() == NPC.State.WAITING && !p.isCarryingNPC()) {
                p.pickUpNPC();
                p.getStatusEffects().apply(new SlowEffect(0.3f, Float.MAX_VALUE));
                npcEntity.startFollowing(p);
            }
        });

        mediator.addRule(ExitDoor.class, Player.class, (door, p) -> {
            if (p.isCarryingNPC()) {
                p.rescueNPC();
                SlowEffect slow = p.getStatusEffects().getEffect(SlowEffect.class);
                if (slow != null) {
                    p.getStatusEffects().remove(slow);
                }
                ioManager.broadcast(GameEvents.NPC_RESCUED);
            }
            // if (p.getRescuedCount() >= door.getRequiredRescues()) {
                ioManager.broadcast(GameEvents.PLAYER_WIN);
            // }
        });

        // --- Events ---
        ioManager.registerEvent(GameEvents.PLAYER_WIN, () -> {
            Gdx.app.log("Game", "Player won!");
            audioManager.play("victory.mp3");

            ScoreContext context = new ScoreContext("PLAYER_ESCAPED");
            context.put("objectiveComplete", true);
            context.put("npcsRescued", player.getRescuedCount());
            context.put("timeRemaining", getTimer().getTimeRemaining());
            ScoreManager.getInstance().applyRules(context);

            Gdx.app.log("Score", "Final Score: " + ScoreManager.getInstance().getFinalScore());

            // Show scoreboard, next level = TEST_SCENE (change to SCENE_2 when ready)
            ScoreBoardScene board = (ScoreBoardScene) sceneManager.getScene(SceneType.SCORE_BOARD.name());
            if (board != null) board.setNextScene(SceneType.TEST_SCENE.name());
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
        if (mediator != null) {
            mediator.clear();
            mediator = null;
        }
        if (platformTex != null) {
            platformTex.dispose();
            platformTex = null;
        }
        ioManager.clearEvent(GameEvents.PLAYER_WIN);
        ioManager.clearEvent(GameEvents.NPC_RESCUED);
        if (hud != null) { hud.dispose(); hud = null; }
        deathHandled = false;
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, MAX_DELTA);

        Gdx.gl.glClearColor(0.1f, 0.05f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        entityManager.renderAll(batch);
        batch.end();
        drawStageAndUI(delta);

        movementManager.applyMovement(player, player.getMovementState(), movementInput, delta);

        groundDetector.checkFallCondition(player);
        groundDetector.checkGroundDetection(player);

        if (!deathHandled && !player.isAlive()) {
            deathHandled = true;
            ioManager.broadcast(GameEvents.PLAYER_DEAD);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);  // ticks the timer
        delta = Math.min(delta, MAX_DELTA);
        if (hud != null && player != null) hud.update(delta, player.getHp());
        growFires(delta);
        entityManager.updateAll(delta);
        playerInput.update(delta);
        movementInput.update();
        collisionManager.update(delta);
        Array<Collidable[]> pairs = collisionManager.resolveCollisions();
        mediator.resolve(pairs);
    }

    @Override
    protected void onTimerFinished() {
        Gdx.app.log("Game", "Time's up! Game Over.");
        GameOverScene gameOverScene = (GameOverScene) sceneManager.getScene(SceneType.GAME_OVER.name());
        if (gameOverScene != null) {
            gameOverScene.setRetryScene(SceneType.TEST_SCENE.name());
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
    }

    private void trackFire(Fire fire) {
        fire.setMaxScale(FIRE_MAX_SCALE_X, FIRE_MAX_SCALE_Y);
        fires.add(fire);
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
}

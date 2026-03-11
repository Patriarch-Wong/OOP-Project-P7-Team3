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
import io.github.team3engine.engine.status.StatusEffect;
import io.github.team3engine.game.entities.*;
import io.github.team3engine.game.inputs.PlayerInput;
import io.github.team3engine.game.physics.GroundDetector;
import io.github.team3engine.game.status.SlowEffect;

public class TestScene extends BaseScene {
    private static final float MAX_DELTA = 0.07f;

    private Player player;
    private MovementInput movementInput;
    private PlayerInput playerInput;
    private GroundDetector groundDetector;
    private CollisionMediator mediator;
    private Texture platformTex;

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final MovementManager movementManager;
    private final BitmapFont font;

    private final int screenWidth;
    private final int screenHeight;

    public TestScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager,
                     IOManager ioManager, AudioManager audioManager, EntityManager entityManager,
                     CollisionManager collisionManager, MovementManager movementManager,
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

        // --- Player ---
        player = new Player("player", gw * 0.1f, 60f, 20f, 36f, 100f, gw, gh);
        entityManager.addEntity(player);
        player.getMovementState().reset();
        movementInput = new MovementInput(player.getMovementState(), ioManager, playerInput);
        groundDetector = new GroundDetector(movementManager, collisionManager, entityManager);

        // --- Platforms ---
        platformTex = new Texture(Gdx.files.internal("platform.png"));

        // Ground
        Platform ground = new Platform("ground", 0, 0, gw, 40f, platformTex);
        entityManager.addEntity(ground);

        // Platform 1: lower left (reachable from ground)
        Platform p1 = new Platform("plat_1", 80f, 130f, 200f, 16f, platformTex);
        entityManager.addEntity(p1);

        // Platform 2: middle (reachable from p1 or ground with good jump)
        Platform p2 = new Platform("plat_2", 350f, 210f, 220f, 16f, platformTex);
        entityManager.addEntity(p2);

        // Platform 3: upper right (reachable from p2)
        Platform p3 = new Platform("plat_3", 620f, 300f, 200f, 16f, platformTex);
        entityManager.addEntity(p3);

        // --- Fire hazards (clusters to look like spreading fire) ---
        // Ground fire cluster left — blocks the path, player must jump over
        Fire fire1a = new Fire("fire_1a", 220f, 40f, 30f, 35f);
        Fire fire1b = new Fire("fire_1b", 248f, 40f, 25f, 28f);
        Fire fire1c = new Fire("fire_1c", 230f, 72f, 20f, 22f); // on top, looks like it's spreading up
        entityManager.addEntity(fire1a);
        entityManager.addEntity(fire1b);
        entityManager.addEntity(fire1c);

        // Ground fire cluster right — near the exit, have to navigate around
        Fire fire2a = new Fire("fire_2a", 680f, 40f, 35f, 40f);
        Fire fire2b = new Fire("fire_2b", 713f, 40f, 28f, 32f);
        Fire fire2c = new Fire("fire_2c", 695f, 78f, 22f, 24f);
        entityManager.addEntity(fire2a);
        entityManager.addEntity(fire2b);
        entityManager.addEntity(fire2c);

        // Platform fire — on middle platform, near the NPC
        Fire fire3a = new Fire("fire_3a", 520f, 226f, 28f, 30f);
        Fire fire3b = new Fire("fire_3b", 546f, 226f, 22f, 25f);
        entityManager.addEntity(fire3a);
        entityManager.addEntity(fire3b);

        // --- Pickups ---
        // Wet towel on platform 1
        WetTowelPickup towel = new WetTowelPickup("towel", 180f, 175f);
        // Mask on ground level, past the NPC
        MaskPickup mask = new MaskPickup("mask", 550f, 75f);
        entityManager.addEntity(towel);
        entityManager.addEntity(mask);

        // --- NPC on ground level, past the first fire cluster ---
        NPC npc = new NPC("npc_child", 350f, 55f, "Child");
        entityManager.addEntity(npc);

        // --- Exit door on far right ground ---
        ExitDoor exit = new ExitDoor("exit_door", gw - 55f, 40f, 40f, 60f, 1, ioManager);
        entityManager.addEntity(exit);

        // --- Register collisions ---
        collisionManager.register(player);
        collisionManager.register(ground);
        collisionManager.register(p1);
        collisionManager.register(p2);
        collisionManager.register(p3);
        collisionManager.register(fire1a);
        collisionManager.register(fire1b);
        collisionManager.register(fire1c);
        collisionManager.register(fire2a);
        collisionManager.register(fire2b);
        collisionManager.register(fire2c);
        collisionManager.register(fire3a);
        collisionManager.register(fire3b);
        collisionManager.register(towel);
        collisionManager.register(mask);
        collisionManager.register(npc);
        collisionManager.register(exit);

        // --- Collision rules (Mediator pattern) ---
        mediator = new CollisionMediator();

        // Fire damages any Damageable entity
        mediator.addRule(Fire.class, Damageable.class, (fire, target) -> {
            if (!target.isInvincible()) {
                target.takeDamage(fire.getDamage());
            }
        });

        // Mask pickup grants 50% damage reduction
        mediator.addRule(MaskPickup.class, Player.class, (pickup, p) -> {
            if (!pickup.isDestroyed()) {
                pickup.onPickup(p);
            }
        });

        // Wet towel pickup grants 30% damage reduction
        mediator.addRule(WetTowelPickup.class, Player.class, (pickup, p) -> {
            if (!pickup.isDestroyed()) {
                pickup.onPickup(p);
            }
        });

        // NPC collection — player picks up NPC and gets slow debuff
        mediator.addRule(NPC.class, Player.class, (npcEntity, p) -> {
            if (!npcEntity.isDestroyed() && !p.isCarryingNPC()) {
                p.pickUpNPC();
                p.getStatusEffects().apply(new SlowEffect(0.3f, Float.MAX_VALUE));
                npcEntity.destroy();
            }
        });

        // Exit door — rescue NPC or win
        mediator.addRule(ExitDoor.class, Player.class, (door, p) -> {
            if (p.isCarryingNPC()) {
                p.rescueNPC();
                SlowEffect slow = p.getStatusEffects().getEffect(SlowEffect.class);
                if (slow != null) {
                    p.getStatusEffects().remove(slow);
                }
                ioManager.broadcast("NPC_RESCUED");
            }
            if (p.getRescuedCount() >= door.getRequiredRescues()) {
                ioManager.broadcast("PLAYER_WIN");
            }
        });

        // register scene specific events in scene
        ioManager.registerEvent("PLAYER_WIN", () -> {
            Gdx.app.log("Game", "Player won!");
            audioManager.play("victory.mp3");
            // Restart the test scene
            Gdx.app.postRunnable(() -> sceneManager.setScene(SceneType.TEST_SCENE.name()));
        });
        ioManager.registerEvent("NPC_RESCUED", () -> {
            Gdx.app.log("Game", "NPC rescued!");
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
        ioManager.clearEvent("PLAYER_WIN");
        ioManager.clearEvent("NPC_RESCUED");

    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, MAX_DELTA);

        Gdx.gl.glClearColor(0.1f, 0.05f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        entityManager.renderAll(batch);
        batch.end();
        drawStageAndUI(delta);

        // Apply movement (player.update() already called by entityManager.updateAll)
        movementManager.applyMovement(player, player.getMovementState(), movementInput, delta);

        groundDetector.checkFallCondition(player);
        groundDetector.checkGroundDetection(player);

        // Check death
        if (!player.isAlive()) {
            ioManager.broadcast("PLAYER_DEAD");
        }
    }

    @Override
    public void update(float delta) {
        delta = Math.min(delta, MAX_DELTA);
        entityManager.updateAll(delta);
        playerInput.update(delta);
        movementInput.update();
        collisionManager.update(delta);
        // Platform.onCollision() still runs for physics pushout
        Array<Collidable[]> pairs = collisionManager.resolveCollisions();
        // Game rules dispatched through mediator (no instanceof in entities)
        mediator.resolve(pairs);
    }

    @Override
    protected void renderUI() {
        // HP
        String hpText = "HP: " + (int) player.getHp() + " / " + (int) player.getMaxHp();
        font.draw(batch, hpText, 20, screenHeight - 20);

        // Buffs
        StringBuilder buffs = new StringBuilder();
        for (StatusEffect effect : player.getStatusEffects().getAll()) {
            if (buffs.length() > 0) buffs.append("  |  ");
            buffs.append(effect.getName());
            if (effect.getRemainingTime() < Float.MAX_VALUE) {
                buffs.append(" ").append((int) Math.ceil(effect.getRemainingTime())).append("s");
            }
        }
        if (buffs.length() > 0) {
            font.draw(batch, buffs.toString(), 20, screenHeight - 40);
        }

        // Carrying status
        if (player.isCarryingNPC()) {
            font.draw(batch, "Carrying: Child", 20, screenHeight - 60);
        }

        // Rescued count
        font.draw(batch, "Rescued: " + player.getRescuedCount() + "/1",
                screenWidth - 150, screenHeight - 20);

        // Instructions
        font.draw(batch, "Rescue the NPC and reach the EXIT!", 200, screenHeight - 20);
    }
}

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
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.engine.status.StatusEffect;
import io.github.team3engine.game.entities.*;
import io.github.team3engine.game.inputs.PlayerInput;
import io.github.team3engine.game.physics.GroundDetector;

public class TestScene extends BaseScene {
    private static final float MAX_DELTA = 0.07f;

    private Player player;
    private MovementInput movementInput;
    private PlayerInput playerInput;
    private GroundDetector groundDetector;
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

        // Platform 1: lower left
        Platform p1 = new Platform("plat_1", 100f, 140f, 180f, 16f, platformTex);
        entityManager.addEntity(p1);

        // Platform 2: middle
        Platform p2 = new Platform("plat_2", 380f, 240f, 200f, 16f, platformTex);
        entityManager.addEntity(p2);

        // Platform 3: upper right
        Platform p3 = new Platform("plat_3", 650f, 340f, 180f, 16f, platformTex);
        entityManager.addEntity(p3);

        // --- Fire hazards ---
        Fire fire1 = new Fire("fire_1", 250f, 40f, 30f, 40f);
        Fire fire2 = new Fire("fire_2", 500f, 40f, 35f, 45f);
        Fire fire3 = new Fire("fire_3", 420f, 256f, 25f, 35f);
        entityManager.addEntity(fire1);
        entityManager.addEntity(fire2);
        entityManager.addEntity(fire3);

        // --- Pickups ---
        WetTowelPickup towel = new WetTowelPickup("towel", 150f, 80f);
        MaskPickup mask = new MaskPickup("mask", 700f, 380f);
        entityManager.addEntity(towel);
        entityManager.addEntity(mask);

        // --- NPC ---
        NPC npc = new NPC("npc_child", 450f, 256f, "Child");
        entityManager.addEntity(npc);

        // --- Exit door ---
        ExitDoor exit = new ExitDoor("exit_door", gw - 60f, 40f, 40f, 60f, 1, ioManager);
        entityManager.addEntity(exit);

        // --- Register collisions ---
        collisionManager.register(player);
        collisionManager.register(ground);
        collisionManager.register(p1);
        collisionManager.register(p2);
        collisionManager.register(p3);
        collisionManager.register(fire1);
        collisionManager.register(fire2);
        collisionManager.register(fire3);
        collisionManager.register(towel);
        collisionManager.register(mask);
        collisionManager.register(npc);
        collisionManager.register(exit);
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
        if (platformTex != null) {
            platformTex.dispose();
            platformTex = null;
        }
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, MAX_DELTA);
        update(delta);

        Gdx.gl.glClearColor(0.1f, 0.05f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        entityManager.renderAll(batch);
        batch.end();
        drawStageAndUI(delta);

        // Apply movement
        movementManager.applyMovement(player, player.getMovementState(), movementInput, delta);
        player.update(delta);

        groundDetector.checkFallCondition(player);
        groundDetector.checkGroundDetection(player);

        // Check death
        if (!player.isAlive()) {
            ioManager.broadcast("PLAYER_DEAD");
        }
    }

    @Override
    public void update(float delta) {
        entityManager.updateAll(delta);
        playerInput.update(delta);
        movementInput.update();
        collisionManager.update(delta);
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
                buffs.append(" ").append((int) effect.getRemainingTime()).append("s");
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

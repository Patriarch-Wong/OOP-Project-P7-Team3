package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.ui.SceneButtonFactory;

public class MainMenuScene extends BaseScene {

    private final SceneManager sceneManager;
    private final IOManager    ioManager;
    private final AudioManager audioManager;
    private final BitmapFont   font;
    private final GlyphLayout  layout = new GlyphLayout();

    private static final float PARALLAX_STRENGTH = 40f;
    private static final float SMOOTH_SPEED      = 5f;
    private static final float ZOOM              = 1.15f;

    private Texture       background;
    private ShapeRenderer shape;
    private Skin          skin;

    private float currentOffsetX = 0f;
    private float currentOffsetY = 0f;

    public MainMenuScene(SpriteBatch batch, BitmapFont sharedFont,
                         SceneManager sceneManager, IOManager ioManager,
                         AudioManager audioManager,
                         int screenWidth, int screenHeight) {
        super(batch);
        this.font         = sharedFont;
        this.sceneManager = sceneManager;
        this.ioManager    = ioManager;
        this.audioManager = audioManager;
    }

    @Override
    public void onShow() {
        super.onShow();
        shape = new ShapeRenderer();

        if (Gdx.files.internal("background/main-menu-background.png").exists()) {
            background = new Texture("background/main-menu-background.png");
        } else {
            background = new Texture("background.png");
        }

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        float sw      = Gdx.graphics.getWidth();
        float sh      = Gdx.graphics.getHeight();
        float centerX = sw / 2f;
        float centerY = sh / 2f;
        float gap     = 16f;
        float btnH    = SceneButtonFactory.BUTTON_HEIGHT;

        TextButton scene1Button = SceneButtonFactory.create("Scene 1", skin,
                () -> ioManager.broadcast(GameEvents.START_GAME));
        scene1Button.setPosition(centerX - SceneButtonFactory.BUTTON_WIDTH / 2f,
                centerY + gap / 2f);

        TextButton testSceneButton = SceneButtonFactory.create("Test Scene", skin,
                () -> ioManager.broadcast(GameEvents.START_GAME_TEST));
        testSceneButton.setPosition(centerX - SceneButtonFactory.BUTTON_WIDTH / 2f,
                centerY - btnH - gap / 2f);

        getStage().addActor(scene1Button);
        getStage().addActor(testSceneButton);

        ioManager.registerEvent(GameEvents.START_GAME, () -> {
            Gdx.app.log("Game", "Starting game - Test Scene at Level 1");
            BaseScene scene = sceneManager.getScene(SceneType.TEST_SCENE.name());
            if (scene instanceof TestScene) {
                ((TestScene) scene).resetForNewGame();
            }
            sceneManager.setScene(SceneType.TEST_SCENE.name());
        });
    }

    @Override
    public void update(float delta) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        float normX = (mouseX / sw - 0.5f) * 2f;
        float normY = (mouseY / sh - 0.5f) * 2f;

        float targetX = -normX * PARALLAX_STRENGTH;
        float targetY =  normY * PARALLAX_STRENGTH;

        float alpha = Math.min(SMOOTH_SPEED * delta, 1f);
        currentOffsetX += (targetX - currentOffsetX) * alpha;
        currentOffsetY += (targetY - currentOffsetY) * alpha;
    }

    @Override
    public void render(float delta) {
        update(delta);

        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        clearScreen(0.05f, 0.05f, 0.08f, 1f);

        batch.begin();

        float drawW = sw * ZOOM;
        float drawH = sh * ZOOM;
        float baseX = (sw - drawW) / 2f;
        float baseY = (sh - drawH) / 2f;
        float drawX = baseX + currentOffsetX;
        float drawY = baseY + currentOffsetY;

        if (background != null) {
            batch.draw(background, drawX, drawY, drawW, drawH);
        }

        batch.end();

        // Dark overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0f, 0f, 0f, 0.55f);
        shape.rect(0, 0, sw, sh);
        shape.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        drawStageAndUI(delta);
    }

    @Override
    protected void renderUI() {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        font.setColor(new Color(1f, 0.5f, 0.1f, 1f));
        String title = "FIRE ESCAPE";
        layout.setText(font, title);
        font.draw(batch, title, (sw - layout.width) / 2f, sh / 2f + 120f);

        font.setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
        String sub = "Rescue survivors. Escape the blaze.";
        layout.setText(font, sub);
        font.draw(batch, sub, (sw - layout.width) / 2f, sh / 2f + 90f);

        font.setColor(Color.WHITE);
    }

    @Override
    public void onHide() {
        ioManager.clearEvent(GameEvents.START_GAME);
        ioManager.clearEvent(GameEvents.START_GAME_TEST);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (skin       != null) { skin.dispose();       skin       = null; }
        if (shape      != null) { shape.dispose();      shape      = null; }
        if (background != null) { background.dispose(); background = null; }
        Stage s = getStage();
        if (s != null) s.clear();
    }

    @Override
    public void dispose() {
        onHide();
        super.dispose();
    }
}
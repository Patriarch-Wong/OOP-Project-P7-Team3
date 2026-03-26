package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.team3engine.engine.audio.AudioManager;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.game.events.GameEvents;
import io.github.team3engine.game.score.ScoreManager;

public class MainMenuScene extends BaseScene {

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final ScoreManager scoreManager;
    private final GlyphLayout layout = new GlyphLayout();

    private static final float PARALLAX_STRENGTH = 40f;
    private static final float SMOOTH_SPEED = 5f;
    private static final float ZOOM = 1.15f;
    private static final float BUTTON_WIDTH = 220f;
    private static final float BUTTON_HEIGHT = 54f;

    private Texture background;
    private ShapeRenderer shape;
    private Skin skin;
    private TextButton startGameButton;

    private Texture pixelTexture;
    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private BitmapFont bodyFont;

    private float currentOffsetX = 0f;
    private float currentOffsetY = 0f;

    public MainMenuScene(SpriteBatch batch, BitmapFont sharedFont,
            SceneManager sceneManager, IOManager ioManager,
            AudioManager audioManager, ScoreManager scoreManager,
            int screenWidth, int screenHeight) {
        super(batch);
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
        this.scoreManager = scoreManager;
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

        pixelTexture = createPixelTexture();
        createFonts();
        createMenuButtonStyle();
        createButtons();
        audioManager.playMusic("title.mp3", true);

        ioManager.registerEvent(GameEvents.START_GAME, () -> {
            Gdx.app.log("Game", "Starting game - Test Scene at Level 1");
            scoreManager.resetScore();
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
        float targetY = normY * PARALLAX_STRENGTH;

        float alpha = Math.min(SMOOTH_SPEED * delta, 1f);
        currentOffsetX += (targetX - currentOffsetX) * alpha;
        currentOffsetY += (targetY - currentOffsetY) * alpha;
    }

    @Override
    public void render(float delta) {
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();

        clearScreen(0.05f, 0.05f, 0.08f, 1f);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, sw, sh);
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
        shape.setProjectionMatrix(batch.getProjectionMatrix());
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
        float centerX = sw / 2f;

        drawCenteredText(titleFont, "FIRE ESCAPE", centerX, sh - 64f, new Color(0.98f, 0.94f, 0.88f, 1f));
        drawCenteredText(subtitleFont, "ESCAPE BEFORE THE FIRE SPREADS", centerX, sh - 112f,
                new Color(1f, 0.78f, 0.42f, 1f));

        float cardX = 92f;
        float lineY = 250f;
        float lineGap = 28f;

        drawLeftText(bodyFont, "Mission Brief", cardX + 18f, lineY + 18f, new Color(1f, 0.82f, 0.48f, 1f));
        drawLeftText(bodyFont, "The building is on fire. Your only goal is to get out alive.",
                cardX + 18f, lineY - lineGap, Color.WHITE);
        drawLeftText(bodyFont, "Use A / D to move, Space to jump, and ESC to pause.", cardX + 18f,
                lineY - lineGap * 2f, new Color(0.88f, 0.91f, 0.96f, 1f));
        drawLeftText(bodyFont, "Grab the wet towel and mask, they'll buy you more time.", cardX + 18f,
                lineY - lineGap * 3f, new Color(0.88f, 0.91f, 0.96f, 1f));

        drawCenteredText(bodyFont, "Find the exit. Don't stop moving.", centerX, 30f,
                new Color(1f, 0.72f, 0.56f, 1f));
    }

    @Override
    protected void onResize(int width, int height) {
        layoutButtons(width);
    }

    @Override
    public void onHide() {
        ioManager.clearEvent(GameEvents.START_GAME);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
        disposeFonts();
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        if (shape != null) {
            shape.dispose();
            shape = null;
        }
        if (background != null) {
            background.dispose();
            background = null;
        }
        startGameButton = null;
        Stage s = getStage();
        if (s != null)
            s.clear();
    }

    private void layoutButtons(int width) {
        if (startGameButton == null) {
            return;
        }
        float buttonWidth = startGameButton.getWidth() > 0f ? startGameButton.getWidth() : BUTTON_WIDTH;
        startGameButton.setPosition((width - buttonWidth) / 2f, 60f);
    }

    @Override
    public void dispose() {
        onHide();
        super.dispose();
    }

    private void createButtons() {
        startGameButton = new TextButton("Start Rescue", skin, "menu-primary");
        startGameButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        layoutButtons(Gdx.graphics.getWidth());

        startGameButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                ioManager.broadcast(GameEvents.START_GAME);
            }
        });

        getStage().addActor(startGameButton);
    }

    private void createMenuButtonStyle() {
        TextButton.TextButtonStyle baseStyle = skin.get(TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle menuStyle = new TextButton.TextButtonStyle(baseStyle);
        menuStyle.up = skin.newDrawable(baseStyle.up, new Color(0.22f, 0.23f, 0.28f, 0.96f));
        menuStyle.over = skin.newDrawable(baseStyle.up, new Color(0.85f, 0.36f, 0.10f, 1f));
        menuStyle.down = skin.newDrawable(baseStyle.down != null ? baseStyle.down : baseStyle.up,
                new Color(0.65f, 0.24f, 0.07f, 1f));
        menuStyle.checked = menuStyle.down;
        menuStyle.font = bodyFont;
        menuStyle.fontColor = new Color(0.98f, 0.94f, 0.88f, 1f);
        menuStyle.overFontColor = Color.WHITE;
        menuStyle.downFontColor = Color.WHITE;
        skin.add("menu-primary", menuStyle);
    }

    private void createFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("ui/OpenSans_Condensed-SemiBold.ttf"));

        titleFont = generator.generateFont(fontParams(46, new Color(0.12f, 0.08f, 0.08f, 1f), 2));
        subtitleFont = generator.generateFont(fontParams(20, new Color(0.10f, 0.08f, 0.08f, 1f), 1));
        bodyFont = generator.generateFont(fontParams(18, new Color(0.08f, 0.08f, 0.08f, 1f), 1));

        generator.dispose();
    }

    private FreeTypeFontGenerator.FreeTypeFontParameter fontParams(int size, Color borderColor, int borderWidth) {
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = size;
        params.color = Color.WHITE;
        params.borderColor = borderColor;
        params.borderWidth = borderWidth;
        params.shadowColor = new Color(0f, 0f, 0f, 0.35f);
        params.shadowOffsetX = 2;
        params.shadowOffsetY = 2;
        return params;
    }

    private void drawCenteredText(BitmapFont drawFont, String text, float centerX, float y, Color color) {
        layout.setText(drawFont, text);
        drawFont.setColor(color);
        drawFont.draw(batch, text, centerX - layout.width / 2f, y);
    }

    private void drawLeftText(BitmapFont drawFont, String text, float x, float y, Color color) {
        drawFont.setColor(color);
        drawFont.draw(batch, text, x, y);
    }

    private Texture createPixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void disposeFonts() {
        if (titleFont != null) {
            titleFont.dispose();
            titleFont = null;
        }
        if (subtitleFont != null) {
            subtitleFont.dispose();
            subtitleFont = null;
        }
        if (bodyFont != null) {
            bodyFont.dispose();
            bodyFont = null;
        }
    }
}

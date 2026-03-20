package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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

public class MainMenuScene extends BaseScene {
    private static final float BUTTON_WIDTH = 220f;
    private static final float BUTTON_HEIGHT = 54f;
    private static final float MISSION_CARD_HEIGHT = 150f;
    private static final float MISSION_CARD_MIN_Y = 120f;
    private static final float MISSION_CARD_Y_RATIO = 0.27f;

    private final SceneManager sceneManager;
    private final IOManager ioManager;
    private final AudioManager audioManager;
    private final GlyphLayout layout = new GlyphLayout();

    private Skin skin;
    private Texture backgroundTexture;
    private Texture pixelTexture;
    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private BitmapFont bodyFont;
    private TextButton startButton;

    public MainMenuScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager, IOManager ioManager,
            AudioManager audioManager) {
        super(batch);
        this.sceneManager = sceneManager;
        this.ioManager = ioManager;
        this.audioManager = audioManager;
    }

    @Override
    protected void onShow() {
        super.onShow();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        pixelTexture = createPixelTexture();
        createFonts();
        createMenuButtonStyle();
        createButtons();
        audioManager.playMusic("title.mp3", true);

        ioManager.registerEvent(GameEvents.START_GAME, () -> {
            Gdx.app.log("Game", "Starting game - Test Scene at Level 1");
            BaseScene scene = sceneManager.getScene(SceneType.TEST_SCENE.name());
            if (scene instanceof TestScene) {
                ((TestScene) scene).setLevel(1);
            }
            sceneManager.setScene(SceneType.TEST_SCENE.name());
        });
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render(float delta) {
        clearScreen(0.12f, 0.11f, 0.18f, 1f);
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        drawBackdrop();
        batch.end();
        drawStageAndUI(delta);
    }

    @Override
    protected void renderUI() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float centerX = screenWidth / 2f;

        drawCenteredText(titleFont, "MAIN MENU", centerX, screenHeight - 64f, new Color(0.98f, 0.94f, 0.88f, 1f));
        drawCenteredText(subtitleFont, "ESCAPE BEFORE THE FIRE SPREADS", centerX, screenHeight - 112f,
                new Color(1f, 0.78f, 0.42f, 1f));

        float cardWidth = getMissionCardWidth(screenWidth);
        float cardX = (screenWidth - cardWidth) / 2f;
        float cardY = getMissionCardY(screenHeight);
        float lineY = cardY + MISSION_CARD_HEIGHT - 18f;
        float lineGap = 28f;

        drawLeftText(bodyFont, "Mission Brief", cardX + 18f, lineY, new Color(1f, 0.82f, 0.48f, 1f));
        drawLeftText(bodyFont, "The building is on fire. Your only goal is to get out alive.",
                cardX + 18f, lineY - lineGap, Color.WHITE);
        drawLeftText(bodyFont, "Use A / D to move, W to jump, and ESC to pause.", cardX + 18f,
                lineY - lineGap * 2f, new Color(0.88f, 0.91f, 0.96f, 1f));
        drawLeftText(bodyFont, "Grab the wet towel and mask - they'll buy you more time.", cardX + 18f,
                lineY - lineGap * 3f, new Color(0.88f, 0.91f, 0.96f, 1f));

        drawCenteredText(bodyFont, "Find the exit. Don't stop moving.", centerX, 30f,
                new Color(1f, 0.72f, 0.56f, 1f));
    }

    @Override
    protected void onResize(int width, int height) {
        layoutButtons(width, height);
    }

    @Override
    protected void onHide() {
        ioManager.clearEvent(GameEvents.START_GAME);
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
        disposeFonts();

        Stage stage = getStage();
        if (stage != null) {
            stage.clear();
        }
    }

    @Override
    public void dispose() {
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (pixelTexture != null) {
            pixelTexture.dispose();
            pixelTexture = null;
        }
        disposeFonts();
        super.dispose();
    }

    private void createButtons() {
        startButton = new TextButton("Start Rescue", skin, "menu-primary");
        startButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        layoutButtons(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        startButton.addListener(new ClickListener() {
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

        getStage().addActor(startButton);
    }

    private void layoutButtons(int width, int height) {
        if (startButton == null) {
            return;
        }
        startButton.setPosition((width - BUTTON_WIDTH) / 2f, height * 0.125f);
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

    private void drawBackdrop() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        if (backgroundTexture != null) {
            batch.setColor(Color.WHITE);
            batch.draw(backgroundTexture, 0f, 0f, screenWidth, screenHeight);
        }
        if (pixelTexture == null) {
            return;
        }

        float contentWidth = Math.min(500f, screenWidth - 80f);
        float contentX = (screenWidth - contentWidth) / 2f;
        float infoWidth = getMissionCardWidth(screenWidth);
        float infoX = (screenWidth - infoWidth) / 2f;
        float infoY = getMissionCardY(screenHeight);

        drawRect(0f, 0f, screenWidth, screenHeight, new Color(0.05f, 0.04f, 0.08f, 0.48f));
        drawRect(contentX, Math.max(screenHeight - 148f, 24f), contentWidth, 92f, new Color(0.07f, 0.08f, 0.11f, 0.82f));
        drawRect(infoX, infoY, infoWidth, MISSION_CARD_HEIGHT, new Color(0.08f, 0.09f, 0.12f, 0.82f));
        drawRect(infoX, infoY + MISSION_CARD_HEIGHT, infoWidth, 3f, new Color(1f, 0.58f, 0.22f, 0.88f));
        drawRect((screenWidth - 320f) / 2f, Math.max(screenHeight * 0.11f, 48f), 320f, 34f, new Color(0.08f, 0.08f, 0.10f, 0.62f));
        batch.setColor(Color.WHITE);
    }

    private float getMissionCardWidth(float screenWidth) {
        return Math.min(456f, screenWidth - 120f);
    }

    private float getMissionCardY(float screenHeight) {
        return Math.max(MISSION_CARD_MIN_Y, screenHeight * MISSION_CARD_Y_RATIO);
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

    private void drawRect(float x, float y, float width, float height, Color color) {
        batch.setColor(color);
        batch.draw(pixelTexture, x, y, width, height);
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

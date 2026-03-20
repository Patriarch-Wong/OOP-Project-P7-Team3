package io.github.team3engine.game.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import io.github.team3engine.engine.scene.BaseScene;
import io.github.team3engine.engine.scene.SceneManager;
import io.github.team3engine.game.ui.SceneButtonFactory;

public class GameOverScene extends BaseScene {
    private final SceneManager sceneManager;
    private final BitmapFont font;
    private final GlyphLayout titleLayout = new GlyphLayout();

    private String retrySceneId;
    private int retryLevel = 1;
    private Skin skin;
    private TextButton retryButton;
    private TextButton menuButton;

    public GameOverScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager,
                         String defaultRetrySceneId) {
        super(batch);
        this.font = sharedFont;
        this.sceneManager = sceneManager;
        this.retrySceneId = defaultRetrySceneId;
    }

    public void setRetryScene(String retrySceneId) {
        if (retrySceneId != null) this.retrySceneId = retrySceneId;
    }

    public void setRetryLevel(int level) {
        this.retryLevel = level;
    }

    @Override
    protected void onShow() {
        super.onShow();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

<<<<<<< HEAD
        float centerX = screenWidth  / 2f;
        float centerY = screenHeight / 2f;
        float gap     = 20f;
        float totalW  = SceneButtonFactory.BUTTON_WIDTH * 2 + gap;
        float startX  = centerX - totalW / 2f;
        float btnY    = centerY - 100f;

        TextButton retryButton = SceneButtonFactory.create("Retry", skin, () -> {
            BaseScene scene = sceneManager.getScene(retrySceneId);
            if (scene instanceof TestScene) {
                ((TestScene) scene).setLevel(retryLevel);
            }
            sceneManager.setScene(retrySceneId);
        });
        retryButton.setPosition(startX, btnY);
=======
        retryButton = SceneButtonFactory.create("Retry", skin,
                () -> sceneManager.setScene(retrySceneId));
>>>>>>> a70e28e5d09ddb96a0cdfd6b0026c8df43756d93

        menuButton = SceneButtonFactory.create("Main Menu", skin,
                () -> sceneManager.setScene(SceneType.MAIN_MENU_SCENE.name()));
        layoutButtons(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        getStage().addActor(retryButton);
        getStage().addActor(menuButton);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void render(float delta) {
        clearScreen(0.2f, 0f, 0f, 1f);
        drawStageAndUI(delta);
    }

    @Override
    protected void onResize(int width, int height) {
        layoutButtons(width, height);
    }

    @Override
    protected void renderUI() {
        font.setColor(Color.RED);
        String title = "GAME OVER";
        titleLayout.setText(font, title);
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float titleX = (screenWidth - titleLayout.width) / 2f;
        font.draw(batch, title, titleX, screenHeight / 2f + 60f);
        font.setColor(Color.WHITE); // reset after use
    }

    private void layoutButtons(int width, int height) {
        if (retryButton == null || menuButton == null) {
            return;
        }
        float centerX = width / 2f;
        float centerY = height / 2f;
        float gap = 20f;
        float totalW = SceneButtonFactory.BUTTON_WIDTH * 2 + gap;
        float startX = centerX - totalW / 2f;
        float btnY = centerY - 100f;

        retryButton.setPosition(startX, btnY);
        menuButton.setPosition(startX + SceneButtonFactory.BUTTON_WIDTH + gap, btnY);
    }

    @Override
    public void onHide() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (skin != null) { skin.dispose(); skin = null; }
        Stage s = getStage();
        if (s != null) s.clear();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

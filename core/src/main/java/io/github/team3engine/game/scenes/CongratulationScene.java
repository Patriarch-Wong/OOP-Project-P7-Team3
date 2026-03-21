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
import io.github.team3engine.game.score.ScoreManager;
import io.github.team3engine.game.ui.SceneButtonFactory;

public class CongratulationScene extends BaseScene {
    private final SceneManager sceneManager;
    private final BitmapFont font;
    private final GlyphLayout titleLayout = new GlyphLayout();
    private final ScoreManager scoreManager;

    private Skin skin;
    private TextButton menuButton;

    public CongratulationScene(SpriteBatch batch, BitmapFont sharedFont, SceneManager sceneManager,
                               int screenWidth, int screenHeight, String defaultRetrySceneId,
                               ScoreManager scoreManager) {
        super(batch);
        this.font = sharedFont;
        this.sceneManager = sceneManager;
        this.scoreManager = scoreManager;
    }

    @Override
    protected void onShow() {
        super.onShow();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        menuButton = SceneButtonFactory.create("Main Menu", skin,
                () -> {
                    scoreManager.resetScore();
                    sceneManager.setScene(SceneType.MAIN_MENU_SCENE.name());
                });

        getStage().addActor(menuButton);
        layoutButtons(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        font.setColor(Color.GREEN);
        String title = "Congratulations!";
        titleLayout.setText(font, title);
        float titleX = (centerX - titleLayout.width / 2f);
        font.draw(batch, title, titleX, centerY + 120f);

        font.setColor(Color.WHITE);
        String finalLine = "Final Score:  " + scoreManager.getFinalScore();
        GlyphLayout scoreLayout = new GlyphLayout();
        scoreLayout.setText(font, finalLine);
        font.draw(batch, finalLine, centerX - scoreLayout.width / 2f, centerY + 75f);

        String highLine = "High Score:   " + scoreManager.getHighScore();
        GlyphLayout highLayout = new GlyphLayout();
        highLayout.setText(font, highLine);
        font.draw(batch, highLine, centerX - highLayout.width / 2f, centerY + 50f);
    }

    @Override
    protected void onHide() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        if (skin != null) { skin.dispose(); skin = null; }
        menuButton = null;
        Stage s = getStage();
        if (s != null) s.clear();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private void layoutButtons(int width, int height) {
        if (menuButton == null) {
            return;
        }
        float centerX = width / 2f;
        float btnY = height / 2f - 100f;
        float btnX = centerX - SceneButtonFactory.BUTTON_WIDTH / 2f;

        menuButton.setPosition(btnX, btnY);
    }
}

package io.github.team3engine.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.team3engine.engine.interfaces.Updatable;
import io.github.team3engine.engine.interfaces.VolumeControl;

public class UIManager implements Updatable {
    private Stage stage;
    private Skin skin;
    private Table rootTable;
    private Window pauseWindow;
    private VolumeControl volumeControl;

    public UIManager(VolumeControl volumeControl) {
        this.volumeControl = volumeControl;
        this.stage = new Stage(new ScreenViewport());

        // Setup Skin with the Font Fix
        this.skin = new Skin();
        this.skin.add("default", new BitmapFont());
        this.skin.addRegions(new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        this.skin.load(Gdx.files.internal("ui/uiskin.json"));

        createPauseMenu();
    }

    private void createPauseMenu() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // The "Black Box" Window
        pauseWindow = new Window("SETTINGS", skin);
        pauseWindow.setMovable(false);
        pauseWindow.pad(20);

        // Add Volume Sliders
        addVolumeRow(pauseWindow, "Master", "master");
        addVolumeRow(pauseWindow, "Music", "music");
        addVolumeRow(pauseWindow, "SFX", "sfx");

        pauseWindow.add(new Label("Press ESC to Resume", skin)).colspan(2).padTop(20);

        rootTable.add(pauseWindow).center();
        rootTable.setVisible(false); // Hidden by default
    }

    private void addVolumeRow(Window window, String labelName, final String type) {
        window.add(new Label(labelName, skin)).left().pad(10);
        final Slider slider = new Slider(0, 1, 0.1f, false, skin);

        // Set initial value from volume control
        if (type.equals("master")) slider.setValue(volumeControl.getMasterVolume());
        else if (type.equals("music")) slider.setValue(volumeControl.getMusicVolume());
        else slider.setValue(volumeControl.getSFXVolume());

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (type.equals("master")) volumeControl.setMasterVolume(slider.getValue());
                else if (type.equals("music")) volumeControl.setMusicVolume(slider.getValue());
                else volumeControl.setSFXVolume(slider.getValue());
            }
        });
        window.add(slider).width(200).row();
    }

    public void toggleMenu(boolean visible) {
        rootTable.setVisible(visible);
        if (visible) {
            Gdx.input.setInputProcessor(stage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void update(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}

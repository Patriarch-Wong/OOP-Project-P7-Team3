package io.github.team3engine.game.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import io.github.team3engine.engine.io.InputListener;

public class PlayerInput extends InputListener {
    private boolean leftHeld, rightHeld, upHeld, downHeld, spaceHeld;

    public PlayerInput() {
        setActive(true);
    }

    @Override
    public boolean onKey(int keycode, boolean pressed) {
        if (!isActive()) {
            clearHeldKeys();
            return false;
        }

        refreshHeldKeys();

        if (leftHeld || rightHeld || upHeld || downHeld || spaceHeld) return true;
        return false;
    }

    @Override
    public boolean onClick(int x, int y, int button) {
        return false;
    }

    @Override
    public void update(float deltaTime) {
        if (!isActive()) {
            clearHeldKeys();
            return;
        }

        // Poll the actual keyboard state every frame so pause/unpause cannot
        // leave cached key state behind.
        refreshHeldKeys();
    }

    public void reset() {
        clearHeldKeys();
    }

    public boolean isLeftHeld() { return leftHeld; }
    public boolean isRightHeld() { return rightHeld; }
    public boolean isUpHeld() { return upHeld; }
    public boolean isDownHeld() { return downHeld; }
    public boolean isSpaceHeld() { return spaceHeld; }

    private void refreshHeldKeys() {
        leftHeld = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
        rightHeld = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
        upHeld = Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP);
        downHeld = Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN);
        spaceHeld = Gdx.input.isKeyPressed(Keys.SPACE);
    }

    private void clearHeldKeys() {
        leftHeld = false;
        rightHeld = false;
        upHeld = false;
        downHeld = false;
        spaceHeld = false;
    }
}

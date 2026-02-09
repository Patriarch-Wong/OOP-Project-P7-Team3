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
            leftHeld = rightHeld = upHeld = downHeld = spaceHeld = false;
            return false;
        }

        leftHeld  = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
        rightHeld = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
        upHeld    = Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP);
        downHeld  = Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN);
        spaceHeld = Gdx.input.isKeyPressed(Keys.SPACE);

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
            leftHeld = rightHeld = upHeld = downHeld = spaceHeld = false;
            return;
        }
    }

    public void reset() {
        leftHeld = rightHeld = upHeld = downHeld = false;
    }



    public boolean isLeftHeld() { return leftHeld; }
    public boolean isRightHeld() { return rightHeld; }
    public boolean isUpHeld() { return upHeld; }
    public boolean isDownHeld() { return downHeld; }
    public boolean isSpaceHeld() { return spaceHeld; }
}
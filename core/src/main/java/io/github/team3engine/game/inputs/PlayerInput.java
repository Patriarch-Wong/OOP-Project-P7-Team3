package io.github.team3engine.game.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import io.github.team3engine.engine.io.InputListener;

public class PlayerInput extends InputListener {
    private boolean leftHeld, rightHeld, upHeld, downHeld;

    public PlayerInput() {
        setActive(true);
    }

    @Override
    public boolean onKey(int keycode, boolean pressed) {
        leftHeld  = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
        rightHeld = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
        upHeld    = Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP);
        downHeld  = Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN);

        if (leftHeld || rightHeld || upHeld || downHeld) return true;
        return false;
    }

    @Override
    public boolean onClick(int x, int y, int button) {
        return false;
    }

    @Override
    public void update(float deltaTime) {
        
    }

    public void reset() {
        leftHeld = rightHeld = upHeld = downHeld = false;
    }

    public boolean isLeftHeld() { return leftHeld; }
    public boolean isRightHeld() { return rightHeld; }
    public boolean isUpHeld() { return upHeld; }
    public boolean isDownHeld() { return downHeld; }
}

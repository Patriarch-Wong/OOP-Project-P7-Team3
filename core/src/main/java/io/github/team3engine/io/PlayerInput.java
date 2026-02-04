package io.github.team3engine.io;

import com.badlogic.gdx.Input.Keys;

import io.github.team3engine.TextureObject;
import io.github.team3engine.entity.MovementInput;

public class PlayerInput extends InputListener {
    private boolean leftHeld;
    private boolean rightHeld;
    private TextureObject bucket;

    public PlayerInput(TextureObject bucket) {
    this.bucket = bucket;
    setActive(true); // enable by default
    }

    // called on key input
    @Override
    public boolean onKey(int keycode, boolean pressed) {
    if (keycode == Keys.A) {
    leftHeld = pressed;
    return true;
    }
    if (keycode == Keys.D) {
    rightHeld = pressed;
    return true;
    }
    return false; // input not consumed
    }
    // public PlayerInput(MovementInput input) {
    //     this.input = input;
    //     setActive(true);
    // }

    // @Override
    // public boolean onKey(int keycode, boolean pressed) {
    //     if (keycode == Keys.A)
    //         leftHeld = pressed;
    //     if (keycode == Keys.D)
    //         rightHeld = pressed;

    //     input.movementAxis = 0f;
    //     if (leftHeld)
    //         input.movementAxis -= 1f;
    //     if (rightHeld)
    //         input.movementAxis += 1f;
    //     if (keycode == Keys.SPACE && pressed)
    //         input.jump = true;

    //     return true;
    // }

    // called on mouse input
    @Override
    public boolean onClick(int x, int y, int button) {
        // button is left click, right click, middle click etc
        // x and y is the coordinates
        return false;
    }

    @Override
    public void update(float deltaTime) {
        // if (leftHeld) {
        // bucket.setX(bucket.getX() - deltaTime * bucket.getSpeed());
        // }

        // if (rightHeld) {
        // bucket.setX(bucket.getX() + deltaTime * bucket.getSpeed());
        // }
    }

    public boolean isLeftHeld() {
        return leftHeld;
    }

    public boolean isRightHeld() {
        return rightHeld;
    }
}

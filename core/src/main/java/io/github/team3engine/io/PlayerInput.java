package io.github.team3engine.io;

import com.badlogic.gdx.Input.Keys;

import io.github.team3engine.TextureObject;

public class PlayerInput extends InputListener {
    private TextureObject bucket;
    private boolean leftHeld;
    private boolean rightHeld;

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
        return false; //input not consumed
    }

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
        //     bucket.setX(bucket.getX() - deltaTime * bucket.getSpeed());
        // }

        // if (rightHeld) {
        //     bucket.setX(bucket.getX() + deltaTime * bucket.getSpeed());
        // }
    }

    public boolean isLeftHeld() {
        return leftHeld;
    }

    
    public boolean isRightHeld() {
        return rightHeld;
    }
}

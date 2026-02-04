package io.github.team3engine.io;

import com.badlogic.gdx.Input.Keys;

import io.github.team3engine.entity.Circle;

public class PlayerInput extends InputListener {
    private Circle player;
    private boolean leftHeld;
    private boolean rightHeld;
    private boolean upHeld;
    private boolean downHeld;

    public PlayerInput(Circle player) {
        this.player = player;
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
        if (keycode == Keys.W) {
            upHeld = pressed;
            return true;
        }
        if (keycode == Keys.S) {
            downHeld = pressed;
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

    public boolean isUpHeld() {
        return upHeld;
    }

    public boolean isDownHeld() {
        return downHeld;
    }
}

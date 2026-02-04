package io.github.team3engine.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

<<<<<<< HEAD
<<<<<<< HEAD
import io.github.team3engine.TextureObject;
import io.github.team3engine.entity.MovementInput;

public class PlayerInput extends InputListener {
    private boolean leftHeld;
    private boolean rightHeld;
    private TextureObject bucket;

    public PlayerInput(TextureObject bucket) {
    this.bucket = bucket;
    setActive(true); // enable by default
=======
import io.github.team3engine.entity.Circle;

=======
>>>>>>> 75eee3648a2037e0aeadec5daee96721855f8e38
public class PlayerInput extends InputListener {
    private boolean leftHeld, rightHeld, upHeld, downHeld;

<<<<<<< HEAD
    public PlayerInput(Circle player) {
        this.player = player;
        setActive(true); // enable by default
>>>>>>> d71daa833706035f1adfd90aa8e7ac0a05932a3c
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
=======
    // This must be empty to match "new PlayerInput()" in Main.java
    public PlayerInput() {
        setActive(true);
>>>>>>> 75eee3648a2037e0aeadec5daee96721855f8e38
    }

    @Override
    public void update(float deltaTime) {
<<<<<<< HEAD
        // if (leftHeld) {
        // bucket.setX(bucket.getX() - deltaTime * bucket.getSpeed());
        // }

        // if (rightHeld) {
        // bucket.setX(bucket.getX() + deltaTime * bucket.getSpeed());
        // }
=======
        leftHeld  = Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT);
        rightHeld = Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT);
        upHeld    = Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP);
        downHeld  = Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN);
>>>>>>> 75eee3648a2037e0aeadec5daee96721855f8e38
    }

    public void reset() {
        leftHeld = rightHeld = upHeld = downHeld = false;
    }

<<<<<<< HEAD
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
=======
    public boolean isLeftHeld() { return leftHeld; }
    public boolean isRightHeld() { return rightHeld; }
    public boolean isUpHeld() { return upHeld; }
    public boolean isDownHeld() { return downHeld; }
    
    @Override public boolean onKey(int keycode, boolean pressed) { return false; }
}
>>>>>>> 75eee3648a2037e0aeadec5daee96721855f8e38

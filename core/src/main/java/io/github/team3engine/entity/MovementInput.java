package io.github.team3engine.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class MovementInput {
	//assume data is already valid when using these values
    public float movementAxis; //-1 is to go left direction, 1 is to go right direction
    public boolean jump;

    /**
     * Polls the keyboard to update the intent state.
     */
    public void update() {
        movementAxis = 0;
        
        // Horizontal logic
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movementAxis -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movementAxis += 1;
        }

        // Jump logic (isKeyJustPressed prevents infinite jumping)
        jump = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
    }
}
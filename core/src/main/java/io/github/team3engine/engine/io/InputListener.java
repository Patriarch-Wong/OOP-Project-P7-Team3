package io.github.team3engine.engine.io;

import io.github.team3engine.engine.interfaces.Updatable;

public abstract class InputListener implements Updatable {
    private boolean isActive;

    // called on key input
    public abstract boolean onKey(int keycode, boolean pressed);

    //called on mouse input
    public abstract boolean onClick(int x, int y, int button);

    public abstract void update(float deltaTime);

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}

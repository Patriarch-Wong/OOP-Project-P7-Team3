package io.github.team3engine.engine.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.InputProcessor;

import io.github.team3engine.engine.interfaces.Updatable;

public class IOManager implements InputProcessor, Updatable {
    private List<InputListener> inputListeners = new ArrayList<>();
    private Map<String, List<Runnable>> eventCallbacks = new HashMap<String, List<Runnable>>();
    private boolean isActive; // useed when not taking input(eg paused)

    public IOManager() {
        setActive(true); // enable by default
    }

    public void addInputListener(InputListener l) {
        inputListeners.add(l);
    }

    public void removeInputListener(InputListener l) {
        inputListeners.remove(l);
    }

    /**
     * Clear all listeners and event callbacks to break references and avoid leaks
     * on shutdown.
     */
    public void dispose() {
        inputListeners.clear();
        eventCallbacks.clear();
    }

    // register callbacks for each event
    public void registerEvent(String eventName, Runnable callback) {
        eventCallbacks
                .computeIfAbsent(eventName, k -> new ArrayList<>())
                .add(callback);
    }

    // clear all callbacks for a specific event
    public void clearEvent(String eventName) {
        eventCallbacks.remove(eventName);
    }

    public void clearAllEvents() {
        eventCallbacks.clear();
    }

    // when an event happens, run all the callbacks linked to that event
    public void broadcast(String eventName) {
        List<Runnable> callbacks = eventCallbacks.get(eventName);
        if (callbacks == null)
            return;

        for (Runnable r : callbacks) {
            r.run();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        for (InputListener listener : inputListeners) {
            listener.setActive(active);
        }
        isActive = active;
    }

    public void toggleListener(InputListener listener) {
        if (listener != null) {
            listener.setActive(!listener.isActive());
        }
    }

    @Override
    public void update(float deltaTime) {
        for (InputListener listener : inputListeners) {
            listener.update(deltaTime);
        }
    }

    /*
     * ============================
     * LibGDX InputProcessor
     * ============================
     */

    @Override
    public boolean keyDown(int keycode) {
        if (!isActive) {
            System.out.println("IOManager inactive, ignoring keyDown for keycode: " + keycode);
            return false;

        }
        for (InputListener listener : inputListeners) {
            if (listener.isActive() && listener.onKey(keycode, true)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!isActive)
            return false;

        for (InputListener listener : inputListeners) {
            if (listener.isActive() && listener.onKey(keycode, false)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (!isActive)
            return false;

        for (InputListener listener : inputListeners) {
            if (listener.isActive() && listener.onClick(x, y, button)) {
                return true;
            }
        }
        return false;
    }

    /*
     * ============================
     * Unused InputProcessor methods
     * ============================
     */

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean touchCancelled(int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
}

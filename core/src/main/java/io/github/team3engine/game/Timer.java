package io.github.team3engine.game;
// Timer.java
import java.util.ArrayList;
import java.util.List;

public class Timer {

    private float timeRemaining;
    private final float totalTime;
    private boolean expired;
    private boolean warningTriggered;

    private static final float WARNING_THRESHOLD = 30.0f;

    private final List<TimerListener> listeners = new ArrayList<>();

    // Called by the level scene with how many seconds this level allows
    public Timer(float totalSeconds) {
        this.totalTime    = totalSeconds;
        this.timeRemaining = totalSeconds;
        this.expired       = false;
        this.warningTriggered = false;
    }

    // Called every game tick from the level's update() loop
    public void update(float deltaTime) {
        if (expired) return;

        timeRemaining -= deltaTime;

        // Trigger warning once when crossing 30s threshold
        if (!warningTriggered && timeRemaining <= WARNING_THRESHOLD) {
            warningTriggered = true;
            for (TimerListener l : listeners) l.onTimerWarning(timeRemaining);
        }

        if (timeRemaining <= 0) {
            timeRemaining = 0;
            expired = true;
            for (TimerListener l : listeners) l.onTimerExpired();
        }
    }

    // Used by: Mask pickup, Call 995 interaction
    public void extend(float seconds) {
        timeRemaining += seconds;
        // If extension pushes us back above warning threshold, re-arm the warning
        if (timeRemaining > WARNING_THRESHOLD) {
            warningTriggered = false;
        }
    }

    // Resets to full time — e.g. when the player retries the level
    public void reset() {
        timeRemaining    = totalTime;
        expired          = false;
        warningTriggered = false;
    }

    // HUD display — formats as "1:23"
    public String getFormattedTime() {
        int minutes = (int) timeRemaining / 60;
        int seconds = (int) timeRemaining % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public boolean isExpired()          { return expired; }
    public float   getTimeRemaining()   { return timeRemaining; }
    public float   getTotalTime()       { return totalTime; }

    public void addListener(TimerListener listener)    { listeners.add(listener); }
    public void removeListener(TimerListener listener) { listeners.remove(listener); }
}

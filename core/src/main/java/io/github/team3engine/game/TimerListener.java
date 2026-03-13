package io.github.team3engine.game;

public interface TimerListener {
    void onTimerWarning(float timeRemaining); // fires when <= 30s left
    void onTimerExpired();                    // fires when timer hits 0
}


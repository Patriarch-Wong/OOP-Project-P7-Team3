package io.github.team3engine.game.scene;

public class Timer {

    private float timeRemaining;
    private float maxTime;
    private boolean isRunning;

    public Timer(float maxTime) {
        this.maxTime = maxTime;
        this.timeRemaining = maxTime;
        this.isRunning = false;
    }

    // Call this every frame from the scene's update()
    public void update(float deltaTime) {
        if (!isRunning) return;
        timeRemaining -= deltaTime;
        if (timeRemaining < 0) {
            timeRemaining = 0;
            isRunning = false;
        }
    }

    public void start() {
        isRunning = true;
    }

    public void stop() {
        isRunning = false;
    }

    public void reset() {
        timeRemaining = maxTime;
        isRunning = false;
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    public float getMaxTime() {
        return maxTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isFinished() {
        return timeRemaining <= 0;
    }

    public void addTime(float seconds) {
        timeRemaining += seconds;
    }
}

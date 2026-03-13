package io.github.team3engine.engine.interfaces;

public interface Scoreable {
    int getScore();
    void addScore(int points);
    void reset();
}

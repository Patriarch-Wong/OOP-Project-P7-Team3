package io.github.team3engine.game.interfaces;

public interface Scoreable {
    int getScore();
    void addScore(int points);
    void reset();
}

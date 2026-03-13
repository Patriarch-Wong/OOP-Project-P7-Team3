package io.github.team3engine.engine.scoring;

import java.util.ArrayList;
import java.util.List;

import io.github.team3engine.engine.interfaces.Scoreable;
import io.github.team3engine.engine.interfaces.ScoreRule;

public class ScoreManager implements Scoreable {

    // Singleton
    private static ScoreManager instance;

    private int score;
    private int highScore;
    private float multiplier;
    private List<ScoreRule> scoreRules;

    private ScoreManager() {
        score = 0;
        highScore = 0;
        multiplier = 1.0f;
        scoreRules = new ArrayList<>();
    }

    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    // --- Scoreable interface ---

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void addScore(int points) {
        score += (int)(points * multiplier);
        if (score > highScore) {
            highScore = score;
        }
    }

    @Override
    public void reset() {
        score = 0;
        multiplier = 1.0f;
        scoreRules.clear();
    }

    // --- Rule management ---

    public void addRule(ScoreRule rule) {
        scoreRules.add(rule);
    }

    public void removeRule(ScoreRule rule) {
        scoreRules.remove(rule);
    }

    // Run all rules against a context and add resulting points
    public void applyRules(ScoreContext context) {
        for (ScoreRule rule : scoreRules) {
            int points = rule.evaluate(context);
            addScore(points);
        }
    }

    // --- Getters/Setters ---

    public int getFinalScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }
}

package io.github.team3engine.game.score;

import java.util.ArrayList;
import java.util.List;

import io.github.team3engine.game.interfaces.Scoreable;
import io.github.team3engine.game.interfaces.ScoreRule;

public class ScoreManager implements Scoreable {

    private int score;
    private int highScore;
    private float multiplier;
    private List<ScoreRule> scoreRules;

    public ScoreManager() {
        score = 0;
        highScore = 0;
        multiplier = 1.0f;
        scoreRules = new ArrayList<>();
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

    public void resetScore() {
        score = 0;
        multiplier = 1.0f;
    }

    // --- Rule management ---

    public void addRule(ScoreRule rule) {
        for (ScoreRule existingRule : scoreRules) {
            if (existingRule.getClass().equals(rule.getClass())) {
                return;
            }
        }
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

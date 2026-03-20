package io.github.team3engine.game.score;

import io.github.team3engine.game.interfaces.ScoreRule;
import io.github.team3engine.game.score.ScoreContext;

// ─────────────────────────────────────
// More time remaining = more bonus points
// context.put("timeRemaining", getTimer().getTimeRemaining())
// ─────────────────────────────────────
public class TimeBonusRule implements ScoreRule {
    private static final int POINTS_PER_SECOND = 10;

    @Override
    public int evaluate(ScoreContext context) {
        float timeRemaining = context.getFloat("timeRemaining", 0f);
        return (int)(timeRemaining * POINTS_PER_SECOND);
    }
}

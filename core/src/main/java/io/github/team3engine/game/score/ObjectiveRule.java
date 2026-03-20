package io.github.team3engine.game.score;

import io.github.team3engine.game.interfaces.ScoreRule;
import io.github.team3engine.game.score.ScoreContext;

public class ObjectiveRule implements ScoreRule {
    private static final int BASE_POINTS = 1000;

    @Override
    public int evaluate(ScoreContext context) {
        boolean complete = context.getBoolean("objectiveComplete", false);
        return complete ? BASE_POINTS : 0;
    }
}

package io.github.team3engine.game.score;

import io.github.team3engine.engine.interfaces.ScoreRule;
import io.github.team3engine.engine.scoring.ScoreContext;

public class ObjectiveRule implements ScoreRule {
    private static final int BASE_POINTS = 1000;

    @Override
    public int evaluate(ScoreContext context) {
        if (!context.has("objectiveComplete")) return 0;
        boolean complete = (boolean) context.get("objectiveComplete");
        return complete ? BASE_POINTS : 0;
    }
}

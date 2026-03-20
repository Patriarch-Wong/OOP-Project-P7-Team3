package io.github.team3engine.game.interfaces;

import io.github.team3engine.game.score.ScoreContext;

public interface ScoreRule {
    int evaluate(ScoreContext context);
}

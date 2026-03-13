package io.github.team3engine.engine.interfaces;

import io.github.team3engine.engine.scoring.ScoreContext;

public interface ScoreRule {
    int evaluate(ScoreContext context);
}

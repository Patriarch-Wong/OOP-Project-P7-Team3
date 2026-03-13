package io.github.team3engine.game.score;

import io.github.team3engine.engine.interfaces.ScoreRule;
import io.github.team3engine.engine.scoring.ScoreContext;

// ─────────────────────────────────────
// Base points for reaching the exit
// context.put("objectiveComplete", true)
// ─────────────────────────────────────
class ObjectiveRule implements ScoreRule {
    private static final int BASE_POINTS = 1000;

    @Override
    public int evaluate(ScoreContext context) {
        if (!context.has("objectiveComplete")) return 0;
        boolean complete = (boolean) context.get("objectiveComplete");
        return complete ? BASE_POINTS : 0;
    }
}

// ─────────────────────────────────────
// Bonus points per NPC rescued
// context.put("npcsRescued", 3)
// ─────────────────────────────────────
class NpcRescueRule implements ScoreRule {
    private static final int POINTS_PER_NPC = 300;

    @Override
    public int evaluate(ScoreContext context) {
        if (!context.has("npcsRescued")) return 0;
        int npcsRescued = (int) context.get("npcsRescued");
        return npcsRescued * POINTS_PER_NPC;
    }
}

// ─────────────────────────────────────
// Bonus points based on time remaining
// context.put("timeRemaining", 90.0f)
// ─────────────────────────────────────
class TimeBonusRule implements ScoreRule {
    private static final int POINTS_PER_SECOND = 10;

    @Override
    public int evaluate(ScoreContext context) {
        if (!context.has("timeRemaining")) return 0;
        float timeRemaining = (float) context.get("timeRemaining");
        return (int)(timeRemaining * POINTS_PER_SECOND);
    }
}

// ─────────────────────────────────────
// Bonus points if HP is high on escape
// context.put("hp", 85.0f)
// ─────────────────────────────────────
class HpBonusRule implements ScoreRule {
    private static final float HP_THRESHOLD = 80.0f;
    private static final int BONUS_POINTS = 500;

    @Override
    public int evaluate(ScoreContext context) {
        if (!context.has("hp")) return 0;
        float hp = (float) context.get("hp");
        return hp >= HP_THRESHOLD ? BONUS_POINTS : 0;
    }
}

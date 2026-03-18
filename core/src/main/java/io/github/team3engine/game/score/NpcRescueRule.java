package io.github.team3engine.game.score;

import io.github.team3engine.engine.interfaces.ScoreRule;
import io.github.team3engine.engine.scoring.ScoreContext;

// ─────────────────────────────────────
// Bonus points per NPC rescued
// context.put("npcsRescued", player.getRescuedCount())
// ─────────────────────────────────────
public class NpcRescueRule implements ScoreRule {
    private static final int POINTS_PER_NPC = 500;

    @Override
    public int evaluate(ScoreContext context) {
        int rescued = context.getInt("npcsRescued", 0);
        return rescued * POINTS_PER_NPC;
    }
}

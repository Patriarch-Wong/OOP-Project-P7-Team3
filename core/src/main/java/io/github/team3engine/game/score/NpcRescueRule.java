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
        if (!context.has("npcsRescued")) return 0;
        int rescued = (int) context.get("npcsRescued");
        return rescued * POINTS_PER_NPC;
    }
}

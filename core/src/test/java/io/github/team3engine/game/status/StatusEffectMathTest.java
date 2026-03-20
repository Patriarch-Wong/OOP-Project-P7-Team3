package io.github.team3engine.game.status;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.team3engine.engine.entity.Entity;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class StatusEffectMathTest {
    private StatusEffectManager manager;

    @Before
    public void setUp() {
        manager = new StatusEffectManager(new DummyEntity());
    }

    @Test
    public void damageReductionEffectsStackAndCoexistAcrossKeys() {
        manager.apply(new DamageReductionEffect("dr:mask", 0.5f, 10f, "Mask"));
        manager.apply(new DamageReductionEffect("dr:towel", 0.3f, 15f, "Towel"));

        List<DamageReductionEffect> reductions = manager.getAllEffects(DamageReductionEffect.class);
        assertEquals(2, reductions.size());
        assertEquals(35f, StatusEffectMath.applyDamageReductions(100f, reductions), 0.0001f);
    }

    @Test
    public void sameDamageReductionKeyRefreshesTheEffect() {
        manager.apply(new DamageReductionEffect("dr:mask", 0.5f, 10f, "Mask"));
        manager.apply(new DamageReductionEffect("dr:mask", 0.7f, 20f, "Mask"));

        List<DamageReductionEffect> reductions = manager.getAllEffects(DamageReductionEffect.class);
        assertEquals(1, reductions.size());
        assertEquals(0.3f, reductions.get(0).getDamageMultiplier(), 0.0001f);
    }

    @Test
    public void slowAggregationUsesStrongestMultiplierAndRemovalByKeyLeavesOthers() {
        manager.apply(new SlowEffect("slow:carry_npc", 0.3f, Float.MAX_VALUE));
        manager.apply(new SlowEffect("slow:ice", 0.1f, 10f));

        List<SlowEffect> slows = manager.getAllEffects(SlowEffect.class);
        assertEquals(2, slows.size());
        assertEquals(0.7f, StatusEffectMath.strongestSlowMultiplier(slows), 0.0001f);

        manager.removeByKey("slow:carry_npc");
        slows = manager.getAllEffects(SlowEffect.class);
        assertEquals(1, slows.size());
        assertEquals("slow:ice", slows.get(0).getEffectKey());
        assertEquals(0.9f, StatusEffectMath.strongestSlowMultiplier(slows), 0.0001f);
    }

    @Test
    public void invalidFactorsClampToSafeRange() {
        assertEquals(0f, new DamageReductionEffect("dr:clamp", 2f, 1f, "Clamp").getDamageMultiplier(), 0.0001f);
        assertEquals(1f, new SlowEffect("slow:clamp", -0.5f, 1f).getSpeedMultiplier(), 0.0001f);
    }

    private static final class DummyEntity extends Entity {
        private DummyEntity() {
            super("dummy");
        }

        @Override
        public void update(float dt) {}

        @Override
        public void render(SpriteBatch batch) {}

        @Override
        public void dispose() {}
    }
}

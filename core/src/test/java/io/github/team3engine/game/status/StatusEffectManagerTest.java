package io.github.team3engine.game.status;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.team3engine.engine.entity.Entity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class StatusEffectManagerTest {
    private StatusEffectManager manager;

    @Before
    public void setUp() {
        manager = new StatusEffectManager(new DummyEntity());
    }

    @Test
    public void sameKeyRefreshesInsteadOfStacking() {
        TrackingEffect first = new TrackingEffect("dr:mask", 10f);
        TrackingEffect replacement = new TrackingEffect("dr:mask", 20f);

        manager.apply(first);
        manager.apply(replacement);

        assertEquals(1, manager.getAll().size());
        assertEquals(1, first.applyCount);
        assertEquals(1, first.removeCount);
        assertEquals(1, replacement.applyCount);
        assertEquals(0, replacement.removeCount);
        assertSame(replacement, manager.getEffect("dr:mask"));
    }

    @Test
    public void differentKeysCoexist() {
        TrackingEffect alpha = new TrackingEffect("alpha", 10f);
        TrackingEffect beta = new TrackingEffect("beta", 10f);

        manager.apply(alpha);
        manager.apply(beta);

        assertEquals(2, manager.getAll().size());
        assertSame(alpha, manager.getEffect("alpha"));
        assertSame(beta, manager.getEffect("beta"));
    }

    @Test
    public void expiredEffectRemovesAndCallsOnRemove() {
        TrackingEffect shortLived = new TrackingEffect("short", 1f);
        manager.apply(shortLived);

        manager.update(0.5f);
        assertEquals(1, manager.getAll().size());
        assertEquals(0, shortLived.removeCount);

        manager.update(0.5f);
        assertEquals(0, manager.getAll().size());
        assertEquals(1, shortLived.removeCount);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exposedEffectListIsReadOnly() {
        manager.apply(new TrackingEffect("read-only", 1f));
        manager.getAll().add(new TrackingEffect("extra", 1f));
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

    private static final class TrackingEffect extends StatusEffect {
        private int applyCount;
        private int removeCount;

        private TrackingEffect(String effectKey, float duration) {
            super(effectKey, duration);
        }

        @Override
        public String getName() {
            return getEffectKey();
        }

        @Override
        public void onApply(Entity target) {
            applyCount++;
        }

        @Override
        public void onRemove(Entity target) {
            removeCount++;
        }
    }
}

package io.github.team3engine.engine.movement;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.IMovementInput;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MovementManagerTest {
    @Test
    public void speedMultiplierScalesHorizontalClamp() {
        MovementManager manager = new MovementManager();
        manager.setAcceleration(0f);
        manager.setDeceleration(0f);
        manager.setGravity(0f);
        manager.setJumpForce(0f);

        MovementState state = new MovementState();
        state.setVelocityX(500f);
        state.setSpeedMultiplier(0.5f);

        DummyEntity entity = new DummyEntity();
        manager.applyMovement(entity, state, new DummyInput(), 1f);

        assertEquals(150f, state.getVelocityX(), 0.0001f);
        assertEquals(150f, entity.getX(), 0.0001f);
    }

    @Test
    public void resetRestoresDefaultSpeedMultiplier() {
        MovementState state = new MovementState();
        state.setSpeedMultiplier(0.25f);
        state.reset();

        assertEquals(1f, state.getSpeedMultiplier(), 0.0001f);
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

    private static final class DummyInput implements IMovementInput {
        @Override
        public float getMovementAxis() {
            return 0f;
        }

        @Override
        public boolean isJump() {
            return false;
        }

        @Override
        public boolean isCrawl() {
            return false;
        }
    }
}

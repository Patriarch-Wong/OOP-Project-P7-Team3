package io.github.team3engine.engine.movement;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.team3engine.engine.entity.Entity;
import io.github.team3engine.engine.interfaces.IMovementInput;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MovementManagerTest {
    @Test
    public void speedMultiplierScalesHorizontalClampAndGravityApplies() {
        MovementManager manager = new MovementManager();
        MovementState state = new MovementState();
        state.setVelocityX(500f);
        state.setVelocityY(0f);
        state.setSpeedMultiplier(0.5f);

        MovementConfig config = new MovementConfig(300f, -600f, 0f, 0f, -10f);
        DummyEntity entity = new DummyEntity();
        manager.applyMovement(entity, state, config, new DummyInput(), 1f);

        assertEquals(150f, state.getVelocityX(), 0.0001f);
        assertEquals(-10f, state.getVelocityY(), 0.0001f);
        assertEquals(150f, entity.getX(), 0.0001f);
        assertEquals(-10f, entity.getY(), 0.0001f);
    }

    @Test
    public void gravityClampsToMaxFallSpeed() {
        MovementManager manager = new MovementManager();
        MovementState state = new MovementState();
        state.setVelocityY(-90f);

        MovementConfig config = new MovementConfig(300f, -100f, 0f, 0f, -20f);
        DummyEntity entity = new DummyEntity();
        manager.applyMovement(entity, state, config, new DummyInput(), 1f);

        assertEquals(-100f, state.getVelocityY(), 0.0001f);
        assertEquals(-100f, entity.getY(), 0.0001f);
    }

    @Test
    public void resetRestoresDefaultMovementState() {
        MovementState state = new MovementState();
        state.setVelocityX(12f);
        state.setVelocityY(-8f);
        state.setSpeedMultiplier(0.25f);
        state.setMovementEnabled(false);

        state.reset();

        assertEquals(0f, state.getVelocityX(), 0.0001f);
        assertEquals(0f, state.getVelocityY(), 0.0001f);
        assertEquals(1f, state.getSpeedMultiplier(), 0.0001f);
        assertTrue(state.isMovementEnabled());
    }

    @Test
    public void helperMethodsRemainGeneric() {
        MovementManager manager = new MovementManager();
        MovementState state = new MovementState();

        state.setVelocityY(4f);
        assertTrue(manager.isMovingUpward(state));

        state.setVelocityX(6f);
        assertTrue(manager.hasHorizontalMotion(state));

        manager.cancelUpwardVelocity(state);
        assertEquals(0f, state.getVelocityY(), 0.0001f);

        manager.disableMovement(state);
        assertFalse(state.isMovementEnabled());
        assertEquals(0f, state.getVelocityX(), 0.0001f);

        manager.enableMovement(state);
        assertTrue(state.isMovementEnabled());
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
    }
}

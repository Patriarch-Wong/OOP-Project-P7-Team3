package io.github.team3engine.engine.collision;

import com.badlogic.gdx.utils.Array;
import io.github.team3engine.engine.interfaces.Collidable;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralizes collision response logic using the Mediator pattern.
 * Instead of entities checking instanceof in onCollision(), collision rules
 * are registered here as typed handlers. The mediator dispatches each
 * collision pair to matching handlers.
 *
 * This decouples entities from each other — they no longer need to know
 * what they collided with. All collision rules are visible in one place
 * (the scene that registers them).
 */
public class CollisionMediator {
    private final List<CollisionRule> rules = new ArrayList<>();

    /**
     * Register a typed collision handler. When a collision pair matches
     * (typeA, typeB) in either order, the handler is invoked with
     * correctly typed arguments.
     *
     * Type parameters are unbounded so handlers can match on any interface
     * (e.g. Damageable, Pickup) that a collidable entity implements.
     */
    public <A, B> void addRule(Class<A> typeA, Class<B> typeB,
                               CollisionHandler<A, B> handler) {
        rules.add(new CollisionRule(typeA, typeB, handler));
    }

    /**
     * Resolve all collision pairs through registered handlers.
     * Each pair is checked against rules in registration order;
     * all matching rules are applied.
     */
    public void resolve(Array<Collidable[]> pairs) {
        for (Collidable[] pair : pairs) {
            if (pair == null || pair.length < 2) continue;
            dispatchPair(pair[0], pair[1]);
        }
    }

    /** Clear all registered rules (e.g. on scene change). */
    public void clear() {
        rules.clear();
    }

    private void dispatchPair(Collidable a, Collidable b) {
        for (CollisionRule rule : rules) {
            rule.tryHandle(a, b);
        }
    }

    /**
     * Internal rule that stores type info and dispatches with safe casting.
     * The instanceof checks are centralized here, not scattered across entities.
     */
    private static class CollisionRule {
        private final Class<?> typeA;
        private final Class<?> typeB;
        private final CollisionHandler<Object, Object> handler;

        @SuppressWarnings("unchecked")
        <A, B> CollisionRule(Class<A> typeA, Class<B> typeB,
                             CollisionHandler<A, B> handler) {
            this.typeA = typeA;
            this.typeB = typeB;
            this.handler = (CollisionHandler<Object, Object>) handler;
        }

        /** Try both orderings of the pair. Returns true if handled. */
        boolean tryHandle(Object a, Object b) {
            if (typeA.isInstance(a) && typeB.isInstance(b)) {
                handler.handle(a, b);
                return true;
            }
            if (typeA.isInstance(b) && typeB.isInstance(a)) {
                handler.handle(b, a);
                return true;
            }
            return false;
        }
    }
}

package io.github.team3engine.collision;

import com.badlogic.gdx.utils.Array;
import io.github.team3engine.audio.AudioManager;
import io.github.team3engine.entity.Entity;
import io.github.team3engine.interfaces.Collidable;
import io.github.team3engine.interfaces.Updatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionManager implements Updatable {

    private static final String COLLIDE_SFX = "collide.mp3";
    private static final float SOUND_COOLDOWN = 0.5f;

    private boolean enabled = true;
    private final Map<Collidable, Integer> layers = new HashMap<>();
    private AudioManager audioManager;
    private float soundTimer = 0; // Tracks elapsed time

    @Override
    public void update(float deltaTime) {
        if (soundTimer > 0) {
            soundTimer -= deltaTime;
        }
    }

    public CollisionManager() { }

    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void register(Collidable entity) {
        register(entity, 0);
    }

    public void register(Collidable entity, int layer) {
        if (entity == null) return;
        layers.put(entity, layer);
    }

    public void unregister(Collidable entity) {
        layers.remove(entity);
    }

    public int getLayer(Collidable entity) {
        return layers.getOrDefault(entity, 0);
    }

    /** Clear all registered collidables to release references (e.g. on shutdown). */
    public void clear() {
        layers.clear();
    }

    public Array<Collidable[]> checkCollisions() {
        Array<Collidable[]> pairs = new Array<>();

        if (!enabled) {
            return pairs;
        }

        List<Collidable> list = new ArrayList<>(layers.keySet());

        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                Collidable a = list.get(i);
                Collidable b = list.get(j);

                if (skipEntity(a) || skipEntity(b)) continue;
                if (getLayer(a) != getLayer(b)) continue;

                if (aabbOverlap(a, b)) {
                    pairs.add(new Collidable[]{a, b});
                }
            }
        }

        return pairs;
    }

    public Array<Collidable[]> resolveCollisions() {
        Array<Collidable[]> pairs = checkCollisions();

        for (Collidable[] pair : pairs) {
            if (pair == null || pair.length < 2) continue;

            // Trigger audio only once per "set" of collisions in this frame
            // if (audioManager != null && soundTimer <= 0) {
            //     audioManager.play(COLLIDE_SFX);
            //     soundTimer = SOUND_COOLDOWN; 
            // }

            // Logic for individual entity reactions
            pair[0].onCollision(pair[1]);
            pair[1].onCollision(pair[0]);
        }

        return pairs;
    }

    private boolean skipEntity(Collidable e) {
        if (e == null) return true;
        if (e instanceof Entity) {
            Entity ent = (Entity) e;
            return ent.isDestroyed() || !ent.isActive();
        }
        return false;
    }

    private boolean aabbOverlap(Collidable a, Collidable b) {
        if (a == null || b == null) return false;
        if (a.getHitbox() == null || b.getHitbox() == null) return false;
        return a.getHitbox().overlaps(b.getHitbox());
    }
}

package io.github.team3engine.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.team3engine.engine.movement.MovementState;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles animation logic: loading textures, tracking timers,
 * picking the correct frame, and flipping for direction.
 * Supports both Player (full moveset) and NPC (idle + run only).
 */
public class PlayerAnimator {
    private final List<Texture> allTextures = new ArrayList<>();
    private final TextureRegion idleFrameEast;
    private final TextureRegion idleFrameWest;
    private final Animation<TextureRegion> runAnimation;
    private final Animation<TextureRegion> jumpAnimation;
    private final Animation<TextureRegion> crouchAnimation;

    private float stateTime = 0f;
    private float crouchStateTime = 0f;
    private boolean wasCrouching = false;
    private boolean facingRight = true;

    /** Full player animator with idle, run, jump, and crouch animations. */
    public PlayerAnimator() {
        // Idle frames (single image per direction)
        Texture idleE = loadTexture("player/rotations/east.png");
        Texture idleW = loadTexture("player/rotations/west.png");
        this.idleFrameEast = new TextureRegion(idleE);
        this.idleFrameWest = new TextureRegion(idleW);

        // Running animation (6 frames)
        this.runAnimation = loadFrameAnimation("player/animations/running-6-frames/east/frame_", 6, 0.1f);

        // Jumping animation (9 frames)
        this.jumpAnimation = loadFrameAnimation("player/animations/jumping-1/east/frame_", 9, 0.08f);

        // Crouching animation (5 frames)
        this.crouchAnimation = loadFrameAnimation("player/animations/crouching/east/frame_", 5, 0.12f);
    }

    /**
     * Configurable animator with custom asset paths (idle + run only).
     * Suitable for NPCs that only need idle and running animations.
     *
     * @param idleEastPath  path to the east-facing idle texture
     * @param idleWestPath  path to the west-facing idle texture
     * @param runPathPrefix path prefix for run frames (e.g. "npc/running-6-frames/east/frame_")
     * @param runFrameCount number of run animation frames
     * @param runFrameDuration duration per run frame in seconds
     */
    public PlayerAnimator(String idleEastPath, String idleWestPath,
                          String runPathPrefix, int runFrameCount, float runFrameDuration) {
        Texture idleE = loadTexture(idleEastPath);
        Texture idleW = loadTexture(idleWestPath);
        this.idleFrameEast = new TextureRegion(idleE);
        this.idleFrameWest = new TextureRegion(idleW);

        this.runAnimation = loadFrameAnimation(runPathPrefix, runFrameCount, runFrameDuration);
        this.jumpAnimation = null;
        this.crouchAnimation = null;
    }

    /** Updates animation timers and facing direction based on movement state. */
    public void update(MovementState movementState, float dt) {
        stateTime += dt;

        // Track crouch animation timer
        boolean crouchingNow = movementState.isCrouching();
        if (crouchingNow && !wasCrouching) {
            crouchStateTime = 0f;
        }
        if (crouchingNow) {
            crouchStateTime += dt;
        }
        wasCrouching = crouchingNow;

        // Track facing direction
        if (movementState.getVelocityX() > 0) facingRight = true;
        else if (movementState.getVelocityX() < 0) facingRight = false;
    }

    /** Simple update for entities without MovementState (e.g. NPCs). */
    public void update(float dt, boolean isMoving, boolean facingRight) {
        stateTime += dt;
        this.facingRight = facingRight;
    }

    /** Returns the correct animation frame based on current movement state. */
    public TextureRegion getCurrentFrame(MovementState movementState) {
        // Priority: jumping > crouching > running > idle
        TextureRegion frame;
        if (jumpAnimation != null && !movementState.isGrounded()) {
            frame = jumpAnimation.getKeyFrame(stateTime, false);
        } else if (crouchAnimation != null && movementState.isCrouching()) {
            frame = crouchAnimation.getKeyFrame(crouchStateTime, false);
        } else if (movementState.getVelocityX() != 0) {
            frame = runAnimation.getKeyFrame(stateTime, true);
        } else {
            frame = facingRight ? idleFrameEast : idleFrameWest;
        }

        // Flip for facing direction
        if (facingRight && frame.isFlipX()) frame.flip(true, false);
        else if (!facingRight && !frame.isFlipX()) frame.flip(true, false);

        return frame;
    }

    /** Simple frame getter for entities without MovementState (e.g. NPCs). */
    public TextureRegion getCurrentFrame(boolean isMoving) {
        TextureRegion frame;
        if (isMoving) {
            frame = runAnimation.getKeyFrame(stateTime, true);
        } else {
            frame = facingRight ? idleFrameEast : idleFrameWest;
        }

        if (facingRight && frame.isFlipX()) frame.flip(true, false);
        else if (!facingRight && !frame.isFlipX()) frame.flip(true, false);

        return frame;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    /** Force facing direction — used by NPC to mirror delayed player direction. */
    public void setFacingRight(boolean value) {
        this.facingRight = value;
    }

    /** Loads a texture and tracks it for disposal. */
    private Texture loadTexture(String path) {
        Texture t = new Texture(path);
        allTextures.add(t);
        return t;
    }

    /** Loads numbered frame PNGs (frame_000.png ... frame_NNN.png) into an Animation. */
    private Animation<TextureRegion> loadFrameAnimation(String pathPrefix, int count, float frameDuration) {
        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            Texture t = loadTexture(pathPrefix + String.format("%03d", i) + ".png");
            frames[i] = new TextureRegion(t);
        }
        return new Animation<>(frameDuration, frames);
    }

    /** Disposes all loaded textures. */
    public void dispose() {
        for (Texture t : allTextures) {
            t.dispose();
        }
    }
}

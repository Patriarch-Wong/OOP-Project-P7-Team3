package io.github.team3engine.game.factories;

import io.github.team3engine.game.entities.SpriteEntity;

public class SpriteEntityFactory {
    private SpriteEntityFactory() {}

     public static SpriteEntity animated(String id, float x, float y, String spritesheetPath, int frameCols, int frameRows, float frameDuration, float scaleX, float scaleY, boolean hazard) {
        return new SpriteEntity(
                id, x, y,
                spritesheetPath,
                frameCols, frameRows,
                frameDuration,
                scaleX, scaleY,
                hazard
        );
    }

    public static SpriteEntity staticSprite(String id, float x, float y, String texturePath, float scaleX, float scaleY, boolean hazard) {
        return new SpriteEntity(
                id, x, y,
                texturePath,
                scaleX, scaleY,
                hazard
        );
    }
}
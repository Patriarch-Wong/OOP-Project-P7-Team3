package io.github.team3engine.game.factories;

import com.badlogic.gdx.graphics.Texture;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.engine.io.IOManager;
import io.github.team3engine.game.entities.ExitDoor;
import io.github.team3engine.game.entities.Fire;
import io.github.team3engine.game.entities.MaskPickup;
import io.github.team3engine.game.entities.Platform;
import io.github.team3engine.game.entities.WetTowelPickup;
import io.github.team3engine.game.entities.WinBox;

/**
 * Factory to create static entities like platforms or winbox (entities that dont move)
 */
public class StaticEntityFactory {
    private StaticEntityFactory() {}

    public static <T extends CollidableEntity> T createEntity(Class<T> type, String id, float x, float y, float scaleX, float scaleY, Texture tex) {
        if (type.equals(Platform.class)) {
            return type.cast(new Platform(id, x, y, scaleX, scaleY, tex));
        } else if (type.equals(WinBox.class)) {
            return type.cast(new WinBox(id, x, y, scaleX, scaleY));
        }
        throw new IllegalArgumentException("Unknown entity type: " + type);
    }

    public static ExitDoor createExitDoor(String id, float x, float y,
                                          float width, float height,
                                          int requiredRescues, IOManager io) {
        return new ExitDoor(id, x, y, width, height, requiredRescues, io);
    }

    public static Fire createFire(String id, float x, float y,
                                  float width, float height,
                                  Texture fireTex, int frameCols, int frameRows,
                                  boolean upsideDown) {
        return new Fire(id, x, y, width, height, fireTex, frameCols, frameRows, upsideDown);
    }

    public static <T extends CollidableEntity> T createPickup(Class<T> type, String id, float x, float y) {
        if (type.equals(WetTowelPickup.class)) {
            return type.cast(new WetTowelPickup(id, x, y));
        } else if (type.equals(MaskPickup.class)) {
            return type.cast(new MaskPickup(id, x, y));
        }
        throw new IllegalArgumentException("Unknown pickup type: " + type);
    }
}

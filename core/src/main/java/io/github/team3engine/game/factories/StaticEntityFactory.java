package io.github.team3engine.game.factories;

import com.badlogic.gdx.graphics.Texture;

import io.github.team3engine.engine.entity.CollidableEntity;
import io.github.team3engine.game.entities.Platform;
import io.github.team3engine.game.entities.WinBox;

/**
 * Factory to create static entities like platforms or winbox (entities that dont move)
 */
public class StaticEntityFactory {
    private StaticEntityFactory() {}

    public static <T extends CollidableEntity> T createEntity(Class<T> type, String id, float x, float y, float scaleX, float scaleY, String texturePath) {
        if (type.equals(Platform.class)) {
            Texture tex = new Texture(texturePath);
            return type.cast(new Platform(id, x, y, scaleX, scaleY, tex));
        } else if (type.equals(WinBox.class)) {
            return type.cast(new WinBox(id, x, y, scaleX, scaleY));
        }
        throw new IllegalArgumentException("Unknown entity type: " + type);
    }
}

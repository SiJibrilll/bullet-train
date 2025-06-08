package studio.jawa.bullettrain.factories;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.data.characters.GraceCharacter;
import studio.jawa.bullettrain.data.characters.JingCharacter;
import studio.jawa.bullettrain.data.characters.BaseCharacter.Animation;;
import studio.jawa.bullettrain.entities.players.PlayerEntity;

public class CharacterFactory {
    static PlayerEntity createGrace(float x, float y, AssetManager assetManager) {
        GraceCharacter grace = new GraceCharacter();
        GeneralStatsComponent stats = grace.getStat();
        Texture idle = assetManager.get(grace.getAnim(Animation.IDLE), Texture.class);
        return new PlayerEntity(x, y, idle, stats);
    }

    static PlayerEntity createJing(float x, float y, AssetManager assetManager) {
        JingCharacter jing = new JingCharacter();
        GeneralStatsComponent stats = jing.getStat();
        Texture idle = assetManager.get(jing.getAnim(Animation.IDLE), Texture.class);
        return new PlayerEntity(x, y, idle, stats);
    }
}

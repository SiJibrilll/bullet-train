package studio.jawa.bullettrain.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;
import studio.jawa.bullettrain.data.GameConstants;
import studio.jawa.bullettrain.entities.players.PlayerEntity;
import studio.jawa.bullettrain.screens.uiscreens.CharacterInfo;

public class PlayerFactory {

    public static Entity createPlayerAtCarriageEntry(int carriageNumber, AssetManager assetManager, Engine engine, CharacterInfo selectedCharacter) {
        // Calculate spawn position at carriage entry
        float spawnX = GameConstants.CARRIAGE_WIDTH / 2f;
        float carriageY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;
        float spawnY = carriageY + GameConstants.ENTRY_ZONE_HEIGHT + 50f;

        PlayerEntity player;
        switch (selectedCharacter.name) {
            case "Grace":
                player = CharacterFactory.createGrace(spawnX, spawnY, assetManager, engine);
                break;
            case "Jing Wei":
                player = CharacterFactory.createJing(spawnX, spawnY, assetManager, engine);
                break;
            default:
                player = CharacterFactory.createGrace(spawnX, spawnY, assetManager, engine);
                break;
        }
       
        
        // Set current carriage
        PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
        playerComp.currentCarriageNumber = carriageNumber;
        System.out.println(playerComp.currentCarriageNumber);

        return player;
    }
}

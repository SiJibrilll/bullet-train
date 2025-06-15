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

public class PlayerFactory {

    public static Entity createPlayerAtCarriageEntry(int carriageNumber, AssetManager assetManager, Engine engine) {
        // Calculate spawn position at carriage entry
        float spawnX = GameConstants.CARRIAGE_WIDTH / 2f;
        float carriageY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;
        float spawnY = carriageY + GameConstants.ENTRY_ZONE_HEIGHT + 50f;

        
        // PlayerEntity player = new PlayerEntity(spawnX, spawnY, playerTexture, stats);
        PlayerEntity player = CharacterFactory.createJing(spawnX, spawnY, assetManager, engine);
        // player.add(new PlayerComponent());
        // TODO i removed the player component addition here because i afraid itll cause duplication errors
        // revisit if this results in a bug
        
        // Set current carriage
        PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
        playerComp.currentCarriageNumber = carriageNumber;
        System.out.println(playerComp.currentCarriageNumber);

        return player;
    }
}

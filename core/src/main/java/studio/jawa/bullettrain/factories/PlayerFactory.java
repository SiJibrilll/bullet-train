package studio.jawa.bullettrain.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;

import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.gameplays.GeneralStatsComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;
import studio.jawa.bullettrain.data.GameConstants;
import studio.jawa.bullettrain.entities.players.PlayerEntity;

public class PlayerFactory {

    public static Entity createPlayerAtCarriageEntry(int carriageNumber, Texture playerTexture) {
        // Calculate spawn position at carriage entry
        float spawnX = GameConstants.CARRIAGE_WIDTH / 2f;
        float carriageY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;
        float spawnY = carriageY + GameConstants.ENTRY_ZONE_HEIGHT + 50f;
        
        // Create player with PlayerEntity
        GeneralStatsComponent stats = new GeneralStatsComponent(100, 200);
        PlayerEntity player = new PlayerEntity(spawnX, spawnY, playerTexture, stats);
        
        // Add PlayerComponent (karena PlayerEntity tidak memilikinya)
        player.add(new PlayerComponent());
        
        // Set current carriage
        PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
        playerComp.currentCarriageNumber = carriageNumber;
        
        return player;
    }
}

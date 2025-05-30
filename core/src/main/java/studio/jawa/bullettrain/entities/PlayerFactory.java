package studio.jawa.bullettrain.entities;

import com.badlogic.ashley.core.Entity;
import studio.jawa.bullettrain.components.gameplay.PlayerComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;
import studio.jawa.bullettrain.data.GameConstants;

public class PlayerFactory {
    
    public static Entity createPlayer(float x, float y) {
        Entity player = new Entity();
        
        // Transform component (position)
        player.add(new TransformComponent(x, y));
        
        // Velocity component (movement)
        player.add(new VelocityComponent(200f)); // 200 pixels per second max speed
        
        // Player component (player-specific data)
        player.add(new PlayerComponent());
        
        return player;
    }
    
    public static Entity createPlayerAtCarriageEntry(int carriageNumber) {
        // Calculate spawn position at carriage entry
        float spawnX = GameConstants.CARRIAGE_WIDTH / 2f; 
        float carriageY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;
        float spawnY = carriageY + GameConstants.ENTRY_ZONE_HEIGHT + 50f; 
        
        Entity player = createPlayer(spawnX, spawnY);
        
        // Set current carriage
        PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
        playerComp.currentCarriageNumber = carriageNumber;
        
        return player;
    }
}
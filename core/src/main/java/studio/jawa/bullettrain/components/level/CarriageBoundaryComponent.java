package studio.jawa.bullettrain.components.level;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import studio.jawa.bullettrain.data.GameConstants;

public class CarriageBoundaryComponent implements Component {
    // Different zones dalam carriage
    public Rectangle carriageBounds = new Rectangle();
    public Rectangle playableBounds = new Rectangle();
    public Rectangle entryZone = new Rectangle();
    public Rectangle exitZone = new Rectangle();
    
    public CarriageBoundaryComponent() {
        setupDefaultBounds();
    }
    
    private void setupDefaultBounds() {
        // Full carriage bounds
        carriageBounds.set(0, 0, GameConstants.CARRIAGE_WIDTH, GameConstants.CARRIAGE_HEIGHT);
        
        // Playable area (center)
        float offsetX = (GameConstants.CARRIAGE_WIDTH - GameConstants.PLAYABLE_WIDTH) / 2f;
        playableBounds.set(offsetX, GameConstants.ENTRY_ZONE_HEIGHT, 
                          GameConstants.PLAYABLE_WIDTH, GameConstants.PLAYABLE_HEIGHT);
        
        // Entry zone (bottom)
        entryZone.set(offsetX, 0, GameConstants.PLAYABLE_WIDTH, GameConstants.ENTRY_ZONE_HEIGHT);
        
        // Exit zone (top)
        exitZone.set(offsetX, GameConstants.CARRIAGE_HEIGHT - GameConstants.EXIT_ZONE_HEIGHT,
                    GameConstants.PLAYABLE_WIDTH, GameConstants.EXIT_ZONE_HEIGHT);
    }
}
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
    
    public CarriageBoundaryComponent(int carriageNumber) {
        setupBoundsForCarriage(carriageNumber);
    }
    
    private void setupDefaultBounds() {
        setupBoundsForCarriage(1);
    }
    
    private void setupBoundsForCarriage(int carriageNumber) {
        // Calculate carriage offset
        float carriageOffsetY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;
        
        // Full carriage bounds
        carriageBounds.set(0, carriageOffsetY, GameConstants.CARRIAGE_WIDTH, GameConstants.CARRIAGE_HEIGHT);
        
        // Playable area (center)
        float offsetX = (GameConstants.CARRIAGE_WIDTH - GameConstants.PLAYABLE_WIDTH) / 2f;
        playableBounds.set(offsetX, carriageOffsetY + GameConstants.ENTRY_ZONE_HEIGHT, 
                          GameConstants.PLAYABLE_WIDTH, GameConstants.PLAYABLE_HEIGHT);
        
        // Entry zone (bottom)
        entryZone.set(offsetX, carriageOffsetY, GameConstants.PLAYABLE_WIDTH, GameConstants.ENTRY_ZONE_HEIGHT);
        
        // Exit zone (top)
        exitZone.set(offsetX, carriageOffsetY + GameConstants.CARRIAGE_HEIGHT - GameConstants.EXIT_ZONE_HEIGHT,
                    GameConstants.PLAYABLE_WIDTH, GameConstants.EXIT_ZONE_HEIGHT);
    }
    
    public void updateForCarriage(int carriageNumber) {
        setupBoundsForCarriage(carriageNumber);
    }
}
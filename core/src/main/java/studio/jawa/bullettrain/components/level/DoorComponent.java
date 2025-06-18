package studio.jawa.bullettrain.components.level;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

public class DoorComponent implements Component {
    public DoorType doorType;
    public Rectangle doorBounds = new Rectangle();
    public int targetCarriageNumber;
    public boolean isPlayerNearby = false;
    public boolean isActive = true; 
    
    public DoorComponent() {}
    
    public DoorComponent(DoorType doorType, int targetCarriageNumber) {
        this.doorType = doorType;
        this.targetCarriageNumber = targetCarriageNumber;
    }
    
    public enum DoorType {
        EXIT_TO_NEXT,    // Door menuju carriage berikutnya (ke atas)
        ENTRY_FROM_PREV  // Door dari carriage sebelumnya (dari bawah)
    }
}
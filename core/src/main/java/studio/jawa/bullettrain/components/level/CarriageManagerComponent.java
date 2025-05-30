package studio.jawa.bullettrain.components.level;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.IntMap;

public class CarriageManagerComponent implements Component {
    public IntMap<Entity> loadedCarriages = new IntMap<>();  // carriageNumber -> Entity
    public int currentCarriageNumber = 1;
    public int maxCarriages = 5;
    
    // Active window (3 carriages loaded at once)
    public int windowSize = 3;
    
    public CarriageManagerComponent() {}
    
    public boolean isCarriageLoaded(int carriageNumber) {
        return loadedCarriages.containsKey(carriageNumber);
    }
    
    public Entity getCarriage(int carriageNumber) {
        return loadedCarriages.get(carriageNumber);
    }
    
    public void addCarriage(int carriageNumber, Entity carriage) {
        loadedCarriages.put(carriageNumber, carriage);
    }
    
    public void removeCarriage(int carriageNumber) {
        loadedCarriages.remove(carriageNumber);
    }
}
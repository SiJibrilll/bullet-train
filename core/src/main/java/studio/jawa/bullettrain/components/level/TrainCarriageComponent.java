package studio.jawa.bullettrain.components.level;

import com.badlogic.ashley.core.Component;
import studio.jawa.bullettrain.data.CarriageType;

public class TrainCarriageComponent implements Component {
    public int carriageNumber;           
    public CarriageType carriageType;    
    public boolean isActive = false;
    public long generationSeed;
    
    public TrainCarriageComponent() {}
    
    public TrainCarriageComponent(int carriageNumber, CarriageType carriageType, long seed) {
        this.carriageNumber = carriageNumber;
        this.carriageType = carriageType;
        this.generationSeed = seed;
    }
}
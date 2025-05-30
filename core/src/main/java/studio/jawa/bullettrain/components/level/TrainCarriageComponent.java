package studio.jawa.bullettrain.components.level;

import com.badlogic.ashley.core.Component;
import studio.jawa.bullettrain.data.CarriageType;

public class TrainCarriageComponent implements Component {
    public int carriageNumber;           // 1, 2, 3, 4, 5
    public CarriageType carriageType;    
    public boolean isActive = false;
    public long generationSeed;
    public float difficultyMultiplier = 1.0f;
    
    public TrainCarriageComponent() {}
    
    public TrainCarriageComponent(int carriageNumber, CarriageType carriageType, long seed) {
        this.carriageNumber = carriageNumber;
        this.carriageType = carriageType;
        this.generationSeed = seed;
        this.difficultyMultiplier = 1.0f + (carriageNumber - 1) * 0.2f; // Progressive difficulty
    }
}
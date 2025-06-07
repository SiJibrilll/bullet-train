package studio.jawa.bullettrain.components.gameplay.enemies;

import com.badlogic.ashley.core.Component;

public class EnemyComponent implements Component {
    public int carriageNumber; 
    public boolean isActive = true;
    
    public EnemyComponent() {}
    
    public EnemyComponent(int carriageNumber) {
        this.carriageNumber = carriageNumber;
    }
}

package studio.jawa.bullettrain.components.gameplay;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public int currentCarriageNumber = 1;
    public boolean isTransitioning = false;
    public float health = 100f;
    public float speed = 200f; // pixels per second
    
    public PlayerComponent() {}
}
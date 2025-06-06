package studio.jawa.bullettrain.components.gameplay.palyers;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public int currentCarriageNumber = 1;
    public boolean isTransitioning = false;


    public PlayerComponent() {}
}

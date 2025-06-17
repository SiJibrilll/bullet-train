package studio.jawa.bullettrain.components.gameplay.palyers;

import com.badlogic.ashley.core.Component;

import studio.jawa.bullettrain.data.characters.BaseCharacter;

public class PlayerComponent implements Component {
    public int currentCarriageNumber = 1;
    public boolean isTransitioning = false;
    public BaseCharacter character;
    public float delay = 0;


    public PlayerComponent() {}
}

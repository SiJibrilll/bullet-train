package studio.jawa.bullettrain.components.technicals;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class ParentComponent implements Component {
    public Entity parent;
    public float distanceFromParent = 20f; // how far the weapon orbits from the owner
}


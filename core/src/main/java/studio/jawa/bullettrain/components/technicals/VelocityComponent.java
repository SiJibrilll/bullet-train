package studio.jawa.bullettrain.components.technicals;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class VelocityComponent implements Component {
    public Vector2 velocity = new Vector2();
    public float maxSpeed = 200f;
    
    public VelocityComponent() {}
    
    public VelocityComponent(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
package studio.jawa.bullettrain.components.technicals;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Circle;

public class CircleColliderComponent implements Component {
    public Circle circle = new Circle();;

    public  CircleColliderComponent(float x, float y, float radius) {
        circle.set(x, y, radius);
    }
}


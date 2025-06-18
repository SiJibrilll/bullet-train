package studio.jawa.bullettrain.components.technicals;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;

public class BoxColliderComponent implements Component {
    public Rectangle bounds;

    public BoxColliderComponent(Rectangle rect) {
        this.bounds = rect;
    }

}

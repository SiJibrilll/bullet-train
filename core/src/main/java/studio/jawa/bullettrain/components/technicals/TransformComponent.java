package studio.jawa.bullettrain.components.technicals;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component {
    public final Vector2 position = new Vector2();
    public float rotation = 0f;
    public float scale = 1f;
    public final Vector2 origin = new Vector2(0.5f, 0.5f); // default: center

    public TransformComponent(float x, float y) {
        position.x = x;
        position.y = y;
    }

}

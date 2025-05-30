package studio.jawa.bullettrain.components.technicals;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

// SpriteComponent.java
public class SpriteComponent implements Component {
    public Sprite sprite;

    public SpriteComponent(Sprite sprite) {
        this.sprite = sprite;
    }
}

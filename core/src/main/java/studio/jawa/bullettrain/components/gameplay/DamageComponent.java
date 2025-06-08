package studio.jawa.bullettrain.components.gameplay;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class DamageComponent implements Component{
    public float damage;
    public Vector2 direction;
    public DamageComponent(float damage, Vector2 direction) {
        this.damage = damage;
        this.direction = direction;
    }
}

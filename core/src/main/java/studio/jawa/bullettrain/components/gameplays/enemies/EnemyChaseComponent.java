package studio.jawa.bullettrain.components.gameplays.enemies;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class EnemyChaseComponent implements Component {
    public boolean windup = false;
    public float windupDuration;
    public float windupDirection;

    public EnemyChaseComponent(float windupDuration) {
        this.windupDuration = windupDuration;
    }
}

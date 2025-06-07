package studio.jawa.bullettrain.components.gameplay.enemies;

import com.badlogic.ashley.core.Component;


public class EnemyChaseComponent implements Component {
    public boolean windup = false;
    public float windupDuration;
    public float windupDirection;

    public EnemyChaseComponent(float windupDuration) {
        this.windupDuration = windupDuration;
    }
}

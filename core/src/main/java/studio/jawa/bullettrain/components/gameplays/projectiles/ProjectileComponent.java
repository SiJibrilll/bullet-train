package studio.jawa.bullettrain.components.gameplays.projectiles;

import com.badlogic.ashley.core.Component;

public class ProjectileComponent implements Component {
    public float speed = 0;
    public boolean isMeele;
    public float meleeDuration = 0;

    public ProjectileComponent(float speed) {
        this.speed = speed;
    }

    public ProjectileComponent(float speed, boolean isMeele, float meleeDuration) {
        this.speed = speed;
        this.isMeele = isMeele;
        this.meleeDuration = meleeDuration;
    }
}

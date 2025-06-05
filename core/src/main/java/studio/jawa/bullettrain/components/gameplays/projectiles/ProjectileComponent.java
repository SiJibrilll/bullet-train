package studio.jawa.bullettrain.components.gameplays.projectiles;

import com.badlogic.ashley.core.Component;

import java.awt.*;

public class ProjectileComponent implements Component {
    public float speed = 0;

    public ProjectileComponent(float speed) {
        this.speed = speed;
    }
}

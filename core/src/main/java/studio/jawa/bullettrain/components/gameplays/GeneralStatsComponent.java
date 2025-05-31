package studio.jawa.bullettrain.components.gameplays;

import com.badlogic.ashley.core.Component;

public class GeneralStatsComponent implements Component {
    public float health;
    public float speed;
    public float dash;

    public GeneralStatsComponent(float health, float speed, float dash) {
        this.health = health;
        this.speed = speed;
        this.dash = dash;
    }

    public GeneralStatsComponent(float health, float speed) {
        this.health = health;
        this.speed = speed;
        this.dash = 0;
    }
}

package studio.jawa.bullettrain.components.gameplay.enemies;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.MathUtils;

public class EnemyStrafeComponent implements Component {
    public boolean attack = true;
    public boolean clockWise = MathUtils.randomBoolean();
    public float duration;
    public float offset = MathUtils.random(-60f, 60f);

    public EnemyStrafeComponent(float duration) {
        this.duration  = duration;

    }
}

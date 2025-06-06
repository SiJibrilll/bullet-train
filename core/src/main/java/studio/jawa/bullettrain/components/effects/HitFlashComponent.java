package studio.jawa.bullettrain.components.effects;

import com.badlogic.ashley.core.Component;

public class HitFlashComponent implements Component {
    public float duration = 0.1f;
    public float timer = 0f;
    public boolean active = true;
    public boolean flash = true;

    public HitFlashComponent(float time) {
        timer = time;
    }
}


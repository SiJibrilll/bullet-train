package studio.jawa.bullettrain.components.technicals;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

public class WeaponComponent implements Component {
    public boolean isPlayerWeapon = false;
    public Vector2 target = new Vector2(); // for enemies, the position to aim at
}


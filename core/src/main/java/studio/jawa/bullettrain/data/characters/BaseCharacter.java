package studio.jawa.bullettrain.data.characters;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.entities.Projectiles.ProjectileEntity;

public abstract class BaseCharacter {
    protected String name = "Character";
    
    protected Integer hp = 10;
    protected Integer speed = 10;
    protected Float attackSpeed = .5f;

    protected String idlePath = "testing/dummy.png";
    protected String runPath;
    protected String deathPath;
    protected String bulletPath;
    protected String attackpath = "testing/bullet.png";
    protected String attackSound = "sword";

    public enum Animation {
        IDLE,
        RUN,
        DEATH
    }

    public String getAnim(Animation anim) {
        switch (anim) {
            case IDLE:
                return idlePath;
            case RUN:
                return runPath;
            case DEATH:
                return deathPath;
            default:
                return "testing/dummy.png";
        }
    }

    public GeneralStatsComponent getStat() {
        return new GeneralStatsComponent(hp, speed);
    }

    public abstract ProjectileEntity attack(float x, float y, Vector2 direction, AssetManager manager);

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public String getAttackSound() {
        return attackSound;
    }
}

package studio.jawa.bullettrain.data.characters;



import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent.Team;
import studio.jawa.bullettrain.entities.Projectiles.ProjectileEntity;

public class GraceCharacter extends BaseCharacter {
    public GraceCharacter() {
        this.name = "Grace";
        this.hp = 10;
        this.speed = 450;
        this.idlePath = "testing/dummy.png";
        this.runPath = "some/path/to/run.png";
        this.deathPath = "some/path/to/death.png";
        this.bulletPath = "some/path/to/bullet.png";
    }

    @Override
    public ProjectileEntity attack(float x, float y, Vector2 direction, Texture bulletTex) {
        return new ProjectileEntity(x, y, direction, bulletTex, speed, 0.5f, Team.PLAYER);
    }

}

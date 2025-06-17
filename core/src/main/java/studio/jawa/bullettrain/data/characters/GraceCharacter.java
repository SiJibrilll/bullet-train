package studio.jawa.bullettrain.data.characters;



import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent.Team;
import studio.jawa.bullettrain.entities.Projectiles.ProjectileEntity;

public class GraceCharacter extends BaseCharacter {

    float bulletSpeed = 2000f;
    public GraceCharacter() {
        this.name = "Grace";
        this.hp = 10;
        this.speed = 450;
        this.idlePath = "characters/grace/Grace_Idle.png";
        this.runPath = "characters/grace/Grace_Walk.png";
        this.deathPath = "characters/grace/Grace_Death.png";
        this.attackpath = "particles/Bullet_Ally.png";
        this.attackSound = "gun";
    }

    @Override
    public ProjectileEntity attack(float x, float y, Vector2 direction, AssetManager manager) {
        Texture bulletTex = manager.get(attackpath, Texture.class);
        return new ProjectileEntity(x, y, direction, bulletTex, bulletSpeed, 2.5f, Team.PLAYER);
    }

}

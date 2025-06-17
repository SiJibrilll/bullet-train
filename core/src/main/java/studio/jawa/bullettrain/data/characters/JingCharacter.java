package studio.jawa.bullettrain.data.characters;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent.Team;
import studio.jawa.bullettrain.entities.Projectiles.ProjectileEntity;

public class JingCharacter extends BaseCharacter{
    public JingCharacter() {
        this.name = "Jing Wei";
        this.hp = 100000000; //TODO jingwei is immortal for testing reasons
        this.speed = 550;
        this.idlePath = "testing/dummy2.png";
        this.runPath = "some/path/to/run.png";
        this.deathPath = "some/path/to/death.png";
        this.attackpath = "particles/slash.png";
    }

    @Override
    public ProjectileEntity attack(float x, float y, Vector2 direction, AssetManager manager) {
        float spawnDistance = 100f;
        float spawnX = x + direction.x * spawnDistance;
        float spawnY = y + direction.y * spawnDistance;
        Texture bulletTex = manager.get(attackpath, Texture.class);
        return new ProjectileEntity(spawnX, spawnY, direction, bulletTex, 5f, true, 0.2f, Team.PLAYER, manager);
    }
}

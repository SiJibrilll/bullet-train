package studio.jawa.bullettrain.systems.projectiles;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import studio.jawa.bullettrain.components.gameplays.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;
import studio.jawa.bullettrain.entities.Projectiles.ProjectileEntity;

public class ProjectileSpawnSystem extends EntitySystem {
    private final Camera camera;
    private final Engine engine;
    private final ComponentMapper<ProjectileComponent> pm = ComponentMapper.getFor(ProjectileComponent.class);
    private ImmutableArray<Entity> players;

    private final Texture bulletTexture;

    @Override
    public void addedToEngine(Engine engine) {
        // Query player entities using a tag component or unique component
        players = engine.getEntitiesFor(Family.all(TransformComponent.class, PlayerControlledComponent.class).get());
    }

    public ProjectileSpawnSystem(Camera camera, Engine engine, Texture bulletTexture) {
        this.camera = camera;
        this.engine = engine;
        this.bulletTexture = bulletTexture;
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.justTouched()) {
            spawnProjectile(players.first());
        }
    }

    private void spawnProjectile(Entity player) {
        // 1. Get mouse position in world coordinates
        Vector3 mouseWorld = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);

        // 2. Get player's position
        TransformComponent playerPos = player.getComponent(TransformComponent.class);
        Vector2 start = new Vector2(playerPos.position.x, playerPos.position.y);

        // 3. Calculate normalized direction
        Vector2 direction = new Vector2(mouseWorld.x, mouseWorld.y).sub(start).nor();

        ProjectileEntity projectile = new ProjectileEntity(start.x, start.y, direction, bulletTexture, 2000f, 0.5f);

        engine.addEntity(projectile);
    }
}

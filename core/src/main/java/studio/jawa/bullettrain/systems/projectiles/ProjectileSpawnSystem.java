package studio.jawa.bullettrain.systems.projectiles;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import studio.jawa.bullettrain.components.gameplays.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class ProjectileSpawnSystem extends EntitySystem {
    private final Camera camera;
    private final Engine engine;
    private final Entity player;
    private final float PROJECTILE_SPEED = 500f;
    private final Texture bulletTexture;

    public ProjectileSpawnSystem(Camera camera, Engine engine, Entity player, Texture bulletTexture) {
        this.camera = camera;
        this.engine = engine;
        this.player = player;
        this.bulletTexture = bulletTexture;
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.justTouched()) {
            spawnProjectile();
        }
    }

    private void spawnProjectile() {
        // 1. Get mouse position in world coordinates
        Vector3 mouseWorld = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);

        // 2. Get player's position
        TransformComponent playerPos = player.getComponent(TransformComponent.class);
        Vector2 start = new Vector2(playerPos.position.x, playerPos.position.y);

        // 3. Calculate normalized direction
        Vector2 direction = new Vector2(mouseWorld.x, mouseWorld.y).sub(start).nor();

        // 4. Create projectile entity
        Entity projectile = new Entity();

        TransformComponent pos = new TransformComponent(start.x, start.y);

        VelocityComponent vel = new VelocityComponent();
        vel.velocity.x = direction.x * PROJECTILE_SPEED;
        vel.velocity.y = direction.y * PROJECTILE_SPEED;

        SpriteComponent tex = new SpriteComponent(new Sprite(bulletTexture));

        pos.scale = 0.5f; // Adjust size
        pos.rotation = direction.angleDeg();

        pos.origin.set(0f, 0.5f);

        projectile.add(pos);
        projectile.add(vel);
        projectile.add(tex);
        projectile.add(new ProjectileComponent());

        engine.addEntity(projectile);
    }
}

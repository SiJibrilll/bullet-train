package studio.jawa.bullettrain.systems.projectiles;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent.Team;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.characters.BaseCharacter;
import studio.jawa.bullettrain.entities.Projectiles.ProjectileEntity;
import studio.jawa.bullettrain.helpers.AudioHelper;

public class PlayerProjectileSpawningSystem extends EntitySystem {
    private final Camera camera;
    private final Engine engine;
    private final AssetManager manager;
    private final ComponentMapper<ProjectileComponent> pm = ComponentMapper.getFor(ProjectileComponent.class);
    private final ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);

    private ImmutableArray<Entity> players;

    @Override
    public void addedToEngine(Engine engine) {
        // Query player entities using a tag component or unique component
        players = engine.getEntitiesFor(Family.all(TransformComponent.class, PlayerControlledComponent.class).get());
        AudioHelper.loadSound("sword", "testing/sounds/sword.mp3");
        AudioHelper.loadSound("gun", "testing/sounds/gun.mp3");
    }

    public PlayerProjectileSpawningSystem(Camera camera, Engine engine, AssetManager manager) {
        this.camera = camera;
        this.engine = engine;
        this.manager = manager;
    }

    @Override
    public void update(float deltaTime) {
        Entity player = players.first();
        PlayerComponent playerData = pcm.get(players.first());
        playerData.delay -= deltaTime;
        BaseCharacter character = pcm.get(player).character;
        
        if (playerData.delay > 0) {
            return;
        }

        if (Gdx.input.justTouched()) {
            spawnProjectile(player, deltaTime, character);
            playerData.delay = character.getAttackSpeed();
        }
    }

    private void spawnProjectile(Entity player, float deltaTime, BaseCharacter character) {
        // 1. Get mouse position in world coordinates
        Vector3 mouseWorld = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouseWorld);

        // 2. Get player's position
        TransformComponent playerPos = player.getComponent(TransformComponent.class);
        Vector2 start = new Vector2(playerPos.position.x, playerPos.position.y);

        // 3. Calculate normalized direction
        Vector2 direction = new Vector2(mouseWorld.x, mouseWorld.y).sub(start).nor();

        ProjectileEntity projectile = character.attack(start.x, start.y, direction, manager);
        AudioHelper.playSound(character.getAttackSound());
        engine.addEntity(projectile);
    }
}

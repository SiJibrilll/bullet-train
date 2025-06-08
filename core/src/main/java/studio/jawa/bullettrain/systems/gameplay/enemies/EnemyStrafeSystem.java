package studio.jawa.bullettrain.systems.gameplay.enemies;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.*;

import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;
import studio.jawa.bullettrain.data.GameConstants;
import studio.jawa.bullettrain.entities.Projectiles.ProjectileEntity;
import studio.jawa.bullettrain.helpers.AssetLocator;

public class EnemyStrafeSystem extends EntitySystem {
    private final ComponentMapper<EnemyStateComponent> sm = ComponentMapper.getFor(EnemyStateComponent.class);
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<EnemyBehaviourComponent> bm = ComponentMapper.getFor(EnemyBehaviourComponent.class);
    private final ComponentMapper<EnemyStrafeComponent> stm = ComponentMapper.getFor(EnemyStrafeComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<GeneralStatsComponent> gsm = ComponentMapper.getFor(GeneralStatsComponent.class);
    private final ComponentMapper<EnemyComponent> em = ComponentMapper.getFor(EnemyComponent.class);

    private Family playerFamily = Family.all(TransformComponent.class, PlayerControlledComponent.class).get();

    private ImmutableArray<Entity> entities;
    private AssetManager assetmanager;
    private  Engine engine;

    public EnemyStrafeSystem(AssetManager assetmanager, Engine engine) {
//        this.entities = engine.getEntitiesFor(Family.all(EnemyComponent.class, EnemyStateComponent.class, EnemyBehaviourComponent.class).get());
        this.assetmanager = assetmanager;
        this.engine = engine;
    }
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
            EnemyComponent.class,
            EnemyStateComponent.class,
            EnemyBehaviourComponent.class
        ).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {

            EnemyStateComponent state = sm.get(entity);
            if (state.state != EnemyStateComponent.STATES.STRAFE) continue; 

            // get reference to player
            ImmutableArray<Entity> players = getEngine().getEntitiesFor(playerFamily);
            Entity player = players.first();

            // check for player existance
            if (player == null) {
                state.state = EnemyStateComponent.STATES.IDLE; 
                return;
            }

            // enemy info
            EnemyStrafeComponent strafe = stm.get(entity);
            VelocityComponent vel = vm.get(entity);
            GeneralStatsComponent stat = gsm.get(entity);
            TransformComponent transform = tm.get(entity);
            EnemyBehaviourComponent behaviour = bm.get(entity);

            // Get important datas
            TransformComponent playerTransform = player.getComponent(TransformComponent.class);


           if (strafe.attack) {
               Texture bulletTex = assetmanager.get("testing/bullet.png", Texture.class);
               Vector2 aim = new Vector2(playerTransform.position).sub(transform.position);
               ProjectileEntity bullet = new ProjectileEntity(transform.position.x, transform.position.y, aim, bulletTex, 2f, 0.5f, ProjectileComponent.Team.ENEMY);
               engine.addEntity(bullet);
               strafe.attack = false;
           }

           Vector2 direction = moveLogic(deltaTime, playerTransform, transform, strafe, behaviour);

           direction = direction.scl(stat.speed);

           vel.velocity.set(direction);

           // Apply movement
           transform.position.x += vel.velocity.x * deltaTime;
           transform.position.y += vel.velocity.y * deltaTime;

           // Keep enemy within carriage bounds
           constrainEnemyToCarriage(entity, transform);

           strafe.duration -= deltaTime;

           if (strafe.duration < 0) {
               entity.remove(EnemyStrafeComponent.class);
               entity.add(new EnemyStrafeComponent(behaviour.coolDown));
           }

        }

    }

    private void constrainEnemyToCarriage(Entity entity, TransformComponent transform) {
        EnemyComponent enemy = em.get(entity);
        if (enemy == null) return;

        // Calculate current carriage bounds
        float carriageOffsetY = (enemy.carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;

        // Horizontal bounds
        float leftBound = (GameConstants.CARRIAGE_WIDTH - GameConstants.PLAYABLE_WIDTH) / 2f + 20f;
        float rightBound = leftBound + GameConstants.PLAYABLE_WIDTH - 40f;

        // Vertical bounds
        float bottomBound = carriageOffsetY + 20f;
        float topBound = carriageOffsetY + GameConstants.CARRIAGE_HEIGHT - 20f;

        // Constrain position
        transform.position.x = Math.max(leftBound, Math.min(rightBound, transform.position.x));
        transform.position.y = Math.max(bottomBound, Math.min(topBound, transform.position.y));
    }

    protected Vector2 moveLogic(float deltaTime, TransformComponent playerTransform, TransformComponent enemyTransform, EnemyStrafeComponent strafe, EnemyBehaviourComponent behaviour) {
        // Vector from player to enemy
        Vector2 toEnemy = new Vector2(enemyTransform.position).sub(playerTransform.position);

        // Distance to maintain
        float targetDistance = behaviour.aggroRange - (behaviour.aggroRange / 3);

        // Step 1: Direction from player to enemy (for distance checking)
        float currentDistance = toEnemy.len();
        toEnemy.nor();

        // Step 2: Get perpendicular direction for strafing
        Vector2 strafeDir = new Vector2(toEnemy);
        if (strafe.clockWise) {
            strafeDir.rotateDeg(-90);
        } else {
            strafeDir.rotateDeg(90);
        }

        // Step 3: Add small randomness to the strafe direction (±15 degrees)
        float randomOffset = strafe.offset;
        strafeDir.rotateDeg(randomOffset).nor();

        // Step 4: Combine strafe + maintain distance logic
        Vector2 moveDir = new Vector2();

        // If too close to player, also add a little away movement
        if (currentDistance < targetDistance) {
            moveDir.add(toEnemy); // move away
        }
        // If too far, move slightly toward the player
        else if (currentDistance > targetDistance + 30f) {
            moveDir.sub(toEnemy); // move toward
        }

        moveDir.add(strafeDir).nor(); // Combine with strafe


       return new Vector2(moveDir);
    }
}

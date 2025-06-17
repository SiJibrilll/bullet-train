package studio.jawa.bullettrain.systems.gameplay.enemies;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import studio.jawa.bullettrain.components.gameplay.DeathComponent;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyChaseComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent.Team;
import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.components.technicals.InputComponent;
import studio.jawa.bullettrain.components.technicals.ParentComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;
import studio.jawa.bullettrain.components.technicals.WeaponComponent;
import studio.jawa.bullettrain.data.GameConstants;
import studio.jawa.bullettrain.entities.Projectiles.ProjectileEntity;
import studio.jawa.bullettrain.helpers.AudioHelper;

public class EnemyChaseSystem extends EntitySystem {
    private final ComponentMapper<EnemyStateComponent> sm = ComponentMapper.getFor(EnemyStateComponent.class);
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<EnemyBehaviourComponent> bm = ComponentMapper.getFor(EnemyBehaviourComponent.class);
    private final ComponentMapper<EnemyChaseComponent> cm = ComponentMapper.getFor(EnemyChaseComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<GeneralStatsComponent> gsm = ComponentMapper.getFor(GeneralStatsComponent.class);
    private final ComponentMapper<EnemyComponent> em = ComponentMapper.getFor(EnemyComponent.class);

    // for weapons
    private final ComponentMapper<ParentComponent> pm = ComponentMapper.getFor(ParentComponent.class);
    private final ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);

    private Family playerFamily = Family.all(TransformComponent.class, PlayerControlledComponent.class).get();

    private final AssetManager manager;
    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> weaponEntities;

   public EnemyChaseSystem(AssetManager manager) {
    this.manager = manager;
   }
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
            EnemyComponent.class,
            EnemyStateComponent.class,
            EnemyBehaviourComponent.class
        ).exclude(DeathComponent.class).get());

        weaponEntities = engine.getEntitiesFor(Family.all(WeaponComponent.class, ParentComponent.class).get());
        AudioHelper.loadSound("sword", "testing/sounds/sword.mp3");
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {

            EnemyStateComponent state = sm.get(entity);

            if (state.state != EnemyStateComponent.STATES.CHASE) continue; // if not chasing, return

            // get reference to player
            ImmutableArray<Entity> players = getEngine().getEntitiesFor(playerFamily);
            Entity player = players.first();

            // check for player existance
            if (player == null) {
                state.state = EnemyStateComponent.STATES.IDLE; // if no player then just return
                return;
            }

            // enemy info
            EnemyChaseComponent chase = cm.get(entity);
            VelocityComponent vel = vm.get(entity);
            GeneralStatsComponent stat = gsm.get(entity);

            // Get important datas
            TransformComponent transform = tm.get(entity);
            EnemyBehaviourComponent behaviour = bm.get(entity);
            TransformComponent playerTransform = player.getComponent(TransformComponent.class);
            InputComponent enemyInput = entity.getComponent(InputComponent.class);

            //animation stuffs
            Sprite sprite = entity.getComponent(SpriteComponent.class).sprite;

            AnimationComponent anim = entity.getComponent(AnimationComponent.class);
            AnimationComponent.playAnimation(anim, "run", true);

            // Get the difference
            float dx = playerTransform.position.x - transform.position.x;
            float dy = playerTransform.position.y - transform.position.y;

            // Create the direction vector and normalize it
            Vector2 direction = new Vector2(dx, dy).nor().clamp(0, 1);  // 'nor' makes it unit length (length = 1)

            boolean shouldFaceLeft = direction.x < 0;
            if (sprite.isFlipX() != shouldFaceLeft) {
                sprite.flip(true, false);
            }

            for (Entity weapon : weaponEntities) {
                ParentComponent parent = pm.get(weapon);
                if (parent.parent == entity) {
                    WeaponComponent wc = wm.get(weapon);
                    wc.target.set(playerTransform.position.cpy());
                    break; // if only 1 weapon per enemy
                }
            }


            //if were winding up
            if (chase.windup) {
                Vector2 toEnemy = new Vector2(transform.position).sub(playerTransform.position).nor();

                // Step 2: Randomize direction slightly (±10 degrees for example)
                toEnemy.setAngleDeg(toEnemy.angleDeg() + chase.windupDirection).nor();

                vel.velocity.set(toEnemy).scl(stat.speed);

                //vel.velocity.set(chase.windupDirection).scl(stat.speed);

                chase.windupDuration -= deltaTime;

                if (chase.windupDuration < 0) {
                    entity.remove(EnemyChaseComponent.class);
                    entity.add(new EnemyChaseComponent(behaviour.coolDown));
                }
                return;
            }

            vel.velocity.set(direction).scl(stat.dash);

            float distance = transform.position.dst(playerTransform.position);

            if (distance < behaviour.attackRange) {
                float spawnDistance = 100f;
                float spawnX = transform.position.x + direction.x * spawnDistance;
                float spawnY = transform.position.y + direction.y * spawnDistance;
                Texture bulletTex = manager.get("particles/slash.png", Texture.class);
                ProjectileEntity slash = new ProjectileEntity(spawnX, spawnY, direction, bulletTex, 5f, true, 0.2f, Team.ENEMY, manager);
                getEngine().addEntity(slash);
                AudioHelper.playSound("sword");
                
                chase.windup = true;

                // Create a new direction vector from the new angle
                chase.windupDirection = MathUtils.random(-60f, 60f);
            }
        }
    }
}

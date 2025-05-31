package studio.jawa.bullettrain.systems.gameplays.enemies;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplays.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyChaseComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.components.technicals.InputComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class EnemyChaseSystem extends EntitySystem {
    private final ComponentMapper<EnemyStateComponent> sm = ComponentMapper.getFor(EnemyStateComponent.class);
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<EnemyBehaviourComponent> bm = ComponentMapper.getFor(EnemyBehaviourComponent.class);
    private final ComponentMapper<EnemyChaseComponent> cm = ComponentMapper.getFor(EnemyChaseComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<GeneralStatsComponent> gsm = ComponentMapper.getFor(GeneralStatsComponent.class);

    private Family playerFamily = Family.all(TransformComponent.class, PlayerControlledComponent.class).get();

    private ImmutableArray<Entity> entities;
    public EnemyChaseSystem(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(EnemyComponent.class, EnemyStateComponent.class, EnemyBehaviourComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {

            EnemyStateComponent state = sm.get(entity);

            if (state.state != EnemyStateComponent.STATES.CHASE) return; // if not chasing, return

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

            // Get the difference
            float dx = playerTransform.position.x - transform.position.x;
            float dy = playerTransform.position.y - transform.position.y;

            // Create the direction vector and normalize it
            Vector2 direction = new Vector2(dx, dy).nor().clamp(0, 1);  // 'nor' makes it unit length (length = 1)

            vel.velocity.set(direction).scl(stat.dash);

            float distance = transform.position.dst(playerTransform.position);

            if (distance < behaviour.attackRange) {
                System.out.println("ATTACK");
                chase.windup = true;

                // Create a new direction vector from the new angle
                chase.windupDirection = MathUtils.random(-60f, 60f);
            }



        }
    }
}

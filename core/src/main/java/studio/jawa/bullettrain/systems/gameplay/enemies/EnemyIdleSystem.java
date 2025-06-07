package studio.jawa.bullettrain.systems.gameplay.enemies;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import studio.jawa.bullettrain.components.gameplay.enemies.*;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class EnemyIdleSystem extends EntitySystem {
    private final ComponentMapper<EnemyStateComponent> sm = ComponentMapper.getFor(EnemyStateComponent.class);
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<EnemyBehaviourComponent> bm = ComponentMapper.getFor(EnemyBehaviourComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    private Family playerFamily = Family.all(TransformComponent.class, PlayerControlledComponent.class).get();
    private ImmutableArray<Entity> entities;

    public EnemyIdleSystem(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(EnemyComponent.class, EnemyStateComponent.class, EnemyBehaviourComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            EnemyStateComponent state = sm.get(entity);

            if (state.state != EnemyStateComponent.STATES.IDLE) continue;

            // get important data
            ImmutableArray<Entity> players = getEngine().getEntitiesFor(playerFamily);
            TransformComponent transform = tm.get(entity);
            EnemyBehaviourComponent behaviour = bm.get(entity);
            VelocityComponent vel = vm.get(entity);
            vel.velocity.set(0, 0);




            // the distance untill an enemy is aggroed
            float threshold = behaviour.aggroRange;

            for (Entity player : players) {
                // player data
                TransformComponent playerTransform = player.getComponent(TransformComponent.class);
                float distance = transform.position.dst(playerTransform.position);

                // player walks too close
                if (distance < threshold) {
                    if (behaviour.preferDistance) {
                        state.state = EnemyStateComponent.STATES.STRAFE;
                        entity.add(new EnemyStrafeComponent(behaviour.coolDown));
                    } else {
                        state.state = EnemyStateComponent.STATES.CHASE;
                        entity.add(new EnemyChaseComponent(behaviour.coolDown));
                    }
                }
            }

        }
    }
}

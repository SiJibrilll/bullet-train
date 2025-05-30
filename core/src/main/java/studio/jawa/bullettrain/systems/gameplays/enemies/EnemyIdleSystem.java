package studio.jawa.bullettrain.systems.gameplays.enemies;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;

import studio.jawa.bullettrain.components.gameplays.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;

public class EnemyIdleSystem extends EntitySystem {
    private final ComponentMapper<EnemyStateComponent> sm = ComponentMapper.getFor(EnemyStateComponent.class);
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);

    private Family playerFamily = Family.all(TransformComponent.class, PlayerControlledComponent.class).get();

    private ImmutableArray<Entity> entities;

    public EnemyIdleSystem(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(EnemyComponent.class, EnemyStateComponent.class, EnemyBehaviourComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            EnemyStateComponent state = sm.get(entity);

            if (state.state != EnemyStateComponent.STATES.IDLE) return;

            ImmutableArray<Entity> players = getEngine().getEntitiesFor(playerFamily);

            TransformComponent transform = tm.get(entity);

            float threshold = 200f;

            for (Entity player : players) {
                float distance = transform.position.dst(player.getComponent(TransformComponent.class).position);

                if (distance < threshold) {
                    state.state = EnemyStateComponent.STATES.CHASE;
                }
            }

        }
    }
}

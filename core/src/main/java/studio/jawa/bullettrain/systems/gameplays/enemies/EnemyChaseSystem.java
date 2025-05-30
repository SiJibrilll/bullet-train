package studio.jawa.bullettrain.systems.gameplays.enemies;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.components.technicals.InputComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;

public class EnemyChaseSystem extends EntitySystem {
    private final ComponentMapper<EnemyStateComponent> sm = ComponentMapper.getFor(EnemyStateComponent.class);
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);

    private Family playerFamily = Family.all(TransformComponent.class, PlayerControlledComponent.class).get();

    private ImmutableArray<Entity> entities;
    public EnemyChaseSystem(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(EnemyComponent.class, EnemyStateComponent.class, EnemyBehaviourComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            EnemyStateComponent state = sm.get(entity);

            if (state.state != EnemyStateComponent.STATES.CHASE) return;

            ImmutableArray<Entity> players = getEngine().getEntitiesFor(playerFamily);

            TransformComponent transform = tm.get(entity);


            for (Entity player : players) {
                TransformComponent playerTransform = player.getComponent(TransformComponent.class);
                InputComponent enemyInput = entity.getComponent(InputComponent.class);
                // Get the difference
                float dx = playerTransform.position.x - transform.position.x;
                float dy = playerTransform.position.y - transform.position.y;

                // Create the direction vector and normalize it
                Vector2 direction = new Vector2(dx, dy).nor();  // 'nor' makes it unit length (length = 1)

                System.out.println(direction);
                enemyInput.direction.x = direction.x;
                enemyInput.direction.y = direction.y;

                enemyInput.direction.clamp(0, 1); // Optional normalization
            }

        }
    }
}

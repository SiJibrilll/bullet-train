package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.technicals.InputComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyComponent;
import studio.jawa.bullettrain.data.GameConstants;


public class MovementSystem extends EntitySystem {
    private final ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<InputComponent> im = ComponentMapper.getFor(InputComponent.class);
    private final ComponentMapper<GeneralStatsComponent> sm = ComponentMapper.getFor(GeneralStatsComponent.class);
    private final ComponentMapper<EnemyComponent> em = ComponentMapper.getFor(EnemyComponent.class);

    private ImmutableArray<Entity> entities;

    public MovementSystem(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(TransformComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            TransformComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);
            GeneralStatsComponent stat = sm.get(entity);



            if (im.has(entity)) {
                InputComponent input = im.get(entity);
                vel.velocity.set(input.direction).scl(stat.speed); 
            }


            // Apply movement + collision logic here
            Vector2 oldPos = new Vector2(pos.position);
            pos.position.add(vel.velocity.cpy().scl(deltaTime));

            // Keep enemies within their carriage bounds
            if (em.has(entity)) {
                constrainEnemyToCarriage(entity, pos);
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
}

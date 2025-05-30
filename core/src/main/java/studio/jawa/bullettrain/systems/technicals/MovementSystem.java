package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.technicals.InputComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class MovementSystem extends EntitySystem {
    private final ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<InputComponent> im = ComponentMapper.getFor(InputComponent.class);

    private ImmutableArray<Entity> entities;

    public MovementSystem(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(TransformComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            TransformComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);

            if (im.has(entity)) {
                InputComponent input = im.get(entity);
                vel.velocity.set(input.direction).scl(500); // for now speed will be hardcoded
            }

            // Apply movement + collision logic here
            Vector2 oldPos = new Vector2(pos.position);
            pos.position.add(vel.velocity.cpy().scl(deltaTime));

            // Call your collision resolver here, e.g.:
            // resolveCollisions(pos, vel);
        }
    }
}

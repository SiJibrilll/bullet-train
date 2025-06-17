package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import studio.jawa.bullettrain.components.gameplay.DamageComponent;
import studio.jawa.bullettrain.components.gameplay.DeathComponent;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class DeathDragSystem extends EntitySystem{
     private ImmutableArray<Entity> entities;
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
            DeathComponent.class
        ).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            VelocityComponent velocity = vm.get(entity);

            // System.out.println(velocity.velocity);

            velocity.velocity.scl(0.95f); // Damping
            if (velocity.velocity.len2() < 0.01f) {
                velocity.velocity.setZero();
                // Optionally remove entity after it's done moving
                // getEngine().removeEntity(entity);
            }
        }   
    }
}

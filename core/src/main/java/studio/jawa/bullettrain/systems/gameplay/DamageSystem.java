package studio.jawa.bullettrain.systems.gameplay;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import studio.jawa.bullettrain.components.gameplay.DamageComponent;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;


public class DamageSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private final ComponentMapper<DamageComponent> dm = ComponentMapper.getFor(DamageComponent.class);
    private final ComponentMapper<GeneralStatsComponent> gm = ComponentMapper.getFor(GeneralStatsComponent.class);

    
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
            DamageComponent.class, GeneralStatsComponent.class
        ).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
                GeneralStatsComponent stats = gm.get(entity);
                DamageComponent damage = dm.get(entity);
                System.out.println(entity);
                stats.health -= damage.damage;
                System.out.println(stats.health);
                entity.remove(DamageComponent.class);
        }   
            
    }
}

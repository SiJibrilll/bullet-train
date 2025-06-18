package studio.jawa.bullettrain.systems.gameplay;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import studio.jawa.bullettrain.components.gameplay.DamageComponent;
import studio.jawa.bullettrain.components.gameplay.DeathComponent;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;


public class DamageSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private final ComponentMapper<DamageComponent> dm = ComponentMapper.getFor(DamageComponent.class);
    private final ComponentMapper<GeneralStatsComponent> gm = ComponentMapper.getFor(GeneralStatsComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    
    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
            DamageComponent.class, GeneralStatsComponent.class
        ).exclude(DeathComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
                GeneralStatsComponent stats = gm.get(entity);
                DamageComponent damage = dm.get(entity);
                stats.health -= damage.damage;
                entity.remove(DamageComponent.class);

                
            if (stats.health <= 0) {
                entity.add(new DeathComponent());
                VelocityComponent velocity = vm.get(entity);
                AnimationComponent anim = entity.getComponent(AnimationComponent.class);

                anim.currentAnimation = "death";
                anim.looping = false;
                anim.stateTime = 0f;
                anim.isPlaying = true;
                velocity.velocity.set(damage.direction).scl(1000f);
            }
        }   
            
    }
}

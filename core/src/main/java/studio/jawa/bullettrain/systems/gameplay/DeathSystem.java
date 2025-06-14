package studio.jawa.bullettrain.systems.gameplay;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

import studio.jawa.bullettrain.components.gameplay.DamageComponent;
import studio.jawa.bullettrain.components.gameplay.DeathComponent;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyComponent;
import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class DeathSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private final ComponentMapper<GeneralStatsComponent> gm = ComponentMapper.getFor(GeneralStatsComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<DamageComponent> dm = ComponentMapper.getFor(DamageComponent.class);
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);


    private final AssetManager manager;
    public DeathSystem(AssetManager manager) {
        this.manager = manager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
             GeneralStatsComponent.class,
             DamageComponent.class
        ).exclude(DeathComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            GeneralStatsComponent stats = gm.get(entity);
            
            if (stats.health <= 0) {
                entity.add(new DeathComponent());
                VelocityComponent velocity = vm.get(entity);
                DamageComponent damage = dm.get(entity);
                AnimationComponent anim = entity.getComponent(AnimationComponent.class);

               
                anim.currentAnimation = "death";
                anim.looping = false;
                anim.stateTime = 0f;
                anim.isPlaying = true;
                velocity.velocity = new Vector2(damage.direction).scl(1000f);
            }
        }   
    }
}

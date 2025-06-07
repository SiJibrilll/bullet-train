package studio.jawa.bullettrain.systems.effects;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import studio.jawa.bullettrain.components.effects.HitFlashComponent;

public class HitFlashSystem extends IteratingSystem {

    private ComponentMapper<HitFlashComponent> hfm = ComponentMapper.getFor(HitFlashComponent.class);

    public HitFlashSystem() {
        super(Family.all(HitFlashComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        HitFlashComponent flash = hfm.get(entity);

        if (flash.active) {

            flash.timer -= deltaTime;
            if (flash.timer <= 0f) {
                flash.active = false;
                flash.timer = 0f;

                entity.remove(HitFlashComponent.class);
            }
        }
    }
}


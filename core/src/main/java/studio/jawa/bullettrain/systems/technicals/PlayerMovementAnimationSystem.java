package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import studio.jawa.bullettrain.components.gameplay.DeathComponent;
import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class PlayerMovementAnimationSystem extends IteratingSystem {
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    public PlayerMovementAnimationSystem() {
        super(Family.all(PlayerControlledComponent.class, VelocityComponent.class, AnimationComponent.class).exclude(DeathComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        VelocityComponent velocity = vm.get(entity);
        AnimationComponent anim = am.get(entity);

        boolean isMoving = !velocity.velocity.isZero(0.1f); // Threshold to avoid float jitter

        String newAnimation = isMoving ? "run" : "idle";

        // Only switch if different
        if (!anim.currentAnimation.equals(newAnimation)) {
            anim.currentAnimation = newAnimation;
            anim.stateTime = 0f;
            anim.looping = true;
            anim.isPlaying = true;
        }
    }
}

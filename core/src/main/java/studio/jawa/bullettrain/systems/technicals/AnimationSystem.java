package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;

public class AnimationSystem extends IteratingSystem {
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);

    private boolean isPaused = false;

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }
    
    public AnimationSystem() {
        super(Family.all(AnimationComponent.class, SpriteComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (isPaused) deltaTime = 0f;
        if (isPaused) return;

        AnimationComponent anim = am.get(entity);
        SpriteComponent spriteComp = sm.get(entity);

        Animation<TextureRegion> animation = anim.animations.get(anim.currentAnimation);
        if (animation == null) return;

        // Advance time only if still playing
        if (anim.isPlaying) {
            anim.stateTime += deltaTime;

            if (!anim.looping && animation.isAnimationFinished(anim.stateTime)) {
                anim.isPlaying = false; // stop further updates
            }
        }

        TextureRegion frame = animation.getKeyFrame(anim.stateTime, anim.looping);
        anim.currentFrame = frame;
        spriteComp.sprite.setRegion(frame);
        spriteComp.sprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
    }
}


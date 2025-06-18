package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import studio.jawa.bullettrain.components.gameplay.DeathComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;

public class PlayerFacingSystem extends IteratingSystem {
    private final Camera camera;

    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);

    public PlayerFacingSystem(Camera camera) {
        super(Family.all(PlayerControlledComponent.class, TransformComponent.class, SpriteComponent.class).exclude(DeathComponent.class).get());
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = tm.get(entity);
        SpriteComponent spriteComp = sm.get(entity);
        Sprite sprite = spriteComp.sprite;

        // 1. Get mouse position in world space
        Vector3 mouseScreen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 mouseWorld = camera.unproject(mouseScreen);

        // 2. Compare mouse.x to player.x
        float dirX = mouseWorld.x - transform.position.x;

        // 3. Determine flip
        boolean shouldFaceLeft = dirX < 0;
        boolean isFlipped = sprite.isFlipX();

        // 4. Flip if needed
        if (shouldFaceLeft != isFlipped) {
            sprite.flip(true, false);
        }
    }
}


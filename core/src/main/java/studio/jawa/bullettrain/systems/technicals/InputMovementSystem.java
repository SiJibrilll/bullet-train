package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.components.technicals.InputComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;

public class InputMovementSystem extends EntitySystem {
    private final ComponentMapper<InputComponent> im = ComponentMapper.getFor(InputComponent.class);
    private final ComponentMapper<PlayerControlledComponent> pcm = ComponentMapper.getFor(PlayerControlledComponent.class);

    private ImmutableArray<Entity> entities;

    public InputMovementSystem(Engine engine) {
        this.entities = engine.getEntitiesFor(Family.all(InputComponent.class, PlayerControlledComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            
            AnimationComponent anim = entity.getComponent(AnimationComponent.class);
            AnimationComponent.playAnimation(anim, "run", true);

            input.direction.set(0, 0);
            if (Gdx.input.isKeyPressed(Input.Keys.W)) input.direction.y += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) input.direction.y -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) input.direction.x -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) input.direction.x += 1;

            input.direction.clamp(0, 1); 
        }
    }
}

package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import studio.jawa.bullettrain.components.gameplay.players.PlayerComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;

public class CameraSystem extends IteratingSystem {

    private OrthographicCamera camera;
    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<PlayerComponent> playerMapper;

    public CameraSystem(OrthographicCamera camera) {
        super(Family.all(PlayerComponent.class, TransformComponent.class).get());
        this.camera = camera;
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TransformComponent transform = transformMapper.get(entity);

        if (transform != null) {
            // Smooth camera following
            float targetX = transform.position.x;
            float targetY = transform.position.y;

            camera.position.x = MathUtils.lerp(camera.position.x, targetX, 2f * deltaTime);
            camera.position.y = MathUtils.lerp(camera.position.y, targetY, 2f * deltaTime);

            camera.update();
        }
    }
}

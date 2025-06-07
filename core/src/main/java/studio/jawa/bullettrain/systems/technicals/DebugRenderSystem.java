package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import studio.jawa.bullettrain.components.technicals.BoxColliderComponent;
import studio.jawa.bullettrain.components.technicals.CircleColliderComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;

public class DebugRenderSystem extends EntitySystem {
    private final ShapeRenderer shapeRenderer;
    private final Camera camera;

    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<BoxColliderComponent> rectCm = ComponentMapper.getFor(BoxColliderComponent.class);
    private final ComponentMapper<CircleColliderComponent> circleCm = ComponentMapper.getFor(CircleColliderComponent.class);

    private ImmutableArray<Entity> entities;

    public DebugRenderSystem(Camera camera, Engine engine) {
        this.shapeRenderer = new ShapeRenderer();
        this.camera = camera;

        // Match entities that have Transform + any kind of collider
        this.entities = engine.getEntitiesFor(
            Family.all(TransformComponent.class)
                .one(BoxColliderComponent.class, CircleColliderComponent.class)
                .get()
        );
    }

    @Override
    public void update(float deltaTime) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (Entity entity : entities) {
            TransformComponent t = tm.get(entity);

            if (rectCm.has(entity)) {
                BoxColliderComponent rectCollider = rectCm.get(entity);
                Rectangle worldRect = new Rectangle(
                    t.position.x + rectCollider.bounds.x,
                    t.position.y + rectCollider.bounds.y,
                    rectCollider.bounds.width,
                    rectCollider.bounds.height
                );
                shapeRenderer.rect(
                    worldRect.x,
                    worldRect.y,
                    worldRect.width,
                    worldRect.height
                );
            }

            if (circleCm.has(entity)) {
                CircleColliderComponent circleCollider = circleCm.get(entity);
                Circle c = circleCollider.circle;
                float worldX = t.position.x + c.x;
                float worldY = t.position.y + c.y;
                shapeRenderer.circle(worldX, worldY, c.radius);
            }
        }

        shapeRenderer.end();
    }
}



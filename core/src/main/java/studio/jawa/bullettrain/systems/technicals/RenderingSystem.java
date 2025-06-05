package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.ashley.core.*;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;


public class RenderingSystem extends EntitySystem {
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private ImmutableArray<Entity> entities;

    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);

    public RenderingSystem(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(TransformComponent.class, SpriteComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);

            TransformComponent transform = tm.get(entity);
            SpriteComponent spriteComp = sm.get(entity);
            Sprite sprite = spriteComp.sprite;

            // Apply anchor
            float width = sprite.getWidth();
            float height = sprite.getHeight();

            float originX = width * transform.origin.x;
            float originY = height * transform.origin.y;

            sprite.setOrigin(originX, originY);
            sprite.setPosition(
                transform.position.x - originX,
                transform.position.y - originY
            );
            sprite.setRotation(transform.rotation);
            sprite.setScale(transform.scale);

            sprite.draw(batch);
        }

        batch.end();
    }

    @Override
    public void removedFromEngine(Engine engine) {
        // Clean up if needed
    }
}

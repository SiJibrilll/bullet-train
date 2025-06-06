package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.ashley.core.*;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.level.BaseObjectComponent;
import studio.jawa.bullettrain.data.GameConstants;


public class RenderingSystem extends EntitySystem {
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private ImmutableArray<Entity> entities;

    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<BaseObjectComponent> om = ComponentMapper.getFor(BaseObjectComponent.class);

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
            
            if (transform == null || spriteComp == null) continue;
            
            Sprite sprite = spriteComp.sprite;

            sprite.setPosition(
                transform.position.x - sprite.getWidth()/2f, 
                transform.position.y - sprite.getHeight()/2f
            );
            sprite.setRotation(transform.rotation);
            sprite.setScale(transform.scale);

            sprite.draw(batch);
        }

        batch.end();
    }

    @Override
    public void removedFromEngine(Engine engine) {
    }
}

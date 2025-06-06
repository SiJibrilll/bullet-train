package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import studio.jawa.bullettrain.components.effects.HitFlashComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;

public class HitFlashRenderSystem extends EntitySystem {
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Texture flash;

    public HitFlashRenderSystem(OrthographicCamera camera, SpriteBatch sharedBatch, Texture flash) {
        this.camera = camera;
        this.batch = sharedBatch;
        this.flash = flash;
    }

    public void render() {


        for (Entity entity : getEngine().getEntitiesFor(Family.all(SpriteComponent.class, HitFlashComponent.class).get())) {
            Sprite sprite = entity.getComponent(SpriteComponent.class).sprite;

            Color original = sprite.getColor();
            sprite.setColor(Color.RED); // Tint completely white
            sprite.draw(batch);
            sprite.setColor(original);
        }
    }

    @Override
    public void update(float deltaTime) {
        // Logic-only updates (e.g., reduce flash time, remove component when done)
    }
}






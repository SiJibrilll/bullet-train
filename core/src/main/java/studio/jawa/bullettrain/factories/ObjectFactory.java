package studio.jawa.bullettrain.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.level.BaseObjectComponent;
import studio.jawa.bullettrain.components.technicals.BoxColliderComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.ObjectType;

public class ObjectFactory {

    public static Entity createObject(ObjectType objectType, Vector2 position, int carriageNumber, Texture texture) {
        Entity object = new Entity();

        // Transform component
        object.add(new TransformComponent(position.x, position.y));

        // Base object component
        BaseObjectComponent objectComp = new BaseObjectComponent(objectType, carriageNumber);
        // Set bounds 
        objectComp.bounds.set(-objectType.width/2f, -objectType.height/2f, objectType.width, objectType.height);
        object.add(objectComp);

        // BoxColliderComponent
        object.add(new BoxColliderComponent(new Rectangle(objectComp.bounds)));

        // Sprite component
        if (texture != null) {
            Sprite sprite = new Sprite(texture);
            sprite.setSize(objectType.width, objectType.height);
            sprite.setOriginCenter();
            sprite.setColor(1f, 1f, 1f, 1f);
            object.add(new SpriteComponent(sprite));
        }

        return object;
    }

    public static Entity createRandomObject(Vector2 position, int carriageNumber, Texture texture) {
        ObjectType randomType = ObjectType.getRandomType();
        return createObject(randomType, position, carriageNumber, texture);
    }
}

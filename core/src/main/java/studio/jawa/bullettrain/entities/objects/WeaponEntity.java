package studio.jawa.bullettrain.entities.objects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import studio.jawa.bullettrain.components.technicals.ParentComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.WeaponComponent;

public class WeaponEntity extends Entity {
    public WeaponEntity(float x, float y, Texture tex, boolean ownedByPlayer, Entity owner, float scale) {
        TransformComponent transform = new TransformComponent(x, y);
        transform.scale = scale;
        add(transform);
        add(new SpriteComponent(new Sprite(tex)));
        WeaponComponent wc = new WeaponComponent();
        wc.isPlayerWeapon = ownedByPlayer;
        add(wc);

        ParentComponent parent = new ParentComponent();
        parent.parent = owner;
        parent.distanceFromParent = 24f;
        add(parent);
    }

    public WeaponEntity(float x, float y, Texture tex, boolean ownedByPlayer, Entity owner, float scale, float distance) {
        TransformComponent transform = new TransformComponent(x, y);
        transform.scale = scale;
        add(transform);
        add(new SpriteComponent(new Sprite(tex)));
        WeaponComponent wc = new WeaponComponent();
        wc.isPlayerWeapon = ownedByPlayer;
        add(wc);

        ParentComponent parent = new ParentComponent();
        parent.parent = owner;
        parent.distanceFromParent = distance;
        add(parent);
    }
}

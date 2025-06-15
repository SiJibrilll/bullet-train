package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import studio.jawa.bullettrain.components.technicals.ParentComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.WeaponComponent;

public class WeaponOrbitSystem extends IteratingSystem {
    private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<ParentComponent> pm = ComponentMapper.getFor(ParentComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);


    private final Camera camera;
    private final Vector2 tmp = new Vector2();

    public WeaponOrbitSystem(Camera camera) {
        super(Family.all(WeaponComponent.class, ParentComponent.class, TransformComponent.class).get());
        this.camera = camera;
    }

    @Override
    protected void processEntity(Entity weapon, float deltaTime) {
        TransformComponent weaponTransform = tm.get(weapon);
        ParentComponent parentComp = pm.get(weapon);
        WeaponComponent weaponComp = wm.get(weapon);
        

        if (parentComp.parent == null || !tm.has(parentComp.parent)) return;

        TransformComponent parentTransform = tm.get(parentComp.parent);

        // Determine target point
        Vector2 aimTarget = tmp;
        if (weaponComp.isPlayerWeapon) {
            Vector3 mouseScreen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mouseScreen);
            aimTarget.set(mouseScreen.x, mouseScreen.y);
        } else {
            if (weaponComp.target == null) return;
            aimTarget.set(weaponComp.target);
        }

        // Compute angle between parent and target
        float angleToTarget = aimTarget.cpy().sub(parentTransform.position).angleDeg();

        // Place weapon at distanceFromParent along that angle
        float rad = (float) Math.toRadians(angleToTarget);
        float dx = (float) Math.cos(rad) * parentComp.distanceFromParent;
        float dy = (float) Math.sin(rad) * parentComp.distanceFromParent;

        weaponTransform.position.set(parentTransform.position.x + dx, parentTransform.position.y + dy);
        weaponTransform.rotation = angleToTarget;

        if (!sm.has(weapon)) return;

        Sprite sprite = sm.get(weapon).sprite;

        // Flip horizontally when aiming left (90–270 degrees)
        if (weaponTransform.rotation > 90f && weaponTransform.rotation < 270f) {
            if (!sprite.isFlipY()) {
                sprite.flip(false, true); // or flip(true, false) depending on your art
            }
        } else {
            if (sprite.isFlipY()) {
                sprite.flip(false, true); // unflip it
            }
        }
    }
}


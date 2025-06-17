package studio.jawa.bullettrain.entities.Projectiles;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.*;

public class ProjectileEntity extends Entity {
    public  ProjectileEntity(float x, float y, Vector2 direction, Texture tex, float speed, float scale, ProjectileComponent.Team team, float damage) {
        TransformComponent transform = new TransformComponent(x, y);
        transform.rotation = direction.angleDeg();
        transform.origin.set(0.5f, 0.5f);
        transform.scale = scale; // Adjust size
        add(transform);

        VelocityComponent vel = new VelocityComponent();
        vel.velocity.x = direction.x * speed;
        vel.velocity.y = direction.y * speed;
        add(vel);

        ProjectileComponent bullet = new ProjectileComponent(speed, team);
        bullet.damage = damage;

        add(new SpriteComponent(new Sprite(tex)));
        add(bullet);
        add(new CircleColliderComponent(0f, 0f, 40f));
    }

    public  ProjectileEntity(float x, float y, Vector2 direction, Texture tex, float scale, boolean isMelee, float meleeDuration, ProjectileComponent.Team team, AssetManager assetManager, float damage) {
        TransformComponent transform = new TransformComponent(x, y);
        transform.rotation = direction.angleDeg() - 90f;
        transform.origin.set(0.5f, 0.5f);
        transform.scale = scale; // Adjust size
        add(transform);

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put("slash", AnimationComponent.loadAnimation(assetManager, "particles/Melee_Slash.png", 7, 0.01f, false));
        add(anim);

        add(new SpriteComponent(new Sprite(tex)));
        ProjectileComponent slash = new ProjectileComponent(team, isMelee, meleeDuration);
        slash.damage = damage;
        add(slash);
        add(new CircleColliderComponent(0f, 0f, 80f));
    }
}

package studio.jawa.bullettrain.entities.Projectiles;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplays.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.*;

public class ProjectileEntity extends Entity {
    public  ProjectileEntity(float x, float y, Vector2 direction, Texture tex, float speed) {
        TransformComponent transform = new TransformComponent(x, y);
        transform.rotation = direction.angleDeg();
        transform.origin.set(0f, 0.5f);
        add(transform);

        VelocityComponent vel = new VelocityComponent();
        vel.velocity.x = direction.x * speed;
        vel.velocity.y = direction.y * speed;
        add(vel);

        add(new SpriteComponent(new Sprite(tex)));

    }

    public  ProjectileEntity(float x, float y, Vector2 direction, Texture tex, float speed, float scale, ProjectileComponent.Team team) {
        TransformComponent transform = new TransformComponent(x, y);
        transform.rotation = direction.angleDeg();
        transform.origin.set(0.5f, 0.5f);
        transform.scale = 0.5f; // Adjust size
        add(transform);

        VelocityComponent vel = new VelocityComponent();
        vel.velocity.x = direction.x * speed;
        vel.velocity.y = direction.y * speed;
        add(vel);

        add(new SpriteComponent(new Sprite(tex)));
        add(new ProjectileComponent(speed, team));
        add(new CircleColliderComponent(0f, 0f, 40f));
    }

    public  ProjectileEntity(float x, float y, Vector2 direction, Texture tex, float scale, boolean isMelee, float meleeDuration, ProjectileComponent.Team team) {
        TransformComponent transform = new TransformComponent(x, y);
        transform.rotation = direction.angleDeg();
        transform.origin.set(0.5f, 0.5f);
        transform.scale = 0.5f; // Adjust size
        add(transform);


        add(new SpriteComponent(new Sprite(tex)));
        add(new ProjectileComponent(team, isMelee, meleeDuration));
        add(new CircleColliderComponent(0f, 0f, 40f));
    }
}

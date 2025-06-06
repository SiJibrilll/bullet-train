package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import studio.jawa.bullettrain.components.effects.HitFlashComponent;
import studio.jawa.bullettrain.components.gameplays.TeamComponent;
import studio.jawa.bullettrain.components.gameplays.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.BoxColliderComponent;
import studio.jawa.bullettrain.components.technicals.CircleColliderComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;

public class ProjectileCollisionSystem extends EntitySystem {
    private final ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class);
    private final ComponentMapper<CircleColliderComponent> circleCm = ComponentMapper.getFor(CircleColliderComponent.class);
    private final ComponentMapper<BoxColliderComponent> rectCm = ComponentMapper.getFor(BoxColliderComponent.class);
    private final ComponentMapper<ProjectileComponent> bulletCm = ComponentMapper.getFor(ProjectileComponent.class);

    private final ImmutableArray<Entity> bullets;
    private final ImmutableArray<Entity> targets;

    public ProjectileCollisionSystem(Engine engine) {
        bullets = engine.getEntitiesFor(
            Family.all(ProjectileComponent.class, TransformComponent.class, CircleColliderComponent.class).get()
        );

        targets = engine.getEntitiesFor(
            Family.all(TransformComponent.class)
                .one(CircleColliderComponent.class, BoxColliderComponent.class)
                .exclude(ProjectileComponent.class) // exclude bullets if needed
                .get()
        );
    }

    @Override
    public void update(float deltaTime) {
        for (Entity bullet : bullets) {
            TransformComponent bt = tm.get(bullet);
            CircleColliderComponent bc = circleCm.get(bullet);

            // Rotate bullet's local offset
            float angle = MathUtils.degreesToRadians * bt.rotation;
            float rotatedX = bc.circle.x * MathUtils.cos(angle) - bc.circle.y * MathUtils.sin(angle);
            float rotatedY = bc.circle.x * MathUtils.sin(angle) + bc.circle.y * MathUtils.cos(angle);

            // World-space circle for the bullet
            Circle bulletCircle = new Circle(
                bt.position.x + rotatedX,
                bt.position.y + rotatedY,
                bc.circle.radius
            );

            for (Entity target : targets) {
                if (bullet == target) continue;

                TransformComponent tt = tm.get(target);

                // Check against circle colliders
                if (circleCm.has(target)) {
                    CircleColliderComponent tc = circleCm.get(target);
                    Circle targetCircle = new Circle(
                        tt.position.x + tc.circle.x,
                        tt.position.y + tc.circle.y,
                        tc.circle.radius
                    );

                    if (Intersector.overlaps(bulletCircle, targetCircle)) {
                        onHit(bullet, target);
                    }
                }

                // Check against rectangle colliders
                else if (rectCm.has(target)) {
                    BoxColliderComponent rc = rectCm.get(target);
                    Rectangle rect = new Rectangle(
                        tt.position.x + rc.bounds.x,
                        tt.position.y + rc.bounds.y,
                        rc.bounds.width,
                        rc.bounds.height
                    );

                    if (Intersector.overlaps(bulletCircle, rect)) {
                        onHit(bullet, target);
                    }
                }
            }
        }
    }

    private void onHit(Entity bullet, Entity target) {
        ProjectileComponent pc = bullet.getComponent(ProjectileComponent.class);
        if (pc == null) return;

        // 1. Don't hit your shooter
        if (pc.owner == target) return;

        // 2. Don't hit same team (friendly fire off)
        TeamComponent targetTeam = target.getComponent(TeamComponent.class);
        if (targetTeam != null && pc.team == targetTeam.team) return;

        // ✅ Passed team checks — apply hit
        System.out.println("Bullet hit enemy!");
        target.add(new HitFlashComponent(.15f));
        // Remove bullet from engine
        getEngine().removeEntity(bullet);


    }
}


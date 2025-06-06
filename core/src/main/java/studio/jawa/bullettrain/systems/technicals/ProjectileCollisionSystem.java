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
                // .exclude(ProjectileComponent.class) // exclude bullets if needed
                .get()
        );
    }

    @Override
    public void update(float deltaTime) {
        for (Entity bullet : bullets) {
            
            // get bullet's properties
            TransformComponent bt = tm.get(bullet);
            CircleColliderComponent bc = circleCm.get(bullet);

            // check if this is a melee attack
            ProjectileComponent bcm = bulletCm.get(bullet);
            if (bcm.isMeele) {
                
                if (bcm.meleeDuration <= 0) {   
                    getEngine().removeEntity(bullet);
                    continue;

                }
                bcm.meleeDuration -= deltaTime;

                meleeLogic(bc, bt, bullet);
                continue;
            } 

            // if not melee, then run bullet logic
            bulletLogic(bc, bt, bullet);
        }
    }

    private void bulletLogic(CircleColliderComponent bc, TransformComponent bt, Entity bullet) {
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

    private void meleeLogic(CircleColliderComponent mc, TransformComponent mt, Entity meleeEntity) {
        float angle = MathUtils.degreesToRadians * mt.rotation;
        float rotatedX = mc.circle.x * MathUtils.cos(angle) - mc.circle.y * MathUtils.sin(angle);
        float rotatedY = mc.circle.x * MathUtils.sin(angle) + mc.circle.y * MathUtils.cos(angle);

        Circle meleeCircle = new Circle(
            mt.position.x + rotatedX,
            mt.position.y + rotatedY,
            mc.circle.radius
        );

        for (Entity target : targets) {
            if (meleeEntity == target) continue;

            TransformComponent tt = tm.get(target);

            boolean hit = false;

            if (circleCm.has(target)) {
                CircleColliderComponent tc = circleCm.get(target);
                Circle targetCircle = new Circle(
                    tt.position.x + tc.circle.x,
                    tt.position.y + tc.circle.y,
                    tc.circle.radius
                );
                hit = Intersector.overlaps(meleeCircle, targetCircle);
            } else if (rectCm.has(target)) {
                BoxColliderComponent rc = rectCm.get(target);
                Rectangle rect = new Rectangle(
                    tt.position.x + rc.bounds.x,
                    tt.position.y + rc.bounds.y,
                    rc.bounds.width,
                    rc.bounds.height
                );
                hit = Intersector.overlaps(meleeCircle, rect);
            }

            if (hit) {
                onHit(meleeEntity, target);
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
        if (pc.isMeele) return;
        getEngine().removeEntity(bullet);


    }
}


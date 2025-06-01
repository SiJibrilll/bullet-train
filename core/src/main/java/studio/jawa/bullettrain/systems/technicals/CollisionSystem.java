package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;
import studio.jawa.bullettrain.components.gameplay.players.PlayerComponent;
import studio.jawa.bullettrain.components.level.BaseObjectComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.ObjectType;

public class CollisionSystem extends EntitySystem {

    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<BaseObjectComponent> objectMapper;

    private ImmutableArray<Entity> playerEntities;
    private ImmutableArray<Entity> objectEntities;

    private Rectangle playerBounds = new Rectangle();
    private Rectangle objectBounds = new Rectangle();

    public CollisionSystem() {
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        objectMapper = ComponentMapper.getFor(BaseObjectComponent.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        playerEntities = engine.getEntitiesFor(
            Family.all(PlayerComponent.class, TransformComponent.class).get()
        );

        objectEntities = engine.getEntitiesFor(
            Family.all(BaseObjectComponent.class, TransformComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        if (playerEntities.size() == 0) return;

        Entity player = playerEntities.get(0);
        TransformComponent playerTransform = transformMapper.get(player);
        PlayerComponent playerComp = playerMapper.get(player);

        if (playerTransform == null) return;

        // Player collision bounds 
        float playerSize = 15f;
        playerBounds.set(
            playerTransform.position.x - playerSize,
            playerTransform.position.y - playerSize,
            playerSize * 2,
            playerSize * 2
        );

        // Check collision with all objects in current carriage
        for (Entity object : objectEntities) {
            BaseObjectComponent objectComp = objectMapper.get(object);
            TransformComponent objectTransform = transformMapper.get(object);

            if (objectComp == null || objectTransform == null) continue;

            // Only check objects in same carriage
            if (objectComp.carriageNumber != playerComp.currentCarriageNumber) continue;

            // Object collision bounds
            objectBounds.set(objectComp.bounds);

            // Check collision
            if (playerBounds.overlaps(objectBounds)) {
                if (objectComp.isDestructible) {
                    // Destroy destructible objects (TNT)
                    destroyObject(object, objectComp);
                } else {
                    // Push player away from solid objects
                    resolveCollision(playerTransform, objectTransform, objectComp);
                }
            }
        }
    }

    private void destroyObject(Entity object, BaseObjectComponent objectComp) {
        // Remove object from engine
        getEngine().removeEntity(object);

        // Debug TNT destruction
        System.out.println("💥 TNT destroyed at (" + objectComp.bounds.x + ", " + objectComp.bounds.y + ")");

    }

    private void resolveCollision(TransformComponent playerTransform,
                                 TransformComponent objectTransform,
                                 BaseObjectComponent objectComp) {

        // Calculate overlap
        float playerCenterX = playerTransform.position.x;
        float playerCenterY = playerTransform.position.y;
        float objectCenterX = objectTransform.position.x;
        float objectCenterY = objectTransform.position.y;

        // Calculate push direction
        float deltaX = playerCenterX - objectCenterX;
        float deltaY = playerCenterY - objectCenterY;

        float overlapX = (playerBounds.width/2 + objectComp.bounds.width/2) - Math.abs(deltaX);
        float overlapY = (playerBounds.height/2 + objectComp.bounds.height/2) - Math.abs(deltaY);

        if (overlapX < overlapY) {
            // Push horizontally
            if (deltaX > 0) {
                playerTransform.position.x = objectCenterX + objectComp.bounds.width/2 + playerBounds.width/2;
            } else {
                playerTransform.position.x = objectCenterX - objectComp.bounds.width/2 - playerBounds.width/2;
            }
        } else {
            // Push vertically
            if (deltaY > 0) {
                playerTransform.position.y = objectCenterY + objectComp.bounds.height/2 + playerBounds.height/2;
            } else {
                playerTransform.position.y = objectCenterY - objectComp.bounds.height/2 - playerBounds.height/2;
            }
        }
    }
}

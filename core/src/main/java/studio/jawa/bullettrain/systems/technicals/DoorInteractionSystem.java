package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplay.InteractionComponent;
import studio.jawa.bullettrain.components.gameplay.PlayerComponent;
import studio.jawa.bullettrain.components.level.DoorComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.GameConstants;

public class DoorInteractionSystem extends EntitySystem {

    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<DoorComponent> doorMapper;
    private ComponentMapper<InteractionComponent> interactionMapper;

    private ImmutableArray<Entity> doorEntities;
    private ImmutableArray<Entity> playerEntities;

    private Entity nearbyDoor = null;
    private boolean showingPrompt = false;

    public DoorInteractionSystem() {
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        doorMapper = ComponentMapper.getFor(DoorComponent.class);
        interactionMapper = ComponentMapper.getFor(InteractionComponent.class);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        doorEntities = engine.getEntitiesFor(
            Family.all(DoorComponent.class, TransformComponent.class, InteractionComponent.class).get()
        );

        playerEntities = engine.getEntitiesFor(
            Family.all(PlayerComponent.class, TransformComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        if (playerEntities.size() == 0) return;

        Entity player = playerEntities.get(0);
        TransformComponent playerTransform = transformMapper.get(player);
        PlayerComponent playerComp = playerMapper.get(player);

        // Check proximity to doors
        checkDoorProximity(player, playerTransform);

        // Handle interaction input
        handleInteractionInput(player, playerComp);
    }

    private void checkDoorProximity(Entity player, TransformComponent playerTransform) {
        Entity closestDoor = null;
        float closestDistance = Float.MAX_VALUE;

        // Find nearest door within interaction range
        for (Entity door : doorEntities) {
            TransformComponent doorTransform = transformMapper.get(door);
            InteractionComponent interaction = interactionMapper.get(door);
            DoorComponent doorComp = doorMapper.get(door);

            if (!doorComp.isActive) continue; // Skip inactive doors

            // Calculate distance
            float distance = Vector2.dst(
                playerTransform.position.x, playerTransform.position.y,
                doorTransform.position.x, doorTransform.position.y
            );

            // Check if within interaction radius
            if (distance <= interaction.interactionRadius && distance < closestDistance) {
                closestDistance = distance;
                closestDoor = door;
            }
        }

        // Update door states
        updateDoorStates(closestDoor);
    }

    private void updateDoorStates(Entity closestDoor) {
        // Reset all doors
        for (Entity door : doorEntities) {
            DoorComponent doorComp = doorMapper.get(door);
            doorComp.isPlayerNearby = false;
        }

        // Update nearest door and show prompt
        if (closestDoor != null) {
            DoorComponent doorComp = doorMapper.get(closestDoor);
            InteractionComponent interaction = interactionMapper.get(closestDoor);

            doorComp.isPlayerNearby = true;
            nearbyDoor = closestDoor;

            // Show prompt (console untuk testing, nanti bisa jadi UI)
            if (!showingPrompt) {
                System.out.println("💡 " + interaction.promptMessage);
                showingPrompt = true;
            }
        } else {
            // Clear prompt
            if (showingPrompt) {
                System.out.println(""); // Clear line
                showingPrompt = false;
            }
            nearbyDoor = null;
        }
    }

    private void handleInteractionInput(Entity player, PlayerComponent playerComp) {
        // Check for E key press
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (nearbyDoor != null) {
                performDoorTransition(player, playerComp, nearbyDoor);
            }
        }
    }

    private void performDoorTransition(Entity player, PlayerComponent playerComp, Entity door) {
        DoorComponent doorComp = doorMapper.get(door);
        TransformComponent playerTransform = transformMapper.get(player);

        if (doorComp.doorType == DoorComponent.DoorType.EXIT_TO_NEXT) {
            // Transition to next carriage
            int nextCarriage = doorComp.targetCarriageNumber;

            // Teleport player to entry of next carriage
            teleportPlayerToCarriageEntry(playerTransform, nextCarriage);

            // Update player's current carriage
            playerComp.currentCarriageNumber = nextCarriage;

            System.out.println("🚪 Player entered Car " + nextCarriage);

        } else if (doorComp.doorType == DoorComponent.DoorType.ENTRY_FROM_PREV) {
            // This shouldn't normally happen (entry doors aren't interactive)
            // But could be used for going back to previous carriage
            System.out.println("🚪 Entry door - no action");
        }

        // Clear prompt after transition
        showingPrompt = false;
        nearbyDoor = null;
    }

    private void teleportPlayerToCarriageEntry(TransformComponent playerTransform, int targetCarriageNumber) {
        // Calculate target position (entry point of target carriage)
        float targetX = GameConstants.CARRIAGE_WIDTH / 2f; // Center horizontal
        float targetY = (targetCarriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT +
                       GameConstants.ENTRY_ZONE_HEIGHT + 50f; // Just above entry zone

        playerTransform.position.set(targetX, targetY);

        System.out.println("🔄 Player teleported to Car " + targetCarriageNumber +
                          " at (" + targetX + ", " + targetY + ")");
    }
}

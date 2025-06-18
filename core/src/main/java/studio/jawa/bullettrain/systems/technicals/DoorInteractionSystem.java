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
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;

import studio.jawa.bullettrain.components.level.DoorComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.GameConstants;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyComponent;

public class DoorInteractionSystem extends EntitySystem {

    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<DoorComponent> doorMapper;
    private ComponentMapper<InteractionComponent> interactionMapper;

    private ImmutableArray<Entity> doorEntities;
    private ImmutableArray<Entity> playerEntities;

    private Entity nearbyDoor = null;
    private boolean showingPrompt = false;

    private ImmutableArray<Entity> enemyEntities;

    private String currentDoorPrompt = null;
    private Vector2 currentDoorPromptPos = null;
    private boolean showDoorErrorPrompt = false;
    private String doorErrorPromptMessage = "";
    private float doorErrorPromptTimer = 0f;
    private static final float DOOR_ERROR_PROMPT_DURATION = 1.5f;

    private final ComponentMapper<EnemyComponent> enemyMapper = ComponentMapper.getFor(EnemyComponent.class);

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

        enemyEntities = engine.getEntitiesFor(
            Family.all(EnemyComponent.class).exclude(studio.jawa.bullettrain.components.gameplay.DeathComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        if (playerEntities.size() == 0) return;

        if (showDoorErrorPrompt) {
            doorErrorPromptTimer -= deltaTime;
            if (doorErrorPromptTimer <= 0f) {
                showDoorErrorPrompt = false;
                doorErrorPromptMessage = "";
            }
        }

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

            if (!doorComp.isActive) continue;

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
        for (Entity door : doorEntities) {
            DoorComponent doorComp = doorMapper.get(door);
            doorComp.isPlayerNearby = false;
        }

        currentDoorPrompt = null;
        currentDoorPromptPos = null;

        // Update nearest door and show prompt
        if (closestDoor != null) {
            DoorComponent doorComp = doorMapper.get(closestDoor);
            InteractionComponent interaction = interactionMapper.get(closestDoor);

            doorComp.isPlayerNearby = true;
            nearbyDoor = closestDoor;

            boolean enemiesExist = playerHasActiveEnemiesNearby();

            TransformComponent doorTransform = transformMapper.get(closestDoor);
            currentDoorPromptPos = new Vector2(doorTransform.position.x, doorTransform.position.y + 60f);

            if (enemiesExist) {
                interaction.promptMessage = "Defeat all enemies first!";
                currentDoorPrompt = interaction.promptMessage;
            } else {
                // Default prompt
                if (doorComp.doorType == DoorComponent.DoorType.EXIT_TO_NEXT) {
                    interaction.promptMessage = "Press E to enter Carriage " + doorComp.targetCarriageNumber;
                } else {
                    interaction.promptMessage = "From Carriage " + doorComp.targetCarriageNumber;
                }
                currentDoorPrompt = interaction.promptMessage;
            }
        } else {
            nearbyDoor = null;
        }
    }

    private boolean playerHasActiveEnemiesNearby() {
        if (playerEntities.size() == 0) return false;
        Entity player = playerEntities.get(0);
        PlayerComponent playerComp = playerMapper.get(player);
        int playerCarriage = playerComp.currentCarriageNumber;

        for (Entity enemy : enemyEntities) {
            EnemyComponent ec = enemyMapper.get(enemy);
            if (ec != null && ec.carriageNumber == playerCarriage && ec.isActive) {
                return true;
            }
        }
        return false;
    }

    private void handleInteractionInput(Entity player, PlayerComponent playerComp) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (nearbyDoor != null) {
                if (playerHasActiveEnemiesNearby()) {
                    showDoorErrorPrompt = true;
                    doorErrorPromptMessage = "Defeat all enemies first!";
                    doorErrorPromptTimer = DOOR_ERROR_PROMPT_DURATION;
                    return;
                }
                performDoorTransition(player, playerComp, nearbyDoor);
            }
        }
    }

    private void performDoorTransition(Entity player, PlayerComponent playerComp, Entity door) {
        DoorComponent doorComp = doorMapper.get(door);
        TransformComponent playerTransform = transformMapper.get(player);

        if (doorComp.doorType == DoorComponent.DoorType.EXIT_TO_NEXT) {
            int nextCarriage = doorComp.targetCarriageNumber;

            // Teleport player to entry of next carriage
            teleportPlayerToCarriageEntry(playerTransform, nextCarriage);

            // Update player's current carriage
            playerComp.currentCarriageNumber = nextCarriage;

            System.out.println("🚪 Player entered Car " + nextCarriage);

        } else if (doorComp.doorType == DoorComponent.DoorType.ENTRY_FROM_PREV) {
            System.out.println("🚪 Entry door - no action");
        }

        showingPrompt = false;
        nearbyDoor = null;
    }

    private void teleportPlayerToCarriageEntry(TransformComponent playerTransform, int targetCarriageNumber) {
        // Calculate target position 
        float targetX = GameConstants.CARRIAGE_WIDTH / 2f;
        float targetY = (targetCarriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT +
                       GameConstants.ENTRY_ZONE_HEIGHT + 50f;

        playerTransform.position.set(targetX, targetY);

        System.out.println("🔄 Player teleported to Carriage " + targetCarriageNumber +
                          " at (" + targetX + ", " + targetY + ")");
    }

    // Getter untuk UI
    public String getCurrentDoorPrompt() {
        return currentDoorPrompt;
    }
    public Vector2 getCurrentDoorPromptPos() {
        return currentDoorPromptPos;
    }
    public boolean isShowDoorErrorPrompt() {
        return showDoorErrorPrompt;
    }
    public String getDoorErrorPromptMessage() {
        return doorErrorPromptMessage;
    }
}

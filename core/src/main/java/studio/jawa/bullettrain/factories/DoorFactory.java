package studio.jawa.bullettrain.entities;

import com.badlogic.ashley.core.Entity;
import studio.jawa.bullettrain.components.gameplay.InteractionComponent;
import studio.jawa.bullettrain.components.level.DoorComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.GameConstants;

public class DoorFactory {

    public static Entity createExitDoor(int fromCarriageNumber, float carriageY) {
        Entity door = new Entity();

        // Position at top of carriage (exit zone)
        float doorX = GameConstants.CARRIAGE_WIDTH / 2f; 
        float doorY = carriageY + GameConstants.CARRIAGE_HEIGHT - GameConstants.EXIT_ZONE_HEIGHT / 2f;
        door.add(new TransformComponent(doorX, doorY));

        // Door component
        DoorComponent doorComp = new DoorComponent(DoorComponent.DoorType.EXIT_TO_NEXT, fromCarriageNumber + 1);
        // Door bounds (rectangle around door)
        doorComp.doorBounds.set(doorX - 40f, doorY - 30f, 80f, 60f);
        door.add(doorComp);

        // Interaction component
        InteractionComponent interaction = new InteractionComponent(
            InteractionComponent.InteractionType.DOOR_TRANSITION,
            "Press E to enter Car " + (fromCarriageNumber + 1)
        );
        door.add(interaction);

        return door;
    }

    public static Entity createEntryDoor(int toCarriageNumber, float carriageY) {
        Entity door = new Entity();

        float doorX = GameConstants.CARRIAGE_WIDTH / 2f; 
        float doorY = carriageY + GameConstants.ENTRY_ZONE_HEIGHT / 2f;
        door.add(new TransformComponent(doorX, doorY));

        // Door component
        DoorComponent doorComp = new DoorComponent(DoorComponent.DoorType.ENTRY_FROM_PREV, toCarriageNumber - 1);
        // Door bounds (rectangle around door)
        doorComp.doorBounds.set(doorX - 40f, doorY - 30f, 80f, 60f);
        door.add(doorComp);

        // Interaction component
        InteractionComponent interaction = new InteractionComponent(
            InteractionComponent.InteractionType.DOOR_TRANSITION,
            "From Car " + (toCarriageNumber - 1)
        );
        door.add(interaction);

        return door;
    }
}

package studio.jawa.bullettrain.factories;

import com.badlogic.ashley.core.Entity;
import studio.jawa.bullettrain.components.level.CarriageBoundaryComponent;
import studio.jawa.bullettrain.components.level.CarriageLoadingComponent;
import studio.jawa.bullettrain.components.level.OpenLayoutComponent;
import studio.jawa.bullettrain.components.level.TrainCarriageComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.CarriageType;
import studio.jawa.bullettrain.data.GameConstants;
import studio.jawa.bullettrain.generation.TrainCarriageGenerator;
import studio.jawa.bullettrain.data.ObjectType;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;

public class CarriageFactory {

    public static Entity createCarriage(int carriageNumber, long baseSeed) {
        Entity carriage = new Entity();

        // Calculate position
        float carriageY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;
        carriage.add(new TransformComponent(0f, carriageY));

        // Determine carriage type based on number
        CarriageType type = getCarriageTypeForNumber(carriageNumber);

        // Create carriage seed (deterministic but unique per carriage)
        long carriageSeed = baseSeed + carriageNumber * 1000L;

        // Add carriage metadata
        TrainCarriageComponent carriageComp = new TrainCarriageComponent(carriageNumber, type, carriageSeed);
        carriage.add(carriageComp);

        // Add boundary component
        CarriageBoundaryComponent boundary = new CarriageBoundaryComponent();
        boundary.carriageBounds.y = carriageY;
        boundary.playableBounds.y = carriageY + GameConstants.ENTRY_ZONE_HEIGHT;
        boundary.entryZone.y = carriageY;
        boundary.exitZone.y = carriageY + GameConstants.CARRIAGE_HEIGHT - GameConstants.EXIT_ZONE_HEIGHT;
        carriage.add(boundary);

        // Generate layout
        OpenLayoutComponent layout = TrainCarriageGenerator.generateLayout(type, carriageSeed, carriageNumber);
        // Adjust layout positions for carriage Y offset
        adjustLayoutForPosition(layout, carriageY);
        carriage.add(layout);

        // Add loading component
        CarriageLoadingComponent loading = new CarriageLoadingComponent();
        loading.state = CarriageLoadingComponent.LoadingState.ACTIVE;
        carriage.add(loading);

        return carriage;
    }

    private static CarriageType getCarriageTypeForNumber(int carriageNumber) {
        switch (carriageNumber) {
            case 1: return CarriageType.PASSENGER;
            case 2: return CarriageType.CARGO;
            case 3: return CarriageType.DINING;
            case 4: return CarriageType.ENGINE;
            case 5: return CarriageType.CABOOSE;
            default:
                CarriageType[] types = CarriageType.values();
                return types[(carriageNumber - 1) % types.length];
        }
    }

    private static void adjustLayoutForPosition(OpenLayoutComponent layout, float yOffset) {
        // Adjust all positions by carriage Y offset
        layout.entryPoint.y += yOffset;
        layout.exitPoint.y += yOffset;

        for (int i = 0; i < layout.obstaclePositions.size; i++) {
            layout.obstaclePositions.get(i).y += yOffset;
        }

        for (int i = 0; i < layout.enemySpawnPoints.size; i++) {
            layout.enemySpawnPoints.get(i).y += yOffset;
        }

        for (int i = 0; i < layout.pickupSpawnPoints.size; i++) {
            layout.pickupSpawnPoints.get(i).y += yOffset;
        }
    }

    public static Entity[] createCarriageWithObjects(int carriageNumber, long baseSeed, int maxCarriages, AssetManager assetManager) {
        // Create main carriage
        Entity carriage = createCarriage(carriageNumber, baseSeed);

        // Generate objects from layout
        OpenLayoutComponent layout = carriage.getComponent(OpenLayoutComponent.class);
        createObjectsFromLayout(layout, carriageNumber, assetManager);

        // Calculate carriage Y position
        float carriageY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;

        // Create doors array (carriage + doors + objects)
        Array<Entity> entityList = new Array<>();
        entityList.add(carriage);

        // Add doors
        if (carriageNumber > 1) {
            entityList.add(DoorFactory.createEntryDoor(carriageNumber, carriageY));
        }
        if (carriageNumber < maxCarriages) {
            entityList.add(DoorFactory.createExitDoor(carriageNumber, carriageY));
        }

        // Add objects
        for (Entity object : layout.objectEntities) {
            entityList.add(object);
        }

        // NOTE: Enemies akan di-spawn oleh EnemySpawnSystem, tidak di sini

        return entityList.toArray(Entity.class);
    }

    private static void createObjectsFromLayout(OpenLayoutComponent layout, int carriageNumber, AssetManager assetManager) {
        // Create objects from obstacle positions
        for (Vector2 position : layout.obstaclePositions) {
            ObjectType randomType = ObjectType.getRandomType();

            if (!assetManager.isLoaded(randomType.texturePath)) {
                continue;
            }

            Texture texture = assetManager.get(randomType.texturePath, Texture.class);
            Entity object = ObjectFactory.createObject(randomType, position, carriageNumber, texture);
            layout.objectEntities.add(object);
        }
    }

    public static Entity[] createCarriageWithDoors(int carriageNumber, long baseSeed, int maxCarriages) {
        // Create main carriage
        Entity carriage = createCarriage(carriageNumber, baseSeed);

        // Calculate carriage Y position
        float carriageY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;

        // Create doors array (carriage + doors)
        Entity[] entities;

        if (carriageNumber == 1) {
            // First carriage: only exit door (no entry from below)
            entities = new Entity[2];
            entities[0] = carriage;
            entities[1] = DoorFactory.createExitDoor(carriageNumber, carriageY);
        } else if (carriageNumber == maxCarriages) {
            // Last carriage: only entry door (no exit to above)
            entities = new Entity[2];
            entities[0] = carriage;
            entities[1] = DoorFactory.createEntryDoor(carriageNumber, carriageY);
        } else {
            // Middle carriages: both entry and exit doors
            entities = new Entity[3];
            entities[0] = carriage;
            entities[1] = DoorFactory.createEntryDoor(carriageNumber, carriageY);
            entities[2] = DoorFactory.createExitDoor(carriageNumber, carriageY);
        }

        return entities;
    }
}

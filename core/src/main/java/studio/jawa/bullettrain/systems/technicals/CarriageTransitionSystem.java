package studio.jawa.bullettrain.systems.technicals;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.level.CarriageBoundaryComponent;
import studio.jawa.bullettrain.components.level.CarriageManagerComponent;
import studio.jawa.bullettrain.components.level.TrainCarriageComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.data.GameConstants;
import studio.jawa.bullettrain.factories.CarriageFactory;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import studio.jawa.bullettrain.data.ObjectType;

public class CarriageTransitionSystem extends EntitySystem {

    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<CarriageManagerComponent> managerMapper;
    private ComponentMapper<CarriageBoundaryComponent> boundaryMapper;
    private ComponentMapper<TrainCarriageComponent> carriageMapper;

    private Entity carriageManager;
    private long baseSeed = 12345L;
    private AssetManager assetManager;

    public CarriageTransitionSystem() {
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        managerMapper = ComponentMapper.getFor(CarriageManagerComponent.class);
        boundaryMapper = ComponentMapper.getFor(CarriageBoundaryComponent.class);
        carriageMapper = ComponentMapper.getFor(TrainCarriageComponent.class);
        setupAssetManager();
    }

    private void setupAssetManager() {
        assetManager = new AssetManager();
        // Load all object textures
        for (ObjectType objectType : ObjectType.values()) {
            assetManager.load(objectType.texturePath, Texture.class);
        }

        // Load enemy textures
        assetManager.load("textures/enemies/melee_enemy.png", Texture.class);
        assetManager.load("textures/enemies/ranged_enemy.png", Texture.class);

        assetManager.finishLoading();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        // Create carriage manager entity
        createCarriageManager();

        // Load initial carriages (1, 2, 3)
        loadInitialCarriages();
    }

    @Override
    public void update(float deltaTime) {
        if (carriageManager == null) return;

        // Get player
        ImmutableArray<Entity> playerEntities = getEngine().getEntitiesFor(
            Family.all(PlayerComponent.class, TransformComponent.class).get()
        );

        if (playerEntities.size() == 0) return;
        Entity player = playerEntities.get(0);

        // Check for transitions
        checkPlayerTransitions(player);

        // Update carriage loading window
        updateCarriageWindow(player);
    }

    private void createCarriageManager() {
        carriageManager = new Entity();
        carriageManager.add(new CarriageManagerComponent());
        getEngine().addEntity(carriageManager);
    }

    private void loadInitialCarriages() {
        CarriageManagerComponent manager = managerMapper.get(carriageManager);

        for (int i = 1; i <= 3; i++) {
            Entity[] carriageEntities = CarriageFactory.createCarriageWithObjects(
                i, baseSeed, manager.maxCarriages, assetManager
            );

            Entity carriage = carriageEntities[0];
            manager.addCarriage(i, carriage);

            for (Entity entity : carriageEntities) {
                getEngine().addEntity(entity);
            }
        }
    }

    private void checkPlayerTransitions(Entity player) {
        TransformComponent playerTransform = transformMapper.get(player);
        PlayerComponent playerComp = playerMapper.get(player);
        CarriageManagerComponent manager = managerMapper.get(carriageManager);

        if (playerTransform == null || playerComp == null) return;

        int targetCarriageNumber = (int) (playerTransform.position.y / GameConstants.CARRIAGE_HEIGHT) + 1;
        targetCarriageNumber = Math.max(1, Math.min(manager.maxCarriages, targetCarriageNumber));

        // Check if player has moved to a different carriage
        if (targetCarriageNumber != playerComp.currentCarriageNumber) {
            // Player transition!
            playerComp.currentCarriageNumber = targetCarriageNumber;
            manager.currentCarriageNumber = targetCarriageNumber;

            // Check if player moved too far beyond boundaries
            keepPlayerInBounds(player, targetCarriageNumber);
        }
    }

    private void keepPlayerInBounds(Entity player, int carriageNumber) {
        TransformComponent playerTransform = transformMapper.get(player);
        CarriageManagerComponent manager = managerMapper.get(carriageManager);

        // Get current carriage bounds
        Entity carriage = manager.getCarriage(carriageNumber);
        if (carriage == null) return;

        CarriageBoundaryComponent boundary = boundaryMapper.get(carriage);
        if (boundary == null) return;

        float carriageBottom = boundary.carriageBounds.y + 50f;
        float carriageTop = boundary.carriageBounds.y + boundary.carriageBounds.height - 50f;

        if (playerTransform.position.y < carriageBottom) {
            playerTransform.position.y = carriageBottom;
        } else if (playerTransform.position.y > carriageTop) {
            playerTransform.position.y = carriageTop;
        }
    }

    private void updateCarriageWindow(Entity player) {
        PlayerComponent playerComp = playerMapper.get(player);
        CarriageManagerComponent manager = managerMapper.get(carriageManager);

        if (playerComp == null || manager == null) return;

        int currentCarriage = playerComp.currentCarriageNumber;

        // Determine which carriages should be loaded (3-carriage window)
        int startCarriage = Math.max(1, currentCarriage - 1);
        int endCarriage = Math.min(manager.maxCarriages, currentCarriage + 1);

        // Load missing carriages
        for (int i = startCarriage; i <= endCarriage; i++) {
            if (!manager.isCarriageLoaded(i)) {
                loadCarriage(i, manager);
            }
        }

        // Unload carriages outside window
        IntArray loadedKeys = manager.loadedCarriages.keys().toArray();
        for (int i = 0; i < loadedKeys.size; i++) {
            int carriageNum = loadedKeys.get(i);
            if (carriageNum < startCarriage || carriageNum > endCarriage) {
                unloadCarriage(carriageNum, manager);
            }
        }
    }

    private void loadCarriage(int carriageNumber, CarriageManagerComponent manager) {
        Entity[] carriageEntities = CarriageFactory.createCarriageWithObjects(
            carriageNumber, baseSeed, manager.maxCarriages, assetManager
        );

        Entity carriage = carriageEntities[0];
        
        // Update boundary for correct carriage number
        CarriageBoundaryComponent boundary = boundaryMapper.get(carriage);
        if (boundary != null) {
            boundary.updateForCarriage(carriageNumber);
        }
        
        manager.addCarriage(carriageNumber, carriage);

        for (Entity entity : carriageEntities) {
            getEngine().addEntity(entity);
        }
        
        System.out.println("🚂 Loaded Car " + carriageNumber + " with proper boundaries");
    }

    private void unloadCarriage(int carriageNumber, CarriageManagerComponent manager) {
        Entity carriage = manager.getCarriage(carriageNumber);
        if (carriage != null) {
            getEngine().removeEntity(carriage);
            manager.removeCarriage(carriageNumber);
        }
    }
}

package studio.jawa.bullettrain.screens.gamescreens;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.level.CarriageBoundaryComponent;
import studio.jawa.bullettrain.components.level.CarriageManagerComponent;
import studio.jawa.bullettrain.components.level.OpenLayoutComponent;
import studio.jawa.bullettrain.components.level.TrainCarriageComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.factories.PlayerFactory;
import studio.jawa.bullettrain.systems.technicals.CameraSystem;
import studio.jawa.bullettrain.systems.technicals.CarriageTransitionSystem;
import studio.jawa.bullettrain.systems.technicals.DoorInteractionSystem;
import studio.jawa.bullettrain.systems.technicals.PlayerMovementSystem;
import studio.jawa.bullettrain.systems.technicals.RenderingSystem;
import studio.jawa.bullettrain.components.level.DoorComponent;
import studio.jawa.bullettrain.components.gameplay.InteractionComponent;
import studio.jawa.bullettrain.systems.technicals.CollisionSystem;
import studio.jawa.bullettrain.systems.gameplay.enemies.EnemySpawnSystem;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyComponent;
import studio.jawa.bullettrain.systems.gameplay.enemies.EnemyChaseSystem;
import studio.jawa.bullettrain.systems.gameplay.enemies.EnemyIdleSystem;
import studio.jawa.bullettrain.systems.gameplay.enemies.EnemyStrafeSystem;
import studio.jawa.bullettrain.systems.technicals.MovementSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class GamePlayTestScreen implements Screen {
    private Engine engine;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private CameraSystem cameraSystem;
    private RenderingSystem renderingSystem;

    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<CarriageManagerComponent> managerMapper;
    private ComponentMapper<TrainCarriageComponent> carriageMapper;
    private ComponentMapper<CarriageBoundaryComponent> boundaryMapper;
    private ComponentMapper<OpenLayoutComponent> layoutMapper;
    private ComponentMapper<DoorComponent> doorMapper;
    private ComponentMapper<InteractionComponent> interactionMapper;
    private ComponentMapper<EnemyComponent> enemyMapper;

    private Entity player;
    private AssetManager assetManager;

    @Override
    public void show() {
        // Setup camera
        camera = new OrthographicCamera();
        camera.viewportWidth = 800f;
        camera.viewportHeight = 600f;
        camera.update();

        shapeRenderer = new ShapeRenderer();

        // Setup ECS
        engine = new Engine();

        // Component mappers
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        managerMapper = ComponentMapper.getFor(CarriageManagerComponent.class);
        carriageMapper = ComponentMapper.getFor(TrainCarriageComponent.class);
        boundaryMapper = ComponentMapper.getFor(CarriageBoundaryComponent.class);
        layoutMapper = ComponentMapper.getFor(OpenLayoutComponent.class);
        doorMapper = ComponentMapper.getFor(DoorComponent.class);
        interactionMapper = ComponentMapper.getFor(InteractionComponent.class);
        enemyMapper = ComponentMapper.getFor(EnemyComponent.class);

        // Setup AssetManager untuk enemy textures
        setupAssetManager();

        // Add systems
        engine.addSystem(new PlayerMovementSystem());
        engine.addSystem(new CarriageTransitionSystem());
        engine.addSystem(new DoorInteractionSystem());
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new EnemySpawnSystem(assetManager));
        
        // Add missing enemy movement systems
        engine.addSystem(new EnemyIdleSystem(engine));
        engine.addSystem(new EnemyChaseSystem());
        engine.addSystem(new EnemyStrafeSystem());
        engine.addSystem(new MovementSystem(engine));
        
        cameraSystem = new CameraSystem(camera);
        engine.addSystem(cameraSystem);
        renderingSystem = new RenderingSystem(camera);
        engine.addSystem(renderingSystem);

        // Create player
        createPlayer();
    }

    private void setupAssetManager() {
        assetManager = new AssetManager();
        // Player texture
        assetManager.load("testing/dummy.png", Texture.class);
        //Enemy textures
        assetManager.load("textures/enemies/melee_enemy.png", Texture.class);
        assetManager.load("textures/enemies/ranged_enemy.png", Texture.class);
        assetManager.finishLoading();
    }

    private void createPlayer() {
        Texture playerTexture = assetManager.get("testing/dummy.png", Texture.class);
        player = PlayerFactory.createPlayerAtCarriageEntry(1, playerTexture);
        engine.addEntity(player);
    }

    @Override
    public void render(float delta) {
        handleInput();

        // Clear screen
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update ECS (this will update camera position and render sprites)
        engine.update(delta);

        // Set camera matrix for shape rendering
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Render shapes AFTER sprite rendering (carriages, doors, player)
        renderAllCarriages();
        renderAllDoors();
        renderPlayer();
        renderUI();

        // Note: Objects with sprites are rendered by RenderingSystem during engine.update()
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void renderAllCarriages() {
        // Get carriage manager
        ImmutableArray<Entity> managers = engine.getEntitiesFor(
            Family.all(CarriageManagerComponent.class).get()
        );

        if (managers.size() == 0) return;
        Entity manager = managers.get(0);
        CarriageManagerComponent managerComp = managerMapper.get(manager);

        // Render all loaded carriages
        for (int carriageNum : managerComp.loadedCarriages.keys().toArray().toArray()) {
            Entity carriage = managerComp.getCarriage(carriageNum);
            renderCarriage(carriage);
        }
    }

    private void renderCarriage(Entity carriage) {
        if (carriage == null) return;

        TrainCarriageComponent carriageComp = carriageMapper.get(carriage);
        CarriageBoundaryComponent boundary = boundaryMapper.get(carriage);
        OpenLayoutComponent layout = layoutMapper.get(carriage);

        if (boundary == null || layout == null) return;

        // Draw boundaries
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(1f, 1f, 1f, 1f); // White outline
        shapeRenderer.rect(boundary.carriageBounds.x, boundary.carriageBounds.y,
                        boundary.carriageBounds.width, boundary.carriageBounds.height);

        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, 0.5f); // Light gray
        shapeRenderer.rect(boundary.playableBounds.x, boundary.playableBounds.y,
                        boundary.playableBounds.width, boundary.playableBounds.height);

        // Entry zone (green)
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(boundary.entryZone.x, boundary.entryZone.y,
                        boundary.entryZone.width, boundary.entryZone.height);

        // Exit zone (cyan)
        shapeRenderer.setColor(0, 1, 1, 1);
        shapeRenderer.rect(boundary.exitZone.x, boundary.exitZone.y,
                        boundary.exitZone.width, boundary.exitZone.height);

        shapeRenderer.end();
    }


    private void renderPlayer() {
        if (player == null) return;

        TransformComponent transform = transformMapper.get(player);
        if (transform == null) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Remove the blue circle - player sprite is already rendered by RenderingSystem
        // shapeRenderer.setColor(0, 0.5f, 1, 1);
        // shapeRenderer.circle(transform.position.x, transform.position.y, 20);

        // Keep only the direction indicator (small triangle pointing up)
        shapeRenderer.setColor(1, 1, 1, 1);
        float px = transform.position.x;
        float py = transform.position.y + 25;
        shapeRenderer.triangle(px, py + 10, px - 5, py, px + 5, py);

        shapeRenderer.end();
    }

    private void renderUI() {
        // Get player info for UI
        if (player == null) return;

        PlayerComponent playerComp = playerMapper.get(player);
        TransformComponent transform = transformMapper.get(player);

        if (playerComp == null || transform == null) return;

        // UI info akan di-print ke console untuk sekarang
        // Nanti bisa diganti dengan proper UI rendering
    }

    private void renderAllDoors() {
    // Get all door entities
    ImmutableArray<Entity> doorEntities = engine.getEntitiesFor(
        Family.all(DoorComponent.class, TransformComponent.class).get()
    );


    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

    for (Entity door : doorEntities) {
        renderDoor(door);
    }

    shapeRenderer.end();

    // Render door outlines
    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

    for (Entity door : doorEntities) {
        renderDoorOutline(door);
    }

    shapeRenderer.end();
}

    private void renderDoor(Entity door) {
        TransformComponent transform = transformMapper.get(door);
        DoorComponent doorComp = doorMapper.get(door);
        InteractionComponent interaction = interactionMapper.get(door);

        if (transform == null || doorComp == null) return;

        // Different colors based on door type and state
        if (doorComp.doorType == DoorComponent.DoorType.EXIT_TO_NEXT) {
            // Exit doors (cyan/blue)
            if (doorComp.isPlayerNearby) {
                shapeRenderer.setColor(0f, 1f, 1f, 0.8f); // Bright cyan when nearby
            } else {
                shapeRenderer.setColor(0f, 0.7f, 1f, 0.6f); // Blue when far
            }
        } else {
            // Entry doors (green)
            shapeRenderer.setColor(0f, 0.8f, 0f, 0.5f); // Green
        }

        // Draw door rectangle
        shapeRenderer.rect(
            transform.position.x - 30f,
            transform.position.y - 40f,
            60f, 80f
        );

        // Draw door handle/indicator
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        if (doorComp.doorType == DoorComponent.DoorType.EXIT_TO_NEXT) {
            // Up arrow for exit doors
            float x = transform.position.x;
            float y = transform.position.y + 20f;
            shapeRenderer.triangle(x, y + 15f, x - 10f, y, x + 10f, y);
        } else {
            // Down arrow for entry doors
            float x = transform.position.x;
            float y = transform.position.y - 20f;
            shapeRenderer.triangle(x, y - 15f, x - 10f, y, x + 10f, y);
        }
    }

    private void renderDoorOutline(Entity door) {
        TransformComponent transform = transformMapper.get(door);
        DoorComponent doorComp = doorMapper.get(door);

        if (transform == null || doorComp == null) return;

        // White outline
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(
            transform.position.x - 30f,
            transform.position.y - 40f,
            60f, 80f
        );

        // Interaction radius (when player nearby)
        if (doorComp.isPlayerNearby) {
            shapeRenderer.setColor(1f, 1f, 0f, 0.3f);
            shapeRenderer.circle(transform.position.x, transform.position.y, 50f);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}

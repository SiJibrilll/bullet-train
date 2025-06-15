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
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.level.CarriageBoundaryComponent;
import studio.jawa.bullettrain.components.level.CarriageManagerComponent;
import studio.jawa.bullettrain.components.level.OpenLayoutComponent;
import studio.jawa.bullettrain.components.level.TrainCarriageComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.factories.PlayerFactory;
import studio.jawa.bullettrain.systems.technicals.AnimationSystem;
import studio.jawa.bullettrain.systems.technicals.CameraSystem;
import studio.jawa.bullettrain.systems.technicals.CarriageTransitionSystem;
import studio.jawa.bullettrain.systems.technicals.DoorInteractionSystem;
import studio.jawa.bullettrain.systems.technicals.HitFlashRenderSystem;
import studio.jawa.bullettrain.systems.technicals.PlayerMovementSystem;
import studio.jawa.bullettrain.systems.technicals.ProjectileCollisionSystem;
import studio.jawa.bullettrain.systems.technicals.RenderingSystem;
import studio.jawa.bullettrain.systems.technicals.WeaponOrbitSystem;
import studio.jawa.bullettrain.components.level.DoorComponent;
import studio.jawa.bullettrain.components.gameplay.InteractionComponent;
import studio.jawa.bullettrain.systems.technicals.CollisionSystem;
import studio.jawa.bullettrain.systems.technicals.DeathDragSystem;
import studio.jawa.bullettrain.systems.technicals.DebugRenderSystem;
import studio.jawa.bullettrain.systems.gameplay.enemies.EnemySpawnSystem;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyComponent;
import studio.jawa.bullettrain.systems.effects.HitFlashSystem;
import studio.jawa.bullettrain.systems.gameplay.DamageSystem;
import studio.jawa.bullettrain.systems.gameplay.DeathSystem;
import studio.jawa.bullettrain.systems.gameplay.enemies.EnemyChaseSystem;
import studio.jawa.bullettrain.systems.gameplay.enemies.EnemyIdleSystem;
import studio.jawa.bullettrain.systems.gameplay.enemies.EnemyStrafeSystem;
import studio.jawa.bullettrain.systems.projectiles.PlayerProjectileSpawningSystem;
import studio.jawa.bullettrain.systems.technicals.MovementSystem;
import studio.jawa.bullettrain.systems.technicals.PlayerFacingSystem;
import studio.jawa.bullettrain.systems.technicals.PlayerMovementAnimationSystem;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

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
    private Texture roofTexture;
    private Texture grassTexture;
    private SpriteBatch sharedBatch;
    private BitmapFont font;

    private float grassOffsetY = 0f;
    private float grassSpeed = 100f;
    private DoorInteractionSystem doorInteractionSystem;

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
        sharedBatch = new SpriteBatch();
        font = new BitmapFont(); 

        // Add systems
        engine.addSystem(new PlayerMovementSystem());
        engine.addSystem(new CarriageTransitionSystem());
        doorInteractionSystem = new DoorInteractionSystem();
        engine.addSystem(doorInteractionSystem);
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new EnemySpawnSystem(assetManager));

        engine.addSystem(new AnimationSystem());
        engine.addSystem(new PlayerFacingSystem(camera));
        engine.addSystem(new PlayerMovementAnimationSystem());




        // Add missing enemy movement systems
        engine.addSystem(new EnemyIdleSystem(engine));
        engine.addSystem(new EnemyChaseSystem(assetManager));
        engine.addSystem(new EnemyStrafeSystem(assetManager, engine));
        engine.addSystem(new MovementSystem(engine));
        engine.addSystem(new DeathDragSystem());
        engine.addSystem(new WeaponOrbitSystem(camera));

        engine.addSystem(new PlayerProjectileSpawningSystem(camera, engine, assetManager));
        engine.addSystem(new ProjectileCollisionSystem(engine));

        engine.addSystem(new HitFlashSystem());
        engine.addSystem(new HitFlashRenderSystem(camera, sharedBatch));
        engine.addSystem(new RenderingSystem(camera, sharedBatch));
        engine.addSystem(new DebugRenderSystem(camera, engine));

        cameraSystem = new CameraSystem(camera);
        engine.addSystem(cameraSystem);
        renderingSystem = new RenderingSystem(camera, sharedBatch);
        engine.addSystem(renderingSystem);
        engine.addSystem(new DeathSystem(assetManager));
        engine.addSystem(new DamageSystem());

        // Create player
        createPlayer();
    }

    private void setupAssetManager() {
        assetManager = new AssetManager();
        // Player texture
        assetManager.load("testing/dummy.png", Texture.class);
        assetManager.load("testing/dummy2.png", Texture.class);
        //Enemy textures
        assetManager.load("textures/enemies/melee_enemy.png", Texture.class);
        assetManager.load("textures/enemies/ranged_enemy.png", Texture.class);
        assetManager.load("testing/bullet.png", Texture.class);
        assetManager.load("testing/slash.png", Texture.class);
        assetManager.load("testing/death.png", Texture.class);

        //load animations
        assetManager.load("testing/animation/death.png", Texture.class);
        assetManager.load("testing/animation/idle.png", Texture.class);
        assetManager.load("testing/animation/run.png", Texture.class);

        assetManager.load("testing/sword.png", Texture.class);
        assetManager.load("testing/gun.png", Texture.class);
        assetManager.load("textures/world/roof.png", Texture.class); 
        assetManager.load("textures/world/grass.png", Texture.class); 
        assetManager.finishLoading();
        roofTexture = assetManager.get("textures/world/roof.png", Texture.class); 
        grassTexture = assetManager.get("textures/world/grass.png", Texture.class); 
    }

    private void createPlayer() {
        Texture playerTexture = assetManager.get("testing/dummy.png", Texture.class);
        player = PlayerFactory.createPlayerAtCarriageEntry(1, assetManager, engine);
        engine.addEntity(player);
    }

    @Override
    public void render(float delta) {
        handleInput();

        // Update grass offset
        if (grassTexture != null) {
            float grassHeight = grassTexture.getHeight();
            grassOffsetY += grassSpeed * delta;
            grassOffsetY = grassOffsetY % grassHeight;
            if (grassOffsetY < 0) grassOffsetY += grassHeight;
        }

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);

        shapeRenderer.setProjectionMatrix(camera.combined);

        renderAllCarriages();
        renderAllDoors();
        renderPlayer();
        renderUI();

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

        // Ambil currentCarriageNumber player
        int playerCarriageNumber = 1;
        if (player != null) {
            PlayerComponent playerComp = playerMapper.get(player);
            if (playerComp != null) {
                playerCarriageNumber = playerComp.currentCarriageNumber;
            }
        }

        for (int carriageNum : managerComp.loadedCarriages.keys().toArray().toArray()) {
            Entity carriage = managerComp.getCarriage(carriageNum);
            renderCarriage(carriage);

            if (carriageNum != playerCarriageNumber) {
                renderCarriageRoof(carriage);
            }
        }
    }

    private void renderCarriage(Entity carriage) {
        if (carriage == null) return;

        TrainCarriageComponent carriageComp = carriageMapper.get(carriage);
        CarriageBoundaryComponent boundary = boundaryMapper.get(carriage);
        OpenLayoutComponent layout = layoutMapper.get(carriage);

        if (boundary == null || layout == null) return;

        // Draw grass
        if (grassTexture != null) {
            sharedBatch.begin();
            sharedBatch.setProjectionMatrix(camera.combined);

            float grassWidth = grassTexture.getWidth();
            float grassHeight = grassTexture.getHeight();
            float carriageX = boundary.carriageBounds.x;
            float carriageY = boundary.carriageBounds.y;
            float carriageWidth = boundary.carriageBounds.width;
            float carriageHeight = boundary.carriageBounds.height;

            for (int side = 0; side < 2; side++) {
                float x = (side == 0)
                    ? carriageX - grassWidth // kiri
                    : carriageX + carriageWidth; // kanan

                float startY = carriageY - grassOffsetY;
                int numTiles = (int)Math.ceil((carriageHeight + grassHeight) / grassHeight);

                for (int i = 0; i < numTiles; i++) {
                    float y = startY + i * grassHeight;
                    sharedBatch.draw(
                        grassTexture,
                        x,
                        y,
                        grassWidth,
                        grassHeight
                    );
                }
            }

            sharedBatch.end();
        }

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

    private void renderCarriageRoof(Entity carriage) {
        if (carriage == null || roofTexture == null) return;

        CarriageBoundaryComponent boundary = boundaryMapper.get(carriage);
        if (boundary == null) return;

        sharedBatch.begin();
        sharedBatch.setProjectionMatrix(camera.combined);

        float x = boundary.carriageBounds.x;
        float y = boundary.carriageBounds.y;
        float width = boundary.carriageBounds.width;
        float height = boundary.carriageBounds.height;

        sharedBatch.draw(roofTexture, x, y, width, height);

        sharedBatch.end();
    }

    private void renderPlayer() {
        if (player == null) return;

        TransformComponent transform = transformMapper.get(player);
        if (transform == null) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        shapeRenderer.setColor(1, 1, 1, 1);
        float px = transform.position.x;
        float py = transform.position.y + 25;
        shapeRenderer.triangle(px, py + 10, px - 5, py, px + 5, py);

        shapeRenderer.end();
    }

    private void renderUI() {
        if (player == null) return;

        PlayerComponent playerComp = playerMapper.get(player);
        TransformComponent transform = transformMapper.get(player);

        if (playerComp == null || transform == null) return;

        int playerCarriage = playerComp.currentCarriageNumber;
        int enemyCount = 0;
        ImmutableArray<Entity> enemies = engine.getEntitiesFor(
            Family.all(EnemyComponent.class).exclude(studio.jawa.bullettrain.components.gameplay.DeathComponent.class).get()
        );
        for (Entity enemy : enemies) {
            EnemyComponent ec = enemyMapper.get(enemy);
            if (ec != null && ec.carriageNumber == playerCarriage && ec.isActive) {
                enemyCount++;
            }
        }

        // Render jumlah enemy
        sharedBatch.begin();
        font.draw(sharedBatch, "Enemies in Carriage: " + enemyCount, camera.position.x - camera.viewportWidth/2 + 20, camera.position.y + camera.viewportHeight/2 - 20);

        // Render door prompt (di atas pintu)
        if (doorInteractionSystem != null) {
            String prompt = doorInteractionSystem.getCurrentDoorPrompt();
            Vector2 pos = doorInteractionSystem.getCurrentDoorPromptPos();
            if (prompt != null && pos != null) {
                font.setColor(1f, 1f, 0.7f, 1f); // kuning
                font.draw(sharedBatch, prompt, pos.x - 60, pos.y + 40);
                font.setColor(1f, 1f, 1f, 1f); // reset
            }
            // Render error prompt jika ada
            if (doorInteractionSystem.isShowDoorErrorPrompt()) {
                String errorMsg = doorInteractionSystem.getDoorErrorPromptMessage();
                if (errorMsg != null && pos != null) {
                    font.setColor(1f, 0.2f, 0.2f, 1f); // merah
                    font.draw(sharedBatch, errorMsg, pos.x - 80, pos.y + 70);
                    font.setColor(1f, 1f, 1f, 1f); // reset
                }
            }
        }

        sharedBatch.end();
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
        if (font != null) {
            font.dispose();
        }
    }
}


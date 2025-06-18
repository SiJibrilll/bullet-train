package studio.jawa.bullettrain.screens.gamescreens;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Game;
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
import studio.jawa.bullettrain.systems.gameplay.PlayerHealthSystem;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import studio.jawa.bullettrain.screens.gamescreens.EndScreen;
import studio.jawa.bullettrain.screens.uiscreens.CharacterInfo;
import studio.jawa.bullettrain.screens.uiscreens.CursorManager;
import studio.jawa.bullettrain.screens.uiscreens.HudStage;
import studio.jawa.bullettrain.screens.uiscreens.PauseMenuOverlay;

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
    private Texture floorTexture; 
    private Texture grassTexture;
    private Texture treeTexture;
    private Texture railTexture; 
    private SpriteBatch sharedBatch;
    private BitmapFont font;

    private float grassOffsetY = 0f;
    private float grassSpeed = 100f;
    private float railOffsetY = 0f; 
    private DoorInteractionSystem doorInteractionSystem;
    private Array<TreeEntity> trees = new Array<>();
    private float treeSpawnTimer = 0f;
    private float treeSpawnInterval = 0.7f;
    private boolean victory = false;
    private boolean victoryTransitioning = false;
    private float victoryOverlayAlpha = 0f;
    private float victoryFadeSpeed = 1.5f;
    private float victoryFadeOutAlpha = 0f;
    private boolean startFadeOut = false;

    private boolean victoryPending = false;
    private float victoryDelayTimer = 0f;
    private final float victoryDelayDuration = 1.0f;

    private final Game game;
    private final CharacterInfo selectedCharacter;
    private final AssetManager uiAssetManager;
    private PauseMenuOverlay pauseMenuOverlay;
    private HudStage hudStage;
    private SpriteBatch batch;
    private CursorManager cursorManager;

    private boolean isPaused = false;
    private AnimationSystem animation;
    private MovementSystem movementSystem;
    private RenderingSystem renderer;

    public GamePlayTestScreen(Game game, CharacterInfo selectedCharacter, AssetManager assetManager) {
        this.game = game;
        this.selectedCharacter = selectedCharacter;
        this.uiAssetManager = assetManager;
    }

    @Override
    public void show() {
        // Setup camera
        camera = new OrthographicCamera();
        camera.viewportWidth = 800f;
        camera.viewportHeight = 600f;
        camera.update();

        // Ui set up
        pauseMenuOverlay = new PauseMenuOverlay(game, uiAssetManager);

        Gdx.input.setInputProcessor(hudStage);

        batch = new SpriteBatch();
        cursorManager = new CursorManager(uiAssetManager, 10, 10, selectedCharacter);

        cursorManager.resetToCrosshair();

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

        animation = new AnimationSystem();
        engine.addSystem(animation);
        engine.addSystem(new PlayerFacingSystem(camera));
        engine.addSystem(new PlayerMovementAnimationSystem());




        // Add missing enemy movement systems
        engine.addSystem(new EnemyIdleSystem(engine));
        engine.addSystem(new EnemyChaseSystem(assetManager));
        engine.addSystem(new EnemyStrafeSystem(assetManager, engine));
        movementSystem = new MovementSystem(engine);
        engine.addSystem(movementSystem);
        engine.addSystem(new DeathDragSystem());
        engine.addSystem(new WeaponOrbitSystem(camera));

        engine.addSystem(new PlayerProjectileSpawningSystem(camera, engine, assetManager));
        engine.addSystem(new ProjectileCollisionSystem(engine));

        hudStage = new HudStage(new ScreenViewport());
        engine.addSystem(new PlayerHealthSystem(hudStage, selectedCharacter, game, uiAssetManager));

        engine.addSystem(new HitFlashSystem());
        engine.addSystem(new HitFlashRenderSystem(camera, sharedBatch));
        renderer = new RenderingSystem(camera, sharedBatch);
        engine.addSystem(renderer);
        engine.addSystem(new DebugRenderSystem(camera, engine));

        cameraSystem = new CameraSystem(camera);
        engine.addSystem(cameraSystem);
        renderingSystem = new RenderingSystem(camera, sharedBatch);
        engine.addSystem(renderingSystem);
        // engine.addSystem(new DeathSystem(assetManager));
        engine.addSystem(new DamageSystem());

        // Create player
        createPlayer(selectedCharacter);

        if (grassTexture != null) {
            float grassHeight = grassTexture.getHeight();
            float screenHeight = Gdx.graphics.getHeight();
            grassOffsetY = grassHeight - (screenHeight % grassHeight);
            if (grassOffsetY == grassHeight) grassOffsetY = 0f;
        }
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
        assetManager.load("textures/world/tree.png", Texture.class);
        assetManager.load("textures/world/rel.png", Texture.class); 
        assetManager.load("textures/world/lantai.png", Texture.class);
        
        assetManager.load("characters/grace/Grace_Walk.png", Texture.class);
        assetManager.load("characters/grace/Grace_Idle.png", Texture.class);
        assetManager.load("characters/grace/Grace_Death.png", Texture.class);
        
        assetManager.load("particles/Bullet_Ally.png", Texture.class);
        assetManager.load("particles/Melee_Slash.png", Texture.class);
        assetManager.load("particles/slash.png", Texture.class);
        assetManager.load("weapons/Grace_Gun.png", Texture.class);
        
        assetManager.load("particles/Bullet_Enemy.png", Texture.class);

        assetManager.finishLoading();
        roofTexture = assetManager.get("textures/world/roof.png", Texture.class);
        grassTexture = assetManager.get("textures/world/grass.png", Texture.class);
        treeTexture = assetManager.get("textures/world/tree.png", Texture.class); 
        railTexture = assetManager.get("textures/world/rel.png", Texture.class); 
        floorTexture = assetManager.get("textures/world/lantai.png", Texture.class);
    }

    private void createPlayer(CharacterInfo selectedCharacter) {
        Texture playerTexture = assetManager.get("testing/dummy.png", Texture.class);
        player = PlayerFactory.createPlayerAtCarriageEntry(1, assetManager, engine, selectedCharacter);
        engine.addEntity(player);
    }

    @Override
    public void render(float delta) {
        handleInput();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            
            if (pauseMenuOverlay.isVisible()) {
                pauseMenuOverlay.hide();
                Gdx.input.setInputProcessor(hudStage);

                cursorManager.resetToCrosshair();
            } else {
                pauseMenuOverlay.show();
                Gdx.input.setInputProcessor(pauseMenuOverlay.getStage());
            }
        }

        if (pauseMenuOverlay.isVisible()) {
            pauseMenuOverlay.render(delta);
        }

        animation.setPaused(isPaused);
        movementSystem.setPaused(isPaused);
        renderer.isPaused = isPaused;
        isPaused = pauseMenuOverlay.isVisible();

        if (isPaused) {
            camera.position.x = Math.round(camera.position.x);
            camera.position.y = Math.round(camera.position.y);
        }

       

        if (isPaused) return;

        // Cek victory
        checkVictoryCondition();

        if (victoryPending && !victory) {
            victoryDelayTimer -= delta;
            if (victoryDelayTimer <= 0f) {
                victory = true;
                victoryPending = false;
            }
        }

        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderRails(); 

        if (!victory) {
            int playerCarriageNumber = 1;
            if (player != null) {
                PlayerComponent playerComp = playerMapper.get(player);
                if (playerComp != null) {
                    playerCarriageNumber = playerComp.currentCarriageNumber;
                }
            }
            // Ambil carriage manager
            ImmutableArray<Entity> managers = engine.getEntitiesFor(
                Family.all(CarriageManagerComponent.class).get()
            );
            if (managers.size() > 0) {
                Entity manager = managers.get(0);
                CarriageManagerComponent managerComp = managerMapper.get(manager);
                Entity playerCarriage = managerComp.getCarriage(playerCarriageNumber);
                renderCarriageFloor(playerCarriage);
            }

            // Update grass offset
            if (grassTexture != null) {
                float grassHeight = grassTexture.getHeight();
                grassOffsetY += grassSpeed * delta;
                grassOffsetY = grassOffsetY % grassHeight;
                if (grassOffsetY < 0) grassOffsetY += grassHeight;
            }
            // Update rail offset mirip grass
            if (railTexture != null) {
                float railHeight = railTexture.getHeight();
                railOffsetY += grassSpeed * delta;
                railOffsetY = railOffsetY % railHeight;
                if (railOffsetY < 0) railOffsetY += railHeight;
            }

            // TREE SPAWN & UPDATE
            updateAndSpawnTrees(delta);

           
            engine.update(delta);
            

            shapeRenderer.setProjectionMatrix(camera.combined);

            renderAllCarriages();
            renderGrassTrees();
            renderAllDoors();
            renderPlayer();
            renderUI();
        } else {
            if (!victoryTransitioning && victoryOverlayAlpha < 0.85f) {
                victoryOverlayAlpha += delta * victoryFadeSpeed;
                if (victoryOverlayAlpha > 0.85f) victoryOverlayAlpha = 0.85f;
            }
            renderAllCarriages();
            renderGrassTrees();
            renderAllDoors();
            renderPlayer();
            renderUI();
            renderVictoryOverlay(delta);
            handleVictoryInput(delta);
        }

        cursorManager.updateInput();

        cursorManager.update(delta);

        hudStage.act(delta);
        hudStage.draw();
        cursorManager.render(hudStage.getBatch());

        
    }

    private void checkVictoryCondition() {
        if (victory || victoryPending) return;
        if (player == null) return;
        PlayerComponent playerComp = playerMapper.get(player);
        if (playerComp == null) return;
        ImmutableArray<Entity> managers = engine.getEntitiesFor(
            Family.all(CarriageManagerComponent.class).get()
        );
        if (managers.size() == 0) return;
        Entity manager = managers.get(0);
        CarriageManagerComponent managerComp = managerMapper.get(manager);
        int lastCarriage = managerComp.maxCarriages;
        if (playerComp.currentCarriageNumber == lastCarriage) {
            // Cek enemy
            ImmutableArray<Entity> enemies = engine.getEntitiesFor(
                Family.all(EnemyComponent.class).exclude(studio.jawa.bullettrain.components.gameplay.DeathComponent.class).get()
            );
            boolean enemyAlive = false;
            for (Entity enemy : enemies) {
                EnemyComponent ec = enemyMapper.get(enemy);
                if (ec != null && ec.carriageNumber == lastCarriage && ec.isActive) {
                    enemyAlive = true;
                    break;
                }
            }
            if (!enemyAlive) {
                victoryPending = true;
                victoryDelayTimer = victoryDelayDuration;
            }
        }
    }

    private void renderVictoryOverlay(float delta) {
        float overlayAlpha = victoryOverlayAlpha;
        if (victoryTransitioning) {
            victoryFadeOutAlpha += delta * victoryFadeSpeed;
            overlayAlpha = Math.max(0f, victoryOverlayAlpha - victoryFadeOutAlpha);
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        sharedBatch.begin();
        sharedBatch.setProjectionMatrix(camera.combined);

        sharedBatch.setColor(0, 0, 0, overlayAlpha);
        sharedBatch.draw(grassTexture, camera.position.x - camera.viewportWidth/2, camera.position.y - camera.viewportHeight/2, camera.viewportWidth, camera.viewportHeight);

        // Tulisan Victory
        font.getData().setScale(2f);
        font.setColor(1f, 1f, 0.2f, Math.min(1f, overlayAlpha + 0.15f));
        font.draw(sharedBatch, "VICTORY!", camera.position.x - 100, camera.position.y + 40);

        font.getData().setScale(1f);
        font.setColor(1f, 1f, 1f, Math.min(1f, overlayAlpha + 0.15f));
        font.draw(sharedBatch, "Press ENTER to continue...", camera.position.x - 120, camera.position.y - 20);

        sharedBatch.setColor(1, 1, 1, 1);
        sharedBatch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (victoryTransitioning && overlayAlpha <= 0.01f) {
            if (Gdx.app.getApplicationListener() instanceof com.badlogic.gdx.Game) {
                com.badlogic.gdx.Game game = (com.badlogic.gdx.Game) Gdx.app.getApplicationListener();
                game.setScreen(new EndScreen(game));
            }
        }
    }

    private void handleVictoryInput(float delta) {
        if (!victoryTransitioning && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            victoryTransitioning = true;
            victoryFadeOutAlpha = 0f;
        }
    }

    private void updateAndSpawnTrees(float delta) {
        float grassHeight = (grassTexture != null) ? grassTexture.getHeight() : 128f;
        float grassWidth = (grassTexture != null) ? grassTexture.getWidth() : 128f;
        float screenBottom = camera.position.y - camera.viewportHeight / 2f;
        float screenTop = camera.position.y + camera.viewportHeight / 2f;

        for (int i = trees.size - 1; i >= 0; i--) {
            TreeEntity tree = trees.get(i);
            tree.y -= grassSpeed * delta * 1;
            if (tree.y + tree.height < screenBottom) {
                trees.removeIndex(i);
            }
        }

        treeSpawnTimer += delta;
        if (treeSpawnTimer >= treeSpawnInterval) {
            treeSpawnTimer = 0f;
            for (int side = 0; side < 2; side++) {
                float x = (side == 0)
                    ? (camera.position.x - camera.viewportWidth / 3f) - grassWidth
                    : (camera.position.x + camera.viewportWidth / 3f);

                int numTrees = 1;
                for (int t = 0; t < numTrees; t++) {
                    float tx = x + (float)Math.random() * grassWidth * 0.7f;
                    float ty = screenTop + 30f + (float)Math.random() * 40f;
                    float scale = 0.18f + (float)Math.random() * 0.08f;
                    float tw = grassWidth * scale;
                    float th = grassHeight * scale;
                    trees.add(new TreeEntity(tx, ty, tw, th));
                }
            }
        }
    }

    private void renderGrassTrees() {
        if (treeTexture == null) return;
        sharedBatch.begin();
        sharedBatch.setProjectionMatrix(camera.combined);
        for (TreeEntity tree : trees) {
            sharedBatch.draw(treeTexture, tree.x, tree.y, tree.width, tree.height);
        }
        sharedBatch.end();
    }

    private void renderRails() {
        if (railTexture == null) return;

        // Dapatkan semua carriage yang sedang dimuat
        ImmutableArray<Entity> managers = engine.getEntitiesFor(
            Family.all(CarriageManagerComponent.class).get()
        );
        if (managers.size() == 0) return;
        Entity manager = managers.get(0);
        CarriageManagerComponent managerComp = managerMapper.get(manager);

        sharedBatch.begin();
        sharedBatch.setProjectionMatrix(camera.combined);

        // Ambil area layar kamera
        float screenLeft = camera.position.x - camera.viewportWidth / 2f;
        float screenBottom = camera.position.y - camera.viewportHeight / 2f;
        float screenWidth = camera.viewportWidth;
        float screenHeight = camera.viewportHeight;

        for (int carriageNum : managerComp.loadedCarriages.keys().toArray().toArray()) {
            Entity carriage = managerComp.getCarriage(carriageNum);
            CarriageBoundaryComponent boundary = boundaryMapper.get(carriage);
            if (boundary == null) continue;

            float carriageX = boundary.carriageBounds.x;
            float carriageWidth = boundary.carriageBounds.width;

            float railWidth = carriageWidth;
            float railHeight = railTexture.getHeight();

            float offsetY = -(railOffsetY % railHeight);
            if (offsetY > 0) offsetY -= railHeight;

            int numTiles = (int)Math.ceil((screenHeight + railHeight * 2) / railHeight);

            for (int i = 0; i < numTiles; i++) {
                float y = screenBottom + offsetY + i * railHeight;
                sharedBatch.draw(
                    railTexture,
                    carriageX,
                    y,
                    railWidth,
                    railHeight
                );
            }
        }

        sharedBatch.end();
    }

    private static class TreeEntity {
        float x, y, width, height;
        TreeEntity(float x, float y, float width, float height) {
            this.x = x; this.y = y; this.width = width; this.height = height;
        }
    }



    private void handleInput() {
        // if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
        //     Gdx.app.exit();
        // }
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

            float screenBottom = camera.position.y - camera.viewportHeight / 2f;
            float screenTop = camera.position.y + camera.viewportHeight / 2f;
            int numTiles = (int)Math.ceil((screenTop - screenBottom) / grassHeight) + 2;

            for (int side = 0; side < 2; side++) {
                float x = (side == 0)
                    ? carriageX - grassWidth // kiri
                    : carriageX + carriageWidth; // kanan

                float offsetY = -(grassOffsetY % grassHeight);
                if (offsetY > 0) offsetY -= grassHeight;

                for (int i = 0; i < numTiles; i++) {
                    float y = screenBottom + offsetY + i * grassHeight;
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

    // render lantai
    private void renderCarriageFloor(Entity carriage) {
        if (carriage == null || floorTexture == null) return;

        CarriageBoundaryComponent boundary = boundaryMapper.get(carriage);
        if (boundary == null) return;

        sharedBatch.begin();
        sharedBatch.setProjectionMatrix(camera.combined);

        float x = boundary.carriageBounds.x;
        float y = boundary.carriageBounds.y;
        float width = boundary.carriageBounds.width;
        float height = boundary.carriageBounds.height;

        sharedBatch.draw(floorTexture, x, y, width, height);

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

        if (pauseMenuOverlay != null) pauseMenuOverlay.resize(width, height);
        if (hudStage != null) hudStage.getViewport().update(width, height, true);
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

        if (pauseMenuOverlay != null) pauseMenuOverlay.dispose();
        if (hudStage != null) hudStage.dispose();
        if (cursorManager != null) cursorManager.dispose();
        if (batch != null) batch.dispose();
    }
}


package studio.jawa.bullettrain.systems.gameplay.enemies;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.level.OpenLayoutComponent;
import studio.jawa.bullettrain.components.level.TrainCarriageComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.factories.EnemyFactory;
import studio.jawa.bullettrain.data.GameConstants;

public class EnemySpawnSystem extends EntitySystem {

    private ComponentMapper<PlayerComponent> playerMapper;
    private ComponentMapper<TransformComponent> transformMapper;
    private ComponentMapper<TrainCarriageComponent> carriageMapper;
    private ComponentMapper<OpenLayoutComponent> layoutMapper;

    private ImmutableArray<Entity> playerEntities;
    private ImmutableArray<Entity> carriageEntities;

    private AssetManager assetManager;
    private Texture meleeEnemyTexture;
    private Texture rangedEnemyTexture;

    public EnemySpawnSystem(AssetManager assetManager) {
        this.assetManager = assetManager;

        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        carriageMapper = ComponentMapper.getFor(TrainCarriageComponent.class);
        layoutMapper = ComponentMapper.getFor(OpenLayoutComponent.class);

        loadEnemyTextures();
    }

    private void loadEnemyTextures() {
        if (assetManager.isLoaded("textures/enemies/melee_enemy.png")) {
            meleeEnemyTexture = assetManager.get("textures/enemies/melee_enemy.png", Texture.class);
        }
        if (assetManager.isLoaded("textures/enemies/ranged_enemy.png")) {
            rangedEnemyTexture = assetManager.get("textures/enemies/ranged_enemy.png", Texture.class);
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        playerEntities = engine.getEntitiesFor(
            Family.all(PlayerComponent.class, TransformComponent.class).get()
        );

        carriageEntities = engine.getEntitiesFor(
            Family.all(TrainCarriageComponent.class, OpenLayoutComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        if (playerEntities.size() == 0) return;

        Entity player = playerEntities.get(0);
        PlayerComponent playerComp = playerMapper.get(player);

        // Spawn enemies di carriage yang aktif
        spawnEnemiesInActiveCarriages(playerComp.currentCarriageNumber);
    }

    private void spawnEnemiesInActiveCarriages(int playerCarriageNumber) {
        // Spawn enemies dalam window 3 carriage
        int startCarriage = Math.max(1, playerCarriageNumber - 1);
        int endCarriage = Math.min(5, playerCarriageNumber + 1);

        for (Entity carriageEntity : carriageEntities) {
            TrainCarriageComponent carriage = carriageMapper.get(carriageEntity);
            OpenLayoutComponent layout = layoutMapper.get(carriageEntity);

            if (carriage == null || layout == null) continue;

            if (carriage.carriageNumber >= startCarriage && carriage.carriageNumber <= endCarriage) {
                if (carriage.carriageNumber == 1) {
                    generateSpawnPointsForCarriage(layout, carriage.carriageNumber);
                    continue;
                }
                spawnEnemiesFromLayout(layout, carriage);
            }
        }
    }

    private void spawnEnemiesFromLayout(OpenLayoutComponent layout, TrainCarriageComponent carriage) {
        // Check if textures are loaded
        if (meleeEnemyTexture == null || rangedEnemyTexture == null) {
            loadEnemyTextures();
            if (meleeEnemyTexture == null || rangedEnemyTexture == null) {
                System.out.println("⚠️ Enemy textures not loaded yet, skipping spawn");
                return;
            }
        }

        // Random enemy count based on carriage level
        int[] enemyRange = calculateEnemyRangeForCarriage(carriage.carriageNumber);
        int randomEnemyCount = MathUtils.random(enemyRange[0], enemyRange[1]);

        // Generate proper spawn points within carriage boundaries
        generateSpawnPointsForCarriage(layout, carriage.carriageNumber);

        int enemiesToSpawn = Math.min(randomEnemyCount, layout.enemySpawnPoints.size);

        // Spawn enemies dari spawn points yang sudah di-generate
        for (int i = 0; i < enemiesToSpawn; i++) {
            if (i < layout.enemyEntities.size) continue; 

            float x = layout.enemySpawnPoints.get(i).x;
            float y = layout.enemySpawnPoints.get(i).y;

            // Random enemy type 
            Entity enemy = createRandomEnemy(x, y, carriage.carriageNumber);

            if (enemy != null) {
                getEngine().addEntity(enemy);
                layout.enemyEntities.add(enemy);

                System.out.println("👹 Enemy spawned at (" + x + ", " + y + ") in Car " + carriage.carriageNumber);
            }
        }
    }

    private void generateSpawnPointsForCarriage(OpenLayoutComponent layout, int carriageNumber) {
        // Clear existing spawn points
        layout.enemySpawnPoints.clear();

        // Calculate carriage offset
        float carriageOffsetY = (carriageNumber - 1) * GameConstants.CARRIAGE_HEIGHT;

        // Calculate playable bounds
        float leftBound = (GameConstants.CARRIAGE_WIDTH - GameConstants.PLAYABLE_WIDTH) / 2f + 40f;
        float rightBound = leftBound + GameConstants.PLAYABLE_WIDTH - 80f;
        float bottomBound = carriageOffsetY + GameConstants.ENTRY_ZONE_HEIGHT + 40f;
        float topBound = carriageOffsetY + GameConstants.CARRIAGE_HEIGHT - GameConstants.EXIT_ZONE_HEIGHT - 40f;

        // Generate random spawn points within bounds
        int maxSpawnPoints = 8; 
        for (int i = 0; i < maxSpawnPoints; i++) {
            float x = MathUtils.random(leftBound, rightBound);
            float y = MathUtils.random(bottomBound, topBound);
            layout.enemySpawnPoints.add(new Vector2(x, y));
        }

        // System.out.println("🎯 Generated " + maxSpawnPoints + " spawn points for Car " + carriageNumber);
    }

    private int[] calculateEnemyRangeForCarriage(int carriageNumber) {
        // Simple progressive scaling sesuai request
        switch (carriageNumber) {
            case 1: return new int[]{2, 3}; // Gerbong 1: 2-3 enemies
            case 2: return new int[]{3, 4}; // Gerbong 2: 3-4 enemies
            case 3: return new int[]{4, 5}; // Gerbong 3: 4-5 enemies
            case 4: return new int[]{5, 6}; // Gerbong 4: 5-6 enemies
            case 5: return new int[]{6, 7}; // Gerbong 5: 6-7 enemies
            default: return new int[]{2, 3}; // Fallback untuk gerbong lain
        }
    }

    private Entity createRandomEnemy(float x, float y, int carriageNumber) {
        // Random tipe enemy: 60% melee, 40% ranged
        boolean isMelee = MathUtils.randomBoolean(0.6f);

        if (isMelee) {
            return EnemyFactory.createMeleeEnemy(x, y, carriageNumber, meleeEnemyTexture, assetManager, getEngine());
        } else {
            return EnemyFactory.createRangedEnemy(x, y, carriageNumber, rangedEnemyTexture, assetManager, getEngine());
        }
    }
}

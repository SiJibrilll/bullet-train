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
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.level.OpenLayoutComponent;
import studio.jawa.bullettrain.components.level.TrainCarriageComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.factories.EnemyFactory;

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

        // Hapus setSeed - biarkan LibGDX gunakan seed random bawaan
        // MathUtils.random.setSeed(System.currentTimeMillis()); // ← HAPUS INI

        playerMapper = ComponentMapper.getFor(PlayerComponent.class);
        transformMapper = ComponentMapper.getFor(TransformComponent.class);
        carriageMapper = ComponentMapper.getFor(TrainCarriageComponent.class);
        layoutMapper = ComponentMapper.getFor(OpenLayoutComponent.class);

        loadEnemyTextures();
    }

    private void loadEnemyTextures() {
        // Load enemy textures - sesuaikan path dengan CarriageTransitionSystem
        // Textures sudah di-load di CarriageTransitionSystem, kita gunakan yang sama
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

            // Hanya spawn di carriage dalam window
            if (carriage.carriageNumber >= startCarriage && carriage.carriageNumber <= endCarriage) {
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
        int enemiesToSpawn = Math.min(randomEnemyCount, layout.enemySpawnPoints.size);

        // Shuffle spawn points untuk variasi posisi
        layout.enemySpawnPoints.shuffle();

        // Spawn enemies dari spawn points yang sudah di-shuffle
        for (int i = 0; i < enemiesToSpawn; i++) {
            if (i < layout.enemyEntities.size) continue; // Enemy sudah di-spawn

            float x = layout.enemySpawnPoints.get(i).x;
            float y = layout.enemySpawnPoints.get(i).y;

            // Random enemy type untuk setiap enemy
            Entity enemy = createRandomEnemy(x, y, 1.0f);

            if (enemy != null) {
                getEngine().addEntity(enemy);
                layout.enemyEntities.add(enemy);

                System.out.println("👹 Enemy spawned at (" + x + ", " + y + ") in Car " + carriage.carriageNumber);
            }
        }
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

    private Entity createRandomEnemy(float x, float y, float difficultyMultiplier) {
        // Random tipe enemy: 60% melee, 40% ranged
        boolean isMelee = MathUtils.randomBoolean(0.6f);

        if (isMelee) {
            return EnemyFactory.createMeleeEnemy(x, y, meleeEnemyTexture);
        } else {
            return EnemyFactory.createRangedEnemy(x, y, rangedEnemyTexture);
        }
    }
}

package studio.jawa.bullettrain.generation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.level.OpenLayoutComponent;
import studio.jawa.bullettrain.data.CarriageType;
import studio.jawa.bullettrain.data.GameConstants;
import com.badlogic.gdx.utils.Array;

public class TrainCarriageGenerator {

    public static OpenLayoutComponent generateLayout(CarriageType carriageType, long seed, int carriageNumber) {
        // Set seed untuk consistent generation (SEMENTARA)
        MathUtils.random.setSeed(seed);

        OpenLayoutComponent layout = new OpenLayoutComponent();

        // Generate berdasarkan carriage type
        switch (carriageType) {
            case PASSENGER:
                generatePassengerLayout(layout, carriageNumber);
                break;
            case CARGO:
                generateCargoLayout(layout, carriageNumber);
                break;
            case DINING:
                generateDiningLayout(layout, carriageNumber);
                break;
            case ENGINE:
                generateEngineLayout(layout, carriageNumber);
                break;
            case CABOOSE:
                generateCabooseLayout(layout, carriageNumber);
                break;
        }

        // Add difficulty-based random elements
        addRandomElements(layout, carriageNumber);

        return layout;
    }

    private static void generatePassengerLayout(OpenLayoutComponent layout, int carriageNumber) {
        // Passenger: scattered obstacles, moderate enemies
        int obstacleCount = MathUtils.random(3, 5);
        int enemyCount = MathUtils.random(2, 3);
        int pickupCount = MathUtils.random(1, 2);

        generateRandomPositions(layout.obstaclePositions, obstacleCount);
        generateRandomPositions(layout.enemySpawnPoints, enemyCount);
        generateRandomPositions(layout.pickupSpawnPoints, pickupCount);
    }

    private static void generateCargoLayout(OpenLayoutComponent layout, int carriageNumber) {
        // Cargo: more obstacles, fewer enemies
        int obstacleCount = MathUtils.random(5, 8);
        int enemyCount = MathUtils.random(1, 2);
        int pickupCount = MathUtils.random(1, 3);

        generateRandomPositions(layout.obstaclePositions, obstacleCount);
        generateRandomPositions(layout.enemySpawnPoints, enemyCount);
        generateRandomPositions(layout.pickupSpawnPoints, pickupCount);
    }

    private static void generateDiningLayout(OpenLayoutComponent layout, int carriageNumber) {
        // Dining: balanced layout
        int obstacleCount = MathUtils.random(4, 6);
        int enemyCount = MathUtils.random(2, 4);
        int pickupCount = MathUtils.random(2, 3);

        generateRandomPositions(layout.obstaclePositions, obstacleCount);
        generateRandomPositions(layout.enemySpawnPoints, enemyCount);
        generateRandomPositions(layout.pickupSpawnPoints, pickupCount);
    }

    private static void generateEngineLayout(OpenLayoutComponent layout, int carriageNumber) {
        // Engine: boss area, fewer but stronger
        int obstacleCount = MathUtils.random(3, 5);
        int enemyCount = MathUtils.random(1, 2); // Fewer but stronger enemies
        int pickupCount = MathUtils.random(2, 3);

        generateRandomPositions(layout.obstaclePositions, obstacleCount);
        generateRandomPositions(layout.enemySpawnPoints, enemyCount);
        generateRandomPositions(layout.pickupSpawnPoints, pickupCount);
    }

    private static void generateCabooseLayout(OpenLayoutComponent layout, int carriageNumber) {
        // Caboose: final challenge
        int obstacleCount = MathUtils.random(4, 7);
        int enemyCount = MathUtils.random(3, 5);
        int pickupCount = MathUtils.random(1, 2);

        generateRandomPositions(layout.obstaclePositions, obstacleCount);
        generateRandomPositions(layout.enemySpawnPoints, enemyCount);
        generateRandomPositions(layout.pickupSpawnPoints, pickupCount);
    }

    private static void generateRandomPositions(Array<Vector2> positions, int count) {
        float minX = (GameConstants.CARRIAGE_WIDTH - GameConstants.PLAYABLE_WIDTH) / 2f + 50f;
        float maxX = minX + GameConstants.PLAYABLE_WIDTH - 50f;
        float minY = GameConstants.ENTRY_ZONE_HEIGHT + 50f;
        float maxY = GameConstants.CARRIAGE_HEIGHT - GameConstants.EXIT_ZONE_HEIGHT - 50f;

        for (int i = 0; i < count; i++) {
            Vector2 position = new Vector2();

            // Generate position dengan minimum distance dari existing positions
            boolean validPosition = false;
            int attempts = 0;

            while (!validPosition && attempts < 20) {
                position.set(MathUtils.random(minX, maxX), MathUtils.random(minY, maxY));

                validPosition = true;
                // Check distance dari existing positions
                for (Vector2 existing : positions) {
                    if (position.dst(existing) < 80f) { // Minimum distance 80 units
                        validPosition = false;
                        break;
                    }
                }
                attempts++;
            }

            positions.add(new Vector2(position));
        }
    }

    private static void addRandomElements(OpenLayoutComponent layout, int carriageNumber) {
        // Add extra random elements based on difficulty
        int extraElements = carriageNumber / 2; // More elements in later carriages

        for (int i = 0; i < extraElements; i++) {
            float chance = MathUtils.random();

            if (chance < 0.5f) {
                // Add extra obstacle
                generateRandomPositions(layout.obstaclePositions, 1);
            } else if (chance < 0.8f) {
                // Add extra enemy
                generateRandomPositions(layout.enemySpawnPoints, 1);
            } else {
                // Add extra pickup
                generateRandomPositions(layout.pickupSpawnPoints, 1);
            }
        }
    }
}

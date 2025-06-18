package studio.jawa.bullettrain.generation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import studio.jawa.bullettrain.components.level.OpenLayoutComponent;
import studio.jawa.bullettrain.data.CarriageType;
import studio.jawa.bullettrain.data.GameConstants;
import studio.jawa.bullettrain.data.ObjectType;
import com.badlogic.gdx.utils.Array;

public class TrainCarriageGenerator {

    public static OpenLayoutComponent generateLayout(CarriageType carriageType, long seed, int carriageNumber) {


        OpenLayoutComponent layout = new OpenLayoutComponent();

        // Generate objects 
        generateRandomLayout(layout, carriageNumber);

        return layout;
    }

    private static void generateRandomLayout(OpenLayoutComponent layout, int carriageNumber) {
        int objectCount = MathUtils.random(4, 6); 
        
        // NEW: Generate enemy spawn points 
        int enemySpawnPointCount = 15;
        int pickupCount = MathUtils.random(1, 2);

        generateRandomObjectPositions(layout.obstaclePositions, objectCount);
        generateRandomPositions(layout.enemySpawnPoints, enemySpawnPointCount); 
        generateRandomPositions(layout.pickupSpawnPoints, pickupCount);
    }



    // Generate positions for objects 
    private static void generateRandomObjectPositions(Array<Vector2> positions, int count) {
        generateRandomPositions(positions, count);
    }

    private static void generateRandomPositions(Array<Vector2> positions, int count) {
        float minX = (GameConstants.CARRIAGE_WIDTH - GameConstants.PLAYABLE_WIDTH) / 2f + 50f;
        float maxX = minX + GameConstants.PLAYABLE_WIDTH - 100f;
        float minY = GameConstants.ENTRY_ZONE_HEIGHT + 50f;
        float maxY = GameConstants.CARRIAGE_HEIGHT - GameConstants.EXIT_ZONE_HEIGHT - 50f;

        for (int i = 0; i < count; i++) {
            Vector2 position = new Vector2();
            boolean validPosition = false;
            int attempts = 0;

            while (!validPosition && attempts < 20) {
                position.set(MathUtils.random(minX, maxX), MathUtils.random(minY, maxY));

                validPosition = true;
                for (Vector2 existing : positions) {
                    if (position.dst(existing) < 80f) {
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
        int extraObjects = MathUtils.random(0, 2);

        for (int i = 0; i < extraObjects; i++) {
            generateRandomObjectPositions(layout.obstaclePositions, 1);
        }

    }
}

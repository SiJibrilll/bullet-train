package studio.jawa.bullettrain.factories;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import studio.jawa.bullettrain.components.gameplays.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.entities.enemies.EnemyEntity;


public class EnemyFactory {
    public static Entity createMeleeEnemy(float x, float y, Texture tex) {
        GeneralStatsComponent enemystat = new GeneralStatsComponent(10, 200, 500);
        EnemyBehaviourComponent behaviour = new EnemyBehaviourComponent(500, 100, 1.5f, false);
        return new EnemyEntity(x, y, tex, EnemyStateComponent.STATES.IDLE, behaviour, enemystat);
    }

    public static Entity createRangedEnemy(float x, float y, Texture tex) {
        GeneralStatsComponent enemystat = new GeneralStatsComponent(10, 200, 200);
        EnemyBehaviourComponent behaviour = new EnemyBehaviourComponent(500, 1000, 1.5f, true);
        return new EnemyEntity(x, y, tex, EnemyStateComponent.STATES.IDLE, behaviour, enemystat);
    }
}

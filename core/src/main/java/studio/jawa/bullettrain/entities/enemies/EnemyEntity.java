package studio.jawa.bullettrain.entities.enemies;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import studio.jawa.bullettrain.components.gameplays.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class EnemyEntity extends Entity {
    public EnemyEntity(float x, float y, int carriageNumber, Texture tex, EnemyStateComponent.STATES state, EnemyBehaviourComponent behaviour, GeneralStatsComponent stats) {
        add(new TransformComponent(x, y));
        add(new VelocityComponent());
        
        // Create EnemyComponent with carriage number
        EnemyComponent enemyComp = new EnemyComponent();
        enemyComp.carriageNumber = carriageNumber;
        add(enemyComp);
        
        add(new EnemyStateComponent(state));
        add(behaviour);
        add(stats);
        
        // Set ukuran sprite enemy agar tidak terlalu besar
        Sprite enemySprite = new Sprite(tex);
        enemySprite.setSize(64f, 64f); // Ukuran 64x64 pixels (sesuaikan sesuai kebutuhan)
        add(new SpriteComponent(enemySprite));
    }

}

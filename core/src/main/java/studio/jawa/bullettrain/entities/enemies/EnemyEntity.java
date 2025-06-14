package studio.jawa.bullettrain.entities.enemies;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplay.TeamComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.components.technicals.BoxColliderComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class EnemyEntity extends Entity {
    public EnemyEntity(float x, float y, int carriageNumber, Texture tex, EnemyStateComponent.STATES state, EnemyBehaviourComponent behaviour, GeneralStatsComponent stats, AnimationComponent animation) {
        add(new TransformComponent(x, y));
        add(new VelocityComponent());
        
        // Create EnemyComponent with carriage number
        EnemyComponent enemyComp = new EnemyComponent();
        enemyComp.carriageNumber = carriageNumber;
        add(enemyComp);
        
        add(new EnemyStateComponent(state));
        add(behaviour);
        add(stats);
        
        
        Sprite enemySprite = new Sprite(tex);
        add(new SpriteComponent(enemySprite));
        add(animation);
        add(new BoxColliderComponent(new Rectangle(-50, -65, 100, 100)));
        add(new TeamComponent(ProjectileComponent.Team.ENEMY));
    }

}

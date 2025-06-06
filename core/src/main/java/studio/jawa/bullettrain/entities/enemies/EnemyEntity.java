package studio.jawa.bullettrain.entities.enemies;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import studio.jawa.bullettrain.components.gameplays.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplays.TeamComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyComponent;
import studio.jawa.bullettrain.components.gameplays.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.components.gameplays.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.BoxColliderComponent;
import studio.jawa.bullettrain.components.technicals.SpriteComponent;
import studio.jawa.bullettrain.components.technicals.TransformComponent;
import studio.jawa.bullettrain.components.technicals.VelocityComponent;

public class EnemyEntity extends Entity {
    public EnemyEntity(float x, float y, Texture tex, EnemyStateComponent.STATES state, EnemyBehaviourComponent behaviour, GeneralStatsComponent stats) {
        add(new TransformComponent(x, y));
        add(new VelocityComponent());
        add(new EnemyComponent());
        add(new EnemyStateComponent(state));
        add(behaviour);
        add(stats);
        add(new SpriteComponent(new Sprite(tex)));
        add(new BoxColliderComponent(new Rectangle(-50, -65, 100, 100)));
        add(new TeamComponent(ProjectileComponent.Team.ENEMY));
    }

}

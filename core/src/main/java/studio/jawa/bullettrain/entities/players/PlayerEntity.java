package studio.jawa.bullettrain.entities.players;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import studio.jawa.bullettrain.components.gameplays.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplays.TeamComponent;
import studio.jawa.bullettrain.components.gameplays.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.*;

public class PlayerEntity extends Entity {
    public PlayerEntity(float x, float y, Texture tex, GeneralStatsComponent stats) {
        add(new TransformComponent(x, y));
        add(new SpriteComponent(new Sprite(tex)));
        add(new VelocityComponent());
        add(new PlayerControlledComponent());
        add(new InputComponent());
        add(stats);
        add(new BoxColliderComponent(new Rectangle(-50, -65, 100, 100)));
        add(new TeamComponent(ProjectileComponent.Team.PLAYER));
    }
}

package studio.jawa.bullettrain.entities.players;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplay.TeamComponent;
import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent;
import studio.jawa.bullettrain.components.technicals.*;
import studio.jawa.bullettrain.data.characters.BaseCharacter;
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;

public class PlayerEntity extends Entity {
    public PlayerEntity(float x, float y, Texture tex, GeneralStatsComponent stats, BaseCharacter character, AnimationComponent animation) {
        TransformComponent transform = new TransformComponent(x, y);
        transform.scale = 2f; //TODO sementara untuk nyesuain size
        add(transform);
        add(new SpriteComponent(new Sprite(tex)));
        add(new VelocityComponent());
        add(new PlayerControlledComponent());
        add(new InputComponent());
        PlayerComponent pc = new PlayerComponent();
        pc.character = character;
        add(pc);
        add(stats);
        add(animation);
        add(new BoxColliderComponent(new Rectangle(-50, -65, 100, 100)));
        add(new TeamComponent(ProjectileComponent.Team.PLAYER));
    }
}

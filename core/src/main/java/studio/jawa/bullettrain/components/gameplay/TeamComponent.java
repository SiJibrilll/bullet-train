package studio.jawa.bullettrain.components.gameplay;

import com.badlogic.ashley.core.Component;
import studio.jawa.bullettrain.components.gameplay.projectiles.ProjectileComponent;

public class TeamComponent implements Component {
    public ProjectileComponent.Team team;
    public TeamComponent(ProjectileComponent.Team team) {
        this.team = team;
    }
}


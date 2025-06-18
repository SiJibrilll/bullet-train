package studio.jawa.bullettrain.components.gameplay.projectiles;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class ProjectileComponent implements Component {
    public float speed = 0;
    public boolean isMeele = false;
    public float meleeDuration = 0;
    public Entity owner;   // Who fired the bullet
    public Team team;
    public Set<Entity> hitEntities = new HashSet<>();
    public float damage;

    public enum Team {
        PLAYER,
        ENEMY
    }

    public ProjectileComponent(float speed,Team team) {
        this.speed = speed;
        this.team = team;
        this.isMeele = false;
    }

    public ProjectileComponent(Team team, boolean isMeele, float meleeDuration) {
        this.isMeele = isMeele;
        this.team  = team;
        this.meleeDuration = meleeDuration;
    }
}

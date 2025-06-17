package studio.jawa.bullettrain.systems.gameplay;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;

import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.data.characters.BaseCharacter;
import studio.jawa.bullettrain.data.characters.GraceCharacter;
import studio.jawa.bullettrain.data.characters.JingCharacter;
import studio.jawa.bullettrain.screens.uiscreens.CharacterInfo;
import studio.jawa.bullettrain.screens.uiscreens.HudStage;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;



public class PlayerHealthSystem extends IteratingSystem {

    private ComponentMapper<GeneralStatsComponent> statsMapper = ComponentMapper.getFor(GeneralStatsComponent.class);
    private HudStage hud;
    private BaseCharacter selected;

    public PlayerHealthSystem(HudStage hud, CharacterInfo selection) {
        super(Family.all(PlayerControlledComponent.class, GeneralStatsComponent.class).get());
        this.hud = hud;
        switch (selection.name) {
            case "Grace":
                selected = new GraceCharacter();
                break;
            case "Jing Wei":
                selected = new JingCharacter();
                break;
            default:
                selected = new JingCharacter();
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        GeneralStatsComponent stats = statsMapper.get(entity);
        GeneralStatsComponent baseStat = selected.getStat();
        float healthPercentage = (stats.health / baseStat.health) * 100f;

        hud.setHealth(healthPercentage);

        if (stats.health <= 0) {
            stats.health = 0;
            // Handle death logic
            System.out.println("Player has died!");
            // You could dispatch events or remove the entity here
        }
    }
}


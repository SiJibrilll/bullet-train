package studio.jawa.bullettrain.systems.gameplay;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;

import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.technicals.PlayerControlledComponent;
import studio.jawa.bullettrain.data.characters.BaseCharacter;
import studio.jawa.bullettrain.data.characters.GraceCharacter;
import studio.jawa.bullettrain.data.characters.JingCharacter;
import studio.jawa.bullettrain.screens.uiscreens.CharacterInfo;
import studio.jawa.bullettrain.screens.uiscreens.GameOverScreen;
import studio.jawa.bullettrain.screens.uiscreens.HudStage;
import studio.jawa.bullettrain.screens.uiscreens.LoadingScreen;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;



public class PlayerHealthSystem extends IteratingSystem {

    private ComponentMapper<GeneralStatsComponent> statsMapper = ComponentMapper.getFor(GeneralStatsComponent.class);
    private HudStage hud;
    private BaseCharacter selected;
    private Float timer = 2f;
    private Game game;
    private AssetManager assetManager;
    private CharacterInfo selection;

    public PlayerHealthSystem(HudStage hud, CharacterInfo selection, Game game, AssetManager assetManager) {
        super(Family.all(PlayerControlledComponent.class, GeneralStatsComponent.class).get());
        this.hud = hud;
        this.game = game;
        this.assetManager = assetManager;
        this.selection = selection;
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
            timer -= 1 * deltaTime;

            if (timer <= 0) {
                game.setScreen(new GameOverScreen(game, selection, assetManager));
            }
            // Handle death logic
            // System.out.println("Player has died!");
            // You could dispatch events or remove the entity here
        }
    }
}


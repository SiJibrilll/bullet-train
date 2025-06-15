package studio.jawa.bullettrain.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyBehaviourComponent;
import studio.jawa.bullettrain.components.gameplay.enemies.EnemyStateComponent;
import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.entities.enemies.EnemyEntity;
import studio.jawa.bullettrain.entities.objects.WeaponEntity;


public class EnemyFactory {
    public static Entity createMeleeEnemy(float x, float y, int carriageNumber, Texture tex, AssetManager assetManager, Engine engine) {
        GeneralStatsComponent enemystat = new GeneralStatsComponent(10, 200, 500);
        EnemyBehaviourComponent behaviour = new EnemyBehaviourComponent(500, 100, 1.5f, false);

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put("idle", AnimationComponent.loadAnimation(assetManager, "testing/animation/idle.png", 4, 0.2f));
        anim.animations.put("run", AnimationComponent.loadAnimation(assetManager, "testing/animation/run.png", 8, 0.1f));
        anim.animations.put("death", AnimationComponent.loadAnimation(assetManager, "testing/animation/death.png", 6, 0.15f));
        anim.currentAnimation = "idle";

        Entity enemy = new EnemyEntity(x, y, carriageNumber, tex, EnemyStateComponent.STATES.IDLE, behaviour, enemystat, anim);
        Texture weapon = assetManager.get("testing/sword.png", Texture.class);
        engine.addEntity(new WeaponEntity(x, y, weapon, false, enemy, 2f));
   
        return enemy;
    }

    public static Entity createRangedEnemy(float x, float y, int carriageNumber, Texture tex, AssetManager assetManager, Engine engine) {
        GeneralStatsComponent enemystat = new GeneralStatsComponent(10, 200, 200);
        EnemyBehaviourComponent behaviour = new EnemyBehaviourComponent(1000, 1000, 1.5f, true);

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put("idle", AnimationComponent.loadAnimation(assetManager, "testing/animation/idle.png", 4, 0.2f));
        anim.animations.put("run", AnimationComponent.loadAnimation(assetManager, "testing/animation/run.png", 8, 0.1f));
        anim.animations.put("death", AnimationComponent.loadAnimation(assetManager, "testing/animation/death.png", 6, 0.15f));
        anim.currentAnimation = "idle";

        Entity enemy = new EnemyEntity(x, y, carriageNumber, tex, EnemyStateComponent.STATES.IDLE, behaviour, enemystat, anim);
        Texture weapon = assetManager.get("testing/gun.png", Texture.class);
        engine.addEntity(new WeaponEntity(x, y, weapon, false, enemy, 1f, 50f));
   
        return enemy;

        // return new EnemyEntity(x, y, carriageNumber, tex, EnemyStateComponent.STATES.IDLE, behaviour, enemystat, anim);
    }
}

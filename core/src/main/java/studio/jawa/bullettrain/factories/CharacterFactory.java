package studio.jawa.bullettrain.factories;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.data.characters.GraceCharacter;
import studio.jawa.bullettrain.data.characters.JingCharacter;
import studio.jawa.bullettrain.data.characters.BaseCharacter.Animation;
import studio.jawa.bullettrain.entities.objects.WeaponEntity;
import studio.jawa.bullettrain.entities.players.PlayerEntity;

public class CharacterFactory {
    static PlayerEntity createGrace(float x, float y, AssetManager assetManager, Engine engine) {
        GraceCharacter grace = new GraceCharacter();
        GeneralStatsComponent stats = grace.getStat();
        Texture idle = assetManager.get(grace.getAnim(Animation.IDLE), Texture.class);
        AnimationComponent animation = new AnimationComponent();

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put("idle", AnimationComponent.loadAnimation(assetManager, "testing/animation/idle.png", 4, 0.2f));
        anim.animations.put("run", AnimationComponent.loadAnimation(assetManager, "testing/animation/run.png", 8, 0.1f));
        anim.animations.put("death", AnimationComponent.loadAnimation(assetManager, "testing/animation/death.png", 6, 0.15f));
        anim.currentAnimation = "idle";

        PlayerEntity player = new PlayerEntity(x, y, idle, stats, grace, anim);

        Texture weapon = assetManager.get("testing/gun.png", Texture.class);
        engine.addEntity(new WeaponEntity(x, y, weapon, true, player, 1f, 50f));

        return player;
    }

    static PlayerEntity createJing(float x, float y, AssetManager assetManager, Engine engine) {
        JingCharacter jing = new JingCharacter();
        GeneralStatsComponent stats = jing.getStat();
        Texture idle = assetManager.get(jing.getAnim(Animation.IDLE), Texture.class);

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put("idle", AnimationComponent.loadAnimation(assetManager, "testing/animation/idle.png", 4, 0.2f));
        anim.animations.put("run", AnimationComponent.loadAnimation(assetManager, "testing/animation/run.png", 8, 0.1f));
        anim.animations.put("death", AnimationComponent.loadAnimation(assetManager, "testing/animation/death.png", 6, 0.15f));
        anim.currentAnimation = "idle";

        PlayerEntity player = new PlayerEntity(x, y, idle, stats, jing, anim);
        Texture weapon = assetManager.get("testing/sword.png", Texture.class);
        engine.addEntity(new WeaponEntity(x, y, weapon, true, player, 2f));

        return player;
    }
}

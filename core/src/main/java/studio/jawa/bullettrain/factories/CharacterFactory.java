package studio.jawa.bullettrain.factories;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import studio.jawa.bullettrain.components.gameplay.GeneralStatsComponent;
import studio.jawa.bullettrain.components.gameplay.palyers.PlayerComponent;
import studio.jawa.bullettrain.components.technicals.AnimationComponent;
import studio.jawa.bullettrain.data.characters.GraceCharacter;
import studio.jawa.bullettrain.data.characters.JingCharacter;
import studio.jawa.bullettrain.data.characters.BaseCharacter.Animation;;
import studio.jawa.bullettrain.entities.players.PlayerEntity;

public class CharacterFactory {
    static PlayerEntity createGrace(float x, float y, AssetManager assetManager) {
        GraceCharacter grace = new GraceCharacter();
        GeneralStatsComponent stats = grace.getStat();
        Texture idle = assetManager.get(grace.getAnim(Animation.IDLE), Texture.class);
        AnimationComponent animation = new AnimationComponent();

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put("idle", AnimationComponent.loadAnimation(assetManager, "testing/animation/idle.png", 4, 0.2f));
        anim.animations.put("run", AnimationComponent.loadAnimation(assetManager, "testing/animation/run.png", 8, 0.1f));
        anim.animations.put("death", AnimationComponent.loadAnimation(assetManager, "testing/animation/death.png", 6, 0.15f));
        anim.currentAnimation = "idle";

        return new PlayerEntity(x, y, idle, stats, grace, anim);
    }

    static PlayerEntity createJing(float x, float y, AssetManager assetManager) {
        JingCharacter jing = new JingCharacter();
        GeneralStatsComponent stats = jing.getStat();
        Texture idle = assetManager.get(jing.getAnim(Animation.IDLE), Texture.class);

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put("idle", AnimationComponent.loadAnimation(assetManager, "testing/animation/idle.png", 4, 0.2f));
        anim.animations.put("run", AnimationComponent.loadAnimation(assetManager, "testing/animation/run.png", 8, 0.1f));
        anim.animations.put("death", AnimationComponent.loadAnimation(assetManager, "testing/animation/death.png", 6, 0.15f));
        anim.currentAnimation = "idle";

        return new PlayerEntity(x, y, idle, stats, jing, anim);
    }
}

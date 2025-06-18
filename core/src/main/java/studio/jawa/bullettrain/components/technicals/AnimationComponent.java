package studio.jawa.bullettrain.components.technicals;

import java.util.Map;
import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent implements Component {
    public Map<String, Animation<TextureRegion>> animations = new HashMap<>();
    public String currentAnimation = "idle";
    public float stateTime = 0f;
    public TextureRegion currentFrame;
    public String queuedAnimation = null; // used for after non-looping animations
    public boolean looping = true;
    public boolean isPlaying = true; // NEW! controls whether to advance animation

    /**
     * Helper to create an animation from a sprite sheet.
     * @param assetManager Your AssetManager instance
     * @param path Path to the sprite sheet (e.g., "player/walk.png")
     * @param frameCount Number of frames in the sheet
     * @param frameDuration Time per frame
     * @return A LibGDX Animation<TextureRegion>
     */
    public static Animation<TextureRegion> loadAnimation(AssetManager assetManager, String path, int frameCount, float frameDuration) {
        Texture sheet = assetManager.get(path, Texture.class);
        int frameWidth = sheet.getWidth() / frameCount;
        int frameHeight = sheet.getHeight() / 3; //TODO for testing purpose its divided by 3

        TextureRegion[][] split = TextureRegion.split(sheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = split[0][i]; // assume one row
        }

        return new Animation<>(frameDuration, frames);
    }

    public static Animation<TextureRegion> loadAnimation(AssetManager assetManager, String path, int frameCount, float frameDuration, Boolean debug) {
        Texture sheet = assetManager.get(path, Texture.class);
        int frameWidth = sheet.getWidth() / frameCount;
        int frameHeight = sheet.getHeight();

        TextureRegion[][] split = TextureRegion.split(sheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = split[0][i]; // assume one row
        }

        return new Animation<>(frameDuration, frames);
    }

    public static void playAnimation(AnimationComponent anim, String name, boolean looping) {
        if (!anim.currentAnimation.equals(name)) {
            anim.currentAnimation = name;
            anim.stateTime = 0f;
            anim.looping = looping;
            anim.queuedAnimation = null; // clear queued state unless explicitly set
        }
    }

    public static void playOnceThen(AnimationComponent anim, String name, String next) {
        anim.currentAnimation = name;
        anim.stateTime = 0f;
        anim.looping = false;
        anim.queuedAnimation = next;
    }

}


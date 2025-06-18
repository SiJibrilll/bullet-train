package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class CharacterInfo {
    public final String name, info1, info2;
    public final Texture portrait;
    public Actor actor;

    public CharacterInfo(String name, Texture portrait, String info1, String info2) {
        this.name = name;
        this.portrait = portrait;
        this.info1 = info1;
        this.info2 = info2;
    }

//    public CharacterInfo(String name, String info1, String info2) {
//        this.name = name;
//        this.info1 = info1;
//        this.info2 = info2;
//    }
}

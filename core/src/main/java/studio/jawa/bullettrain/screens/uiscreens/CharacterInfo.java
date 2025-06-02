package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.graphics.Texture;

public class CharacterInfo {
    public final String name;
//    public final Texture portrait;
    public final String info1;
    public final String info2;

//    public CharacterInfo(String name, Texture portrait, String info1, String info2) {
//        this.name = name;
//        this.portrait = portrait;
//        this.info1 = info1;
//        this.info2 = info2;
//    }
    public CharacterInfo(String name, String info1, String info2) {
        this.name = name;
        this.info1 = info1;
        this.info2 = info2;
    }
}

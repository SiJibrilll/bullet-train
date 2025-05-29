package studio.jawa.bullettrain;

import com.badlogic.gdx.Game;
import studio.jawa.bullettrain.screens.gamescreens.FirstScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}

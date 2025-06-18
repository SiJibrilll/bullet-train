package studio.jawa.bullettrain;

import com.badlogic.gdx.Game;

import studio.jawa.bullettrain.helpers.AudioHelper;
import studio.jawa.bullettrain.screens.gamescreens.EndScreen;
import com.badlogic.gdx.assets.AssetManager;
import studio.jawa.bullettrain.screens.gamescreens.GamePlayTestScreen;
import studio.jawa.bullettrain.screens.uiscreens.LoadingScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        AudioHelper.loadMusic("main", "music/main.mp3", true, 1);
        AudioHelper.loadMusic("game", "music/game.mp3", true, 1);
        AssetManager assetManager = new AssetManager();
        setScreen(new LoadingScreen(this, assetManager));
    }
}

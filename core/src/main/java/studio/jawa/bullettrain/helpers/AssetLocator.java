package studio.jawa.bullettrain.helpers;

import com.badlogic.gdx.assets.AssetManager;

public class AssetLocator {
    private static AssetManager manager;

    public static void setAssetManager(AssetManager m) {
        manager = m;
    }

    public static AssetManager get() {
        return manager;
    }
}

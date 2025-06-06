package studio.jawa.bullettrain.screens.gamescreens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import com.badlogic.gdx.utils.viewport.Viewport;
import studio.jawa.bullettrain.components.gameplays.GeneralStatsComponent;
import studio.jawa.bullettrain.entities.players.PlayerEntity;
import studio.jawa.bullettrain.factories.EnemyFactory;
import studio.jawa.bullettrain.helpers.AssetLocator;
import studio.jawa.bullettrain.systems.gameplays.enemies.EnemyChaseSystem;
import studio.jawa.bullettrain.systems.gameplays.enemies.EnemyIdleSystem;
import studio.jawa.bullettrain.systems.gameplays.enemies.EnemyStrafeSystem;
import studio.jawa.bullettrain.systems.projectiles.PlayerProjectileSpawningSystem;
import studio.jawa.bullettrain.systems.technicals.*;

/** First screen of the application. Displayed after the application is created. */
public class TestScreen implements Screen {
    private Engine engine;
    private OrthographicCamera camera;
    private Viewport viewport;

    @Override
    public void show() {
        engine = new Engine();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        viewport = new ExtendViewport(camera.viewportWidth, camera.viewportHeight, camera);
        camera.update();


        AssetManager manager = new AssetManager();
        AssetLocator.setAssetManager(manager);

        manager.load("testing/dummy.png", Texture.class);
        manager.load("testing/dummy2.png", Texture.class);
        manager.load("testing/bullet.png", Texture.class);
        manager.load("testing/slash.png", Texture.class);

        manager.finishLoading();

        Texture tex = manager.get("testing/dummy.png", Texture.class);
        Texture enemytex = manager.get("testing/dummy2.png", Texture.class);
        Texture bulletTex = manager.get("testing/bullet.png", Texture.class);
        Texture slashTex = manager.get("testing/slash.png", Texture.class);

        GeneralStatsComponent stat = new GeneralStatsComponent(10, 500);
        PlayerEntity player = new PlayerEntity(50, 50, tex, stat);

        Entity enemy = EnemyFactory.createRangedEnemy(200, 200, enemytex);
        Entity enemy2 = EnemyFactory.createMeleeEnemy(300, 300, enemytex);

        engine.addEntity(player);
        engine.addEntity(enemy2);
        engine.addEntity(enemy);
        engine.addSystem(new InputMovementSystem(engine));
        engine.addSystem(new EnemyIdleSystem(engine));
        engine.addSystem(new EnemyChaseSystem());
        engine.addSystem(new EnemyStrafeSystem(manager, engine));
        engine.addSystem(new MovementSystem(engine));
        engine.addSystem(new PlayerProjectileSpawningSystem(camera, engine, bulletTex));
        engine.addSystem(new ProjectileCollisionSystem(engine));


        engine.addSystem(new RenderingSystem(camera));
        engine.addSystem(new DebugRenderSystem(camera, engine));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}

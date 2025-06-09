package studio.jawa.bullettrain.screens.gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import studio.jawa.bullettrain.screens.uiscreens.CharacterInfo;
import studio.jawa.bullettrain.screens.uiscreens.CursorManager;
import studio.jawa.bullettrain.screens.uiscreens.GameOverScreen;
import studio.jawa.bullettrain.screens.uiscreens.HudStage;
import studio.jawa.bullettrain.screens.uiscreens.PauseMenuOverlay;

public class TestGameScreen implements Screen {
    private final Game game;
    private final CharacterInfo selectedCharacter;
    private final AssetManager assetManager;

    private PauseMenuOverlay pauseMenuOverlay;
    private HudStage hudStage;
    private SpriteBatch batch;

    private CursorManager cursorManager;

    private float elapsedTime = 0f;

    private int ammo = 10;
    private final int maxAmmo = 10;

    public TestGameScreen(Game game, CharacterInfo selectedCharacter, AssetManager assetManager) {
        this.game = game;
        this.selectedCharacter = selectedCharacter;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        System.out.println("Character dipilih: " + selectedCharacter.name);

        pauseMenuOverlay = new PauseMenuOverlay(game, assetManager);
        hudStage = new HudStage(new ScreenViewport());

        Gdx.input.setInputProcessor(hudStage);

        batch = new SpriteBatch();
        cursorManager = new CursorManager(assetManager);
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        if (elapsedTime > 9999f) {
            game.setScreen(new GameOverScreen(game, selectedCharacter, assetManager));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (pauseMenuOverlay.isVisible()) {
                pauseMenuOverlay.hide();
                Gdx.input.setInputProcessor(hudStage);
            } else {
                pauseMenuOverlay.show();
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !cursorManager.isReloading()) {
            if (ammo > 0) {
                ammo--;
                System.out.println("Sisa peluru: " + ammo);
            }
            if (ammo == 0) {
                cursorManager.startReload();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && !cursorManager.isReloading()) {
            cursorManager.startReload();
            ammo = 0;
        }

        if (!cursorManager.isReloading() && ammo == 0) {
            ammo = maxAmmo;
            System.out.println("Reload selesai! " + ammo);
        }

        cursorManager.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!pauseMenuOverlay.isVisible()) {
            hudStage.act(delta);
            hudStage.draw();
            cursorManager.render(hudStage.getBatch());
        }
    }

    @Override
    public void resize(int width, int height) {
        if (pauseMenuOverlay != null) pauseMenuOverlay.resize(width, height);
        if (hudStage != null) hudStage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (pauseMenuOverlay != null) pauseMenuOverlay.dispose();
        if (hudStage != null) hudStage.dispose();
        if (cursorManager != null) cursorManager.dispose();
        if (batch != null) batch.dispose();
    }
}

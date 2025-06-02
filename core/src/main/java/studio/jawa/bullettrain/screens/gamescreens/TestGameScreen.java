package studio.jawa.bullettrain.screens.gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import studio.jawa.bullettrain.screens.uiscreens.CharacterInfo;
import studio.jawa.bullettrain.screens.uiscreens.GameOverScreen;
import studio.jawa.bullettrain.screens.uiscreens.HudStage;
import studio.jawa.bullettrain.screens.uiscreens.PauseMenuOverlay;

public class TestGameScreen implements Screen {
    private final Game game;
    private final CharacterInfo selectedCharacter;
    private PauseMenuOverlay pauseMenuOverlay;
    private final AssetManager assetManager;
    private HudStage hudStage;
    private float elapsedTime = 0;

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
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;
        if (elapsedTime > 5f) {
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (pauseMenuOverlay.isVisible()) {
            pauseMenuOverlay.render(delta);
        } else {
            hudStage.act(delta);
            hudStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (pauseMenuOverlay != null) {
            pauseMenuOverlay.resize(width, height);
        }

        if (hudStage != null) {
            hudStage.getViewport().update(width, height, true);
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (pauseMenuOverlay != null) pauseMenuOverlay.dispose();
        if (hudStage != null) hudStage.dispose();
    }
}

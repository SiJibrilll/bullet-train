package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SplashScreen implements Screen {
    private final Game game;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;
    private MainMenuScreen mainMenuScreen;
    private Stage mainMenuStage;

    public SplashScreen(Game game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = assetManager.get("ui/uiskin.json", Skin.class);

        Gdx.gl.glClearColor(0, 0, 0, 1);

        Label studioLabel = new Label("Studio Jawa", skin);
        studioLabel.setFontScale(5f);
        studioLabel.setAlignment(Align.center);
        studioLabel.setVisible(false);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(studioLabel).expand().center();
        stage.addActor(table);

        mainMenuScreen = new MainMenuScreen(game, assetManager);
        mainMenuScreen.show();
        mainMenuStage = mainMenuScreen.getStage();
        mainMenuStage.getRoot().getColor().a = 0f;

        studioLabel.addAction(Actions.sequence(
            Actions.delay(2f),
            Actions.run(() -> studioLabel.setVisible(true)),
            Actions.delay(3f),
            Actions.fadeOut(3f),
            Actions.delay(1f),
            Actions.run(() -> mainMenuStage.getRoot().addAction(Actions.fadeIn(3f))),
            Actions.run(() -> game.setScreen(mainMenuScreen))
        ));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mainMenuStage.act(delta);
        mainMenuStage.draw();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        mainMenuStage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); mainMenuStage.dispose(); }
}

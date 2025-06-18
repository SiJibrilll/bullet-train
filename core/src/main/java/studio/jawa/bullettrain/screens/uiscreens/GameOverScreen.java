package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import studio.jawa.bullettrain.screens.gamescreens.TestGameScreen;

public class GameOverScreen implements Screen {
    private final Game game;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;
    private CharacterInfo selectedCharacter;

    public GameOverScreen(Game game, CharacterInfo selectedCharacter, AssetManager assetManager) {
        this.game = game;
        this.selectedCharacter = selectedCharacter;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = assetManager.get("ui/uiskin.json", Skin.class);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default");

        Drawable transparent = skin.newDrawable("white", 1, 1, 1, 0f);
        style.up = transparent;
        style.down = transparent;
        style.over = transparent;

        Table container = new Table();
        container.setFillParent(true);
        container.center();

        Label title = new Label("Game over", skin);
        title.setFontScale(4);

        // TextButton retryButton = new TextButton("Retry", style);
        // retryButton.getLabel().setFontScale(2);
        TextButton selectCharacterButton = new TextButton("Select character", style);
        selectCharacterButton.getLabel().setFontScale(2);
        TextButton mainMenuButton = new TextButton("Main menu", style);
        mainMenuButton.getLabel().setFontScale(2);

        // retryButton.addListener(new ClickListener() {
        //     @Override
        //     public void clicked(InputEvent event, float x, float y) {
        //         game.setScreen(new TestGameScreen(game, selectedCharacter, assetManager));
        //     }
        // });

        selectCharacterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CharacterSelectScreen(game, assetManager));
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game, assetManager));
            }
        });

        container.add(title).padBottom(60).row();
        // container.add(retryButton).width(300).height(60).padBottom(30).row();
        container.add(selectCharacterButton).width(300).height(60).padBottom(30).row();
        container.add(mainMenuButton).width(300).height(60);

        stage.addActor(container);
    };

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}

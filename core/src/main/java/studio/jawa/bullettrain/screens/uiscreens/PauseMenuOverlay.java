package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseMenuOverlay {
    private final Game game;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;
    private boolean visible = false;

    public PauseMenuOverlay(Game game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
        this.stage = new Stage(new ScreenViewport());
        this.skin = assetManager.get("ui/uiskin.json", Skin.class);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default");

        Drawable transparent = skin.newDrawable("white", 1, 1, 1, 0f);
        style.up = transparent;
        style.down = transparent;
        style.over = transparent;

        Table container = new Table();
        container.setFillParent(true);
        container.center();

        Label label = new Label("Game Paused", skin);
        label.setFontScale(5);

        TextButton resumeButton = new TextButton("Resume", style);
        resumeButton.getLabel().setFontScale(2);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        TextButton mainMenuButton = new TextButton("Main menu", style);
        mainMenuButton.getLabel().setFontScale(2);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game, assetManager));
            }
        });

        container.add(label).padBottom(60).row();
        container.add(resumeButton).width(250).height(50).padBottom(40).row();
        container.add(mainMenuButton).width(250).height(50);

        stage.addActor(container);
    }

    public void render(float delta) {
        if (visible) {
            stage.act(delta);
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void show() {
        visible = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void dispose() {
        stage.dispose();
    }
}

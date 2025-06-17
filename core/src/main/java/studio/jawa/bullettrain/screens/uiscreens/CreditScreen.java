package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CreditScreen implements Screen {
    private final Game game;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;
    private Table creditTable;
    private float scrollSpeed = 40f;

    public CreditScreen(Game game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = assetManager.get("ui/uiskin.json", Skin.class);

        creditTable = new Table();
        creditTable.top();
        creditTable.setFillParent(true);
        creditTable.padTop(Gdx.graphics.getHeight());

        addCreditLine("CREDIT", 2.5f, Color.GOLD);
        addSpacer();
        addCreditLine("Game Programmer", 2f, Color.WHITE);
        addCreditLine("Azril", 1.8f, Color.WHITE);
        addCreditLine("Jibril", 1.8f, Color.WHITE);
        addSpacer();
        addCreditLine("Game Asset Manager", 2f, Color.WHITE);
        addCreditLine("Zhafir", 1.8f, Color.WHITE);
        addSpacer();
        addCreditLine("UI Designer", 2f, Color.WHITE);
        addCreditLine("Fahmi", 1.8f, Color.WHITE);
        addSpacer();
        addCreditLine("Thank you for playing!", 2f, Color.CYAN);
        addSpacer();
        addCreditLine("© 2025 Studio Jawa", 1.2f, Color.GRAY);

        Table container = new Table();
        container.setFillParent(true);
        container.add(creditTable).expand().center();

        stage.addActor(container);

        TextButton skipButton = new TextButton("Skip", skin);
        skipButton.setPosition(20, 20);
        skipButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game, assetManager));
            }
        });
        stage.addActor(skipButton);

    }

    private void addSpacer() {
        creditTable.add().height(30).row();
    }

        private void addCreditLine(String text, float scale, Color color) {
        Label.LabelStyle style = new Label.LabelStyle(skin.getFont("default-font"), color);
        Label label = new Label(text, style);
        label.setFontScale(scale);
        creditTable.add(label).padBottom(20).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        creditTable.moveBy(0, delta * scrollSpeed);

        stage.act(delta);
        stage.draw();

        if (creditTable.getY() > Gdx.graphics.getHeight() * 2) {
            game.setScreen(new MainMenuScreen(game, assetManager));
        }
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }

}

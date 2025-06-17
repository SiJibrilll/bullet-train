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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CreditScreen implements Screen {
    private final Game game;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;

    private Table creditTable;
    private Table container;

    private float scrollSpeed = 40f;

    public CreditScreen(Game game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Credit content
        creditTable = new Table();
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

        // Container untuk bisa digeser seluruhnya
        container = new Table();
        container.add(creditTable).center();
        container.pack(); // Hitung ukuran sesuai isi

        stage.addActor(container);

        // Skip button
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default");

        Drawable transparent = skin.newDrawable("white", 1, 1, 1, 0f);
        style.up = transparent;
        style.down = transparent;
        style.over = transparent;

        TextButton skipButton = new TextButton("Skip", style);
        skipButton.getLabel().setFontScale(1.5f);
        skipButton.setPosition(20, 20);
        skipButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game, assetManager));
            }
        });
        stage.addActor(skipButton);

        // Layout dulu untuk mendapatkan ukuran container
        stage.act(); // agar pack() bekerja

        // Tempatkan container di bawah layar
        container.setPosition(
                (stage.getWidth() - container.getWidth()) / 2f,
                -container.getHeight()
        );
    }

    private void addSpacer() {
        creditTable.add().height(30).colspan(1).row();
    }

    private void addCreditLine(String text, float scale, Color color) {
        Label.LabelStyle style = new Label.LabelStyle(skin.getFont("default"), color);
        Label label = new Label(text, style);
        label.setFontScale(scale);
        creditTable.add(label).expandX().center().padBottom(20).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        container.moveBy(0, delta * scrollSpeed);

        stage.act(delta);
        stage.draw();

        if (container.getY() > stage.getHeight()) {
            game.setScreen(new MainMenuScreen(game, assetManager));
        }
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }
}

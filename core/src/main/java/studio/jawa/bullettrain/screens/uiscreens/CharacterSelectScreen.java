package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import studio.jawa.bullettrain.screens.gamescreens.TestGameScreen;

public class CharacterSelectScreen implements Screen {
    private final Game game;
    private final AssetManager assetManager;
    private Stage uiStage;
    private Skin skin;

    private Image portrait;
    private Label nameLabel;
    private Label info1Label;
    private Label info2Label;

    private CharacterInfo[] characters;
    private CharacterInfo selectedCharacter;

    public CharacterSelectScreen(Game game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);
        skin = assetManager.get("ui/uiskin.json", Skin.class);

        characters = new CharacterInfo[] {
//            new CharacterInfo("Grace", assetManager.get("grace.png", Texture.class), "Daughter of a Raider warlord", "Glass canon"),
//            new CharacterInfo("Jing Wei", assetManager.get("jing_wei.png", Texture.class), "Guardian Automata", "Quick dashes")
            new CharacterInfo("Grace", "Daughter of a Raider warlord", "Glass canon"),
            new CharacterInfo("Jing Wei", "Guardian Automata", "Quick dashes")
        };
        selectedCharacter = characters[0];

        Image bgBar = new Image(skin.newDrawable("white", 0.2f, 0.2f, 0.2f, 1f));
        bgBar.setSize(Gdx.graphics.getWidth(), 220);
        bgBar.setPosition(0, 0);
        uiStage.addActor(bgBar);

        Table root = new Table();
        root.setFillParent(true);
        root.pad(20);
        uiStage.addActor(root);

        Table confirmTable = new Table();
        confirmTable.setFillParent(true);
        confirmTable.align(Align.bottom | Align.center);

        Table bottomContainer = new Table();
        bottomContainer.setFillParent(true);
        bottomContainer.bottom().padBottom(30).left().padLeft(30);
        uiStage.addActor(bottomContainer);

        TextButton backButton = new TextButton("Back", skin);
        backButton.getLabel().setFontScale(1.5f);
        root.add(backButton).left().top().width(150).height(60).padLeft(60).row();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game, assetManager));
            }
        });

        Label infoLabel = new Label("Select your character", skin);
        infoLabel.setAlignment(Align.center);
        infoLabel.setFontScale(5);
        root.top().padTop(40);
        root.add(infoLabel).expandX().center().top().padBottom(20).row();

        portrait = new Image(skin.newDrawable("white", 1, 1, 1, 1));
        portrait.setSize(400, 400);
        portrait.setPosition(-portrait.getWidth(), 100);
        uiStage.addActor(portrait);

//        portrait = new Image(new TextureRegionDrawable(characters[0].portrait));
//        bottomContainer.add(portrait).width(500).height(500).left().padLeft(30).bottom().padBottom(30);

        Table rightContainer = new Table();

        nameLabel = new Label(characters[0].name, skin);
        nameLabel.setFontScale(4);
        nameLabel.setAlignment(Align.left);

        info1Label = new Label("• " + characters[0].info1, skin);
        info1Label.setFontScale(3);
        info1Label.setWrap(false);

        info2Label = new Label("• " + characters[0].info2, skin);
        info2Label.setFontScale(3);
        info2Label.setWrap(false);

        rightContainer.add(nameLabel).width(500).padBottom(70).left().row();
        rightContainer.add(info1Label).width(500).padBottom(30).left().row();
        rightContainer.add(info2Label).width(500).padBottom(150).left().row();

        Table selectionTable = new Table();
        for (CharacterInfo character : characters) {
            TextButton charButton = new TextButton(character.name, skin);
            charButton.getLabel().setFontScale(1.5f);
            charButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    updateCharacterInfo(character);
                    selectedCharacter = character;
                }
            });
            selectionTable.add(charButton).width(180).height(60).pad(10);
        }

        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.getLabel().setFontScale(1.5f);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedCharacter != null) {
                    game.setScreen(new TestGameScreen(game, selectedCharacter, assetManager));
                }
            }
        });

        confirmTable.add(confirmButton).width(250).height(60).padBottom(60);
        uiStage.addActor(confirmTable);

        rightContainer.add(selectionTable).padBottom(20).row();
        bottomContainer.add(rightContainer).expandX().bottom().right().padRight(150);

        portrait.addAction(Actions.sequence(
            Actions.delay(0.2f),
            Actions.moveTo(30, 100, 0.7f, Interpolation.pow3Out)
        ));

        confirmTable.moveBy(0, -200);
        confirmTable.addAction(Actions.sequence(
            Actions.delay(0.5f),
            Actions.moveBy(0, 200, 0.7f, Interpolation.pow3Out)
        ));

        bottomContainer.moveBy(0, -300);
        bottomContainer.addAction(Actions.sequence(
            Actions.delay(0.3f),
            Actions.moveBy(0, 300, 0.7f, Interpolation.pow3Out)
        ));
    }

    public void updateCharacterInfo(CharacterInfo character) {
//        portrait.setDrawable(new TextureRegionDrawable(new TextureRegion(character.portrait)));
        nameLabel.setText(character.name);
        info1Label.setText("• " + character.info1);
        info2Label.setText("• " + character.info2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        uiStage.act(delta);
        uiStage.draw();
    }

    @Override public void resize(int width, int height) { uiStage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { uiStage.dispose(); }
}

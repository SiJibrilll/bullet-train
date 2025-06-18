package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import studio.jawa.bullettrain.helpers.AudioHelper;
import studio.jawa.bullettrain.screens.gamescreens.GamePlayTestScreen;
import studio.jawa.bullettrain.screens.gamescreens.TestGameScreen;

public class CharacterSelectScreen implements Screen {
    private final Game game;
    private final AssetManager assetManager;

    private Stage uiStage;
    private Stage worldStage;
    private OrthographicCamera worldCamera;
    private Image bgBar;

    private Skin skin;

    private Image portrait;
    private Label nameLabel, info1Label, info2Label;
    private CharacterInfo[] characters;
    private CharacterInfo selectedCharacter;

    private Actor graceActor, jingWeiActor;

    private Vector2 targetCameraPos = new Vector2();

    public CharacterSelectScreen(Game game, AssetManager assetManager) {
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        AudioHelper.stopMusic("game");
        if (!AudioHelper.isMusicPlaying("main")) {
            AudioHelper.playMusic("main");
        }
        worldCamera = new OrthographicCamera();
        worldCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldStage = new Stage(new ScreenViewport(worldCamera));

        characters = new CharacterInfo[] {
            new CharacterInfo("Grace", assetManager.get("testing/char/grace.png", Texture.class), "Daughter of a Raider warlord", "Glass canon"),
            new CharacterInfo("Jing Wei", createPlaceholderTexture() , "Guardian Automata", "Quick dashes")
//            new CharacterInfo("Grace", "Daughter of a Raider warlord", "Glass canon"),
//            new CharacterInfo("Jing Wei", "Guardian Automata", "Quick dashes")
        };
        selectedCharacter = characters[0];

        skin = assetManager.get("ui/uiskin.json", Skin.class);

        Texture desertTexture = assetManager.get("testing/background/desert.png", Texture.class);
        TextureRegion desertRegion = new TextureRegion(desertTexture);
        Image desertBackground = new Image(new TextureRegionDrawable(desertRegion));
        float newDesertWidth = 2500f;
        float newDesertheight = (newDesertWidth * 3100) / 4800;
        desertBackground.setSize(newDesertWidth, newDesertheight);
        desertBackground.setPosition(0, 0);
        worldStage.addActor(desertBackground);

        graceActor = new Image(assetManager.get("testing/char/grace_main.png", Texture.class));
        float size = 32 * 5;
        graceActor.setSize(size, size);
        graceActor.setPosition(900, 800);
        worldStage.addActor(graceActor);

        jingWeiActor = new Image(skin.newDrawable("white", 1f, 1f, 0.6f, 1f));
        jingWeiActor.setSize(100, 150);
        jingWeiActor.setPosition(1500, 800);
        worldStage.addActor(jingWeiActor);

        characters[0].actor = graceActor;
        characters[1].actor = jingWeiActor;

        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);

        setupUI();

        targetCameraPos.set(graceActor.getX() + graceActor.getWidth() / 2f, graceActor.getY() + graceActor.getHeight() / 2f);
        worldCamera.position.set(targetCameraPos, 0);

    }

    private void setupUI() {
        skin = assetManager.get("ui/uiskin.json", Skin.class);

        bgBar = new Image(skin.newDrawable("white", 0.2f, 0.2f, 0.2f, 1f));
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

//        portrait = new Image(skin.newDrawable("white", 1, 1, 1, 1));
//        portrait.setSize(400, 400);
//        portrait.setPosition(-portrait.getWidth(), 100);
//        uiStage.addActor(portrait);

        portrait = new Image(new TextureRegionDrawable(characters[0].portrait));
        portrait.setScaling(Scaling.fit);
        portrait.setAlign(Align.left);
        bottomContainer.add(portrait).width(500).left().padLeft(30).padTop(50).bottom();
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
                    if (character.actor != null) {
                        updateCameraTarget(character.actor);
                    }
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
                    AudioHelper.stopMusic("main");
                    game.setScreen(new GamePlayTestScreen(game, selectedCharacter, assetManager));
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

    private void updateCameraTarget(Actor actor) {
        targetCameraPos.set(
            actor.getX() + actor.getWidth() / 2f,
            actor.getY() + actor.getHeight() / 2f
        );
    }

    public void updateCharacterInfo(CharacterInfo character) {
        portrait.setDrawable(new TextureRegionDrawable(new TextureRegion(character.portrait)));

        nameLabel.setText(character.name);
        info1Label.setText("• " + character.info1);
        info2Label.setText("• " + character.info2);
    }

    private Texture createPlaceholderTexture() {
        Pixmap pixmap = new Pixmap(500, 500, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 0, 1);
        pixmap.fill();

        pixmap.setColor(0, 0, 0, 1);
        pixmap.drawLine(0, 0, 500, 500);
        pixmap.drawLine(0, 500, 500, 0);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }


    @Override
    public void render(float delta) {
        float lerp = 3f * delta;
        worldCamera.position.lerp(new Vector3(targetCameraPos.x, targetCameraPos.y, 0), lerp);
        worldCamera.update();

        Gdx.gl.glClearColor(0, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldStage.act(delta);
        worldStage.draw();

        uiStage.act(delta);
        uiStage.draw();
    }

    @Override public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
        worldStage.getViewport().update(width, height, true);

        if (bgBar != null) {
            bgBar.setSize(width, 220);
            bgBar.setPosition(0, 0);
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { uiStage.dispose(); worldStage.dispose(); }
}

package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final Game game;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;
    private Sprite sky, farMountain, midMountain, farTrees, myst, nearTrees, train;
    private float farMountainX, midMountainX, farTreesX, mystX, nearTreesX, trainX;
    private float fadeAlpha = 0f, fadeDuration = 2f, fadeTimer = 0f;
    private float skyY = -1000f;

    private float parallaxSpeed = 1f;
    private boolean isParallaxStopping = false;
    private boolean isTrainMoving = false;
    private static final float STOP_DURATION = 1.5f;
    private float stopTimer = 0f;
    private static final float TRAIN_SPEED = 1000f, TRAIN_START_THRESHOLD = 0.5f;

    public MainMenuScreen(Game game, AssetManager assetManager) {
        this.stage = new Stage(new ScreenViewport());
        this.game = game;
        this.assetManager = assetManager;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        skin = assetManager.get("ui/uiskin.json", Skin.class);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = skin.getFont("default");

        Drawable transparent = skin.newDrawable("white", 1, 1, 1, 0f);
        style.up = transparent;
        style.down = transparent;
        style.over = transparent;

        sky = new Sprite(assetManager.get("testing/sky.png", Texture.class));
        farMountain = new Sprite(assetManager.get("testing/far-mountains.png", Texture.class));
        midMountain = new Sprite(assetManager.get("testing/middle-mountains.png", Texture.class));
        farTrees = new Sprite(assetManager.get("testing/far-trees.png", Texture.class));
        myst = new Sprite(assetManager.get("testing/myst.png", Texture.class));
        nearTrees = new Sprite(assetManager.get("testing/near-trees.png", Texture.class));
        train = new Sprite(assetManager.get("testing/train_v18.png", Texture.class));

        float newWidth = 1000f;
        float newSkyWidth = 1920f;
        float newTrainWidth = 500f;
        float originalWidth = 320f;
        float originalHeight = 240f;
        float newHeight = (newWidth * originalHeight) / originalWidth;
        float newSkyHeight = (newSkyWidth * originalHeight) / originalWidth;
        float newTrainHeight = (newTrainWidth * 64) / 256;
        sky.setSize(newSkyWidth, newSkyHeight);
        farMountain.setSize(newWidth,newHeight);
        midMountain.setSize(newWidth, newHeight);
        farTrees.setSize(newWidth, newHeight);
        myst.setSize(newWidth, newHeight);
        nearTrees.setSize(newWidth, newHeight);
        train.setSize(newTrainWidth, newTrainHeight);
        train.flip(true, false);

        Label title = new Label("Bullet Train", skin);
        title.setFontScale(5);
        title.setAlignment(Align.center);

        TextButton playButton = new TextButton("Play", style);
        playButton.getLabel().setFontScale(3);
        TextButton quitButton = new TextButton("Quit", style);
        quitButton.getLabel().setFontScale(3);

        Table menuContainer = new Table();
        menuContainer.setFillParent(true);
        menuContainer.center();

        menuContainer.add(title).padBottom(60).row();
        menuContainer.add(playButton).width(250).height(50).padBottom(40).row();
        menuContainer.add(quitButton).width(250).height(50).row();

        stage.addActor(menuContainer);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(new CharacterSelectScreen(game, assetManager));
                isParallaxStopping = true;
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        trainX = Gdx.graphics.getWidth() - train.getWidth();
        train.setPosition(trainX, 0);
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (fadeTimer < fadeDuration) {
            fadeTimer += delta;
            fadeAlpha = Math.min(fadeTimer / fadeDuration, 1f);
            skyY = -1000f + (1000f * fadeTimer / fadeDuration);
        }

        sky.setColor(1, 1, 1, fadeAlpha);
        farMountain.setColor(1, 1, 1, fadeAlpha);
        midMountain.setColor(1, 1, 1, fadeAlpha);
        farTrees.setColor(1, 1, 1, fadeAlpha);
        myst.setColor(1, 1, 1, fadeAlpha);
        nearTrees.setColor(1, 1, 1, fadeAlpha);
        train.setColor(1, 1, 1, fadeAlpha);

        if (isParallaxStopping) {
            stopTimer += delta;
            parallaxSpeed = Math.max(0, 1f - (stopTimer / STOP_DURATION));
            if (stopTimer >= TRAIN_START_THRESHOLD) {
                isTrainMoving = true;
            }
            if (stopTimer >= STOP_DURATION) {
                parallaxSpeed = 0;
                isParallaxStopping = false;
                stopTimer = 0;
            }
        }

        farMountainX += 30 * delta * parallaxSpeed;
        if (farMountainX >= -farMountain.getWidth()) farMountainX -= farMountain.getWidth();

        midMountainX += 60 * delta * parallaxSpeed;
        if (midMountainX >= -midMountain.getWidth()) midMountainX -= midMountain.getWidth();

        farTreesX += 90 * delta * parallaxSpeed;
        if (farTreesX >= -farTrees.getWidth()) farTreesX -= farTrees.getWidth();

        mystX += 120 * delta * parallaxSpeed;
        if (mystX >= -myst.getWidth()) mystX -= myst.getWidth();

        nearTreesX += 150 * delta * parallaxSpeed;
        if (nearTreesX >= -nearTrees.getWidth()) nearTreesX -= nearTrees.getWidth();

        if (isTrainMoving) {
            trainX -= TRAIN_SPEED * delta;
            if (trainX + train.getWidth() < 0) {
                game.setScreen(new CharacterSelectScreen(game, assetManager));
            }
        }

        stage.getBatch().begin();
        float screenWidth = Gdx.graphics.getWidth();

        float skyX = (screenWidth - sky.getWidth()) / 2;
        sky.setPosition(skyX, skyY);
        sky.draw(stage.getBatch());
        if (sky.getWidth() < screenWidth) {
            float nextX = skyX + sky.getWidth();
            while (nextX < screenWidth) {
                sky.setPosition(nextX, skyY);
                sky.draw(stage.getBatch());
                nextX += sky.getWidth();
            }
        }

        float currentX = farMountainX;
        while (currentX < screenWidth) {
            farMountain.setPosition(currentX, 0);
            farMountain.draw(stage.getBatch());
            currentX += farMountain.getWidth();
        }

        currentX = midMountainX;
        while (currentX < screenWidth) {
            midMountain.setPosition(currentX, 0);
            midMountain.draw(stage.getBatch());
            currentX += midMountain.getWidth();
        }

        currentX = farTreesX;
        while (currentX < screenWidth) {
            farTrees.setPosition(currentX, 0);
            farTrees.draw(stage.getBatch());
            currentX += farTrees.getWidth();
        }

        currentX = mystX;
        while (currentX < screenWidth) {
            myst.setPosition(currentX, 0);
            myst.draw(stage.getBatch());
            currentX += myst.getWidth();
        }

        currentX = nearTreesX;
        while (currentX < screenWidth) {
            nearTrees.setPosition(currentX, 0);
            nearTrees.draw(stage.getBatch());
            currentX += nearTrees.getWidth();
        }

        train.setPosition(trainX, 0);
        train.draw(stage.getBatch());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }

    public Stage getStage() {
        return stage;
    }
}

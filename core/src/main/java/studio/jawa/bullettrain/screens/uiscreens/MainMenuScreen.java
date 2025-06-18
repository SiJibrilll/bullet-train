package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
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
import com.badlogic.gdx.Input;

public class MainMenuScreen implements Screen {
    private final Game game;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;
//    private Sprite sky, farMountain, midMountain, farTrees, myst, nearTrees, train;
//    private float farMountainX, midMountainX, farTreesX, mystX, nearTreesX, trainX;
    private float fadeAlpha = 0f, fadeDuration = 2f, fadeTimer = 0f;
    private float skyY = -1000f;

    private Sprite sky, clouds, mountains, desert1, desert2, desert3, desert4, desert5, train;
    private float cloudsX, mountainsX, desert1X, desert2X, desert3X, desert4X, desert5X, trainX;

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

//        sky = new Sprite(assetManager.get("testing/sky.png", Texture.class));
//        farMountain = new Sprite(assetManager.get("testing/far-mountains.png", Texture.class));
//        midMountain = new Sprite(assetManager.get("testing/middle-mountains.png", Texture.class));
//        farTrees = new Sprite(assetManager.get("testing/far-trees.png", Texture.class));
//        myst = new Sprite(assetManager.get("testing/myst.png", Texture.class));
//        nearTrees = new Sprite(assetManager.get("testing/near-trees.png", Texture.class));
//        train = new Sprite(assetManager.get("testing/train_v18.png", Texture.class));



//        float newWidth = 1000f;
//        float newSkyWidth = 1920f;
//        float newTrainWidth = 500f;
//        float originalWidth = 320f;
//        float originalHeight = 240f;
//        float newHeight = (newWidth * originalHeight) / originalWidth;
//        float newSkyHeight = (newSkyWidth * originalHeight) / originalWidth;
//        float newTrainHeight = (newTrainWidth * 64) / 256;
//        sky.setSize(newSkyWidth, newSkyHeight);
//        farMountain.setSize(newWidth,newHeight);
//        midMountain.setSize(newWidth, newHeight);
//        farTrees.setSize(newWidth, newHeight);
//        myst.setSize(newWidth, newHeight);
//        nearTrees.setSize(newWidth, newHeight);
//        train.setSize(newTrainWidth, newTrainHeight);
//        train.flip(true, false);

        sky = new Sprite(assetManager.get("testing/menu/Sky_Main.png", Texture.class));
        clouds = new Sprite(assetManager.get("testing/menu/Clouds_Main.png", Texture.class));
        mountains = new Sprite(assetManager.get("testing/menu/Mountains_Main.png", Texture.class));
        desert1 = new Sprite(assetManager.get("testing/menu/Desert_01_Main.png", Texture.class));
        desert2 = new Sprite(assetManager.get("testing/menu/Desert_02_Main.png", Texture.class));
        desert3 = new Sprite(assetManager.get("testing/menu/Desert_03_Main.png", Texture.class));
        desert4 = new Sprite(assetManager.get("testing/menu/Desert_04_Main.png", Texture.class));
        desert5 = new Sprite(assetManager.get("testing/menu/Desert_05_Main.png", Texture.class));
        train = new Sprite(assetManager.get("testing/menu/Train_Full_Main.png", Texture.class));

        float newWidth = 1000f;
        float newSkyWidth = 2000f;
        float newTrainWidth = 1875f;
        float originalWidth = 4800f;
        float originalHeight = 4800f;
        float newHeight = (newWidth * originalHeight) / originalWidth;
        float newSkyHeight = (newSkyWidth * originalHeight) / originalWidth;
        float newTrainHeight = (newTrainWidth * 64) / 960;

        sky.setSize(newWidth, newSkyHeight);
        clouds.setSize(newWidth, newHeight);
        mountains.setSize(newWidth, newHeight);
        desert1.setSize(newWidth, newHeight);
        desert2.setSize(newWidth, newHeight);
        desert3.setSize(newWidth, newHeight);
        desert4.setSize(newWidth, newHeight);
        desert5.setSize(newWidth, newHeight);
        train.setSize(newTrainWidth, newTrainHeight);

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

        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (fadeTimer < fadeDuration) {
            fadeTimer += delta;
            fadeAlpha = Math.min(fadeTimer / fadeDuration, 1f);
//            skyY = -1000f + (1000f * fadeTimer / fadeDuration);
        }

        sky.setColor(1, 1, 1, fadeAlpha);
        clouds.setColor(1, 1, 1, fadeAlpha);
        mountains.setColor(1, 1, 1, fadeAlpha);
        desert1.setColor(1, 1, 1, fadeAlpha);
        desert2.setColor(1, 1, 1, fadeAlpha);
        desert3.setColor(1, 1, 1, fadeAlpha);
        desert4.setColor(1, 1, 1, fadeAlpha);
        desert5.setColor(1, 1, 1, fadeAlpha);
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

        cloudsX += 30 * delta * parallaxSpeed;
        if (cloudsX >= -clouds.getWidth()) cloudsX -= clouds.getWidth();

        mountainsX += 60 * delta * parallaxSpeed;
        if (mountainsX >= -mountains.getWidth()) mountainsX -= mountains.getWidth();

        desert1X += 90 * delta * parallaxSpeed;
        if (desert1X >= -desert1.getWidth()) desert1X -= desert1.getWidth();

        desert2X += 120 * delta * parallaxSpeed;
        if (desert2X >= -desert2.getWidth()) desert2X -= desert2.getWidth();

        desert3X += 150 * delta * parallaxSpeed;
        if (desert3X >= -desert3.getWidth()) desert3X -= desert3.getWidth();

        desert4X += 180 * delta * parallaxSpeed;
        if (desert4X >= -desert4.getWidth()) desert4X -= desert4.getWidth();

        desert5X += 210 * delta * parallaxSpeed;
        if (desert5X >= -desert5.getWidth()) desert5X -= desert5.getWidth();

        if (isTrainMoving) {
            trainX -= TRAIN_SPEED * delta;
            if (trainX + train.getWidth() + 800 < 0) {
                game.setScreen(new CharacterSelectScreen(game, assetManager));
            }
        }

        stage.getBatch().begin();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

//        float skyX = (screenWidth - sky.getWidth()) / 2;
//        sky.setPosition(skyX, skyY);
//        sky.draw(stage.getBatch());
//        if (sky.getWidth() < screenWidth) {
//            float nextX = skyX + sky.getWidth();
//            while (nextX < screenWidth) {
//                sky.setPosition(nextX, skyY);
//                sky.draw(stage.getBatch());
//                nextX += sky.getWidth();
//            }
//        }

        sky.setSize(screenWidth, screenHeight);
        sky.setPosition(0, 0);
        sky.draw(stage.getBatch());

        float currentX = cloudsX;
        while (currentX < screenWidth) {
            clouds.setPosition(currentX, 0);
            clouds.draw(stage.getBatch());
            currentX += clouds.getWidth();
        }

        currentX = mountainsX;
        while (currentX < screenWidth) {
            mountains.setPosition(currentX, 0);
            mountains.draw(stage.getBatch());
            currentX += mountains.getWidth();
        }

        currentX = desert1X;
        while (currentX < screenWidth) {
            desert1.setPosition(currentX, 0);
            desert1.draw(stage.getBatch());
            currentX += desert1.getWidth();
        }

        currentX = desert2X;
        while (currentX < screenWidth) {
            desert2.setPosition(currentX, 0);
            desert2.draw(stage.getBatch());
            currentX += desert2.getWidth();
        }

        currentX = desert3X;
        while (currentX < screenWidth) {
            desert3.setPosition(currentX, 0);
            desert3.draw(stage.getBatch());
            currentX += desert3.getWidth();
        }

        currentX = desert4X;
        while (currentX < screenWidth) {
            desert4.setPosition(currentX, 0);
            desert4.draw(stage.getBatch());
            currentX += desert4.getWidth();
        }

        currentX = desert5X;
        while (currentX < screenWidth) {
            desert5.setPosition(currentX, 0);
            desert5.draw(stage.getBatch());
            currentX += desert5.getWidth();
        }

        train.setPosition(trainX + 800, 55);
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

package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HudStage extends Stage {
    private AssetManager assetManager;
    public ProgressBar healthBar, xpBar;
    private Label healthLabel, xpLabel,  ammoLabel, speedLabel;

    public HudStage(Viewport viewport) {
        super(viewport);
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);

        Table bottomContainer = new Table();
        bottomContainer.setFillParent(true);
        bottomContainer.bottom().padBottom(30).left().padLeft(30);
        root.add(bottomContainer).expand().fill().bottom().row();

        Table leftContainer = new Table();

        Drawable background = skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 1f));
        background.setMinHeight(20);

        Drawable knobBefore = skin.newDrawable("white", Color.CYAN);
        knobBefore.setMinHeight(20);

        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = background;
        style.knobBefore = knobBefore;
        style.knob = knobBefore;

        healthLabel = new Label("HP", skin);
        healthLabel.setFontScale(2);

        healthBar = new ProgressBar(0, 100, 1, false, style);
        healthBar.setValue(100);

        leftContainer.add(healthLabel).padRight(10);
        leftContainer.add(healthBar).width(200).height(20).padRight(30);

        bottomContainer.add(leftContainer).bottom().left();

        root.add(bottomContainer).expand().fillX().bottom().row();
    }

    public void setHealth(float value) {
        healthBar.setValue(Math.max(0, value));
    }
}

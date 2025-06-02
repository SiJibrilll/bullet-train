package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HudStage extends Stage {
    private AssetManager assetManager;
    private ProgressBar healthBar, xpBar;
    private Label healthLabel, xpLabel,  ammoLabel, speedLabel;
    private Image hudBackgorund;
//    private Image bodyEye, bodyHand, BodyLeg;

    public HudStage(Viewport viewport) {
        super(viewport);
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        hudBackgorund = new Image(skin.newDrawable("white", 0.2f, 0.2f, 0.2f, 1f));
        hudBackgorund.setSize(Gdx.graphics.getWidth(), 100);
        hudBackgorund.setPosition(0, 0);
        this.addActor(hudBackgorund);

        Table root = new Table();
        root.setFillParent(true);
        this.addActor(root);

        Table bottomContainer = new Table();
        bottomContainer.setFillParent(true);
        bottomContainer.bottom().padBottom(30).left().padLeft(30);
        root.add(bottomContainer).expand().fill().bottom().row();

        Table leftContainer = new Table();

        healthLabel = new Label("HP", skin);
        healthLabel.setFontScale(2);
        healthBar = new ProgressBar(0, 100, 1, false, skin);
        xpLabel = new Label("XP", skin);
        xpLabel.setFontScale(2);
        xpBar = new ProgressBar(0, 100, 1, false, skin);

        leftContainer.add(healthLabel).padRight(10);
        leftContainer.add(healthBar).width(200).height(20).padRight(30);
        leftContainer.add(xpLabel).padRight(10);
        leftContainer.add(xpBar).width(200).height(20);

        bottomContainer.add(leftContainer).bottom().left().padBottom(100);

        root.add(bottomContainer).expand().fillX().bottom().row();
    }
}

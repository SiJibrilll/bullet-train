package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.assets.AssetManager;

public class CursorManager implements Disposable {
    private static final int MAX_AMMO = 10;
    private int currentAmmo = MAX_AMMO;
    private boolean reloading = false;
    private float reloadTime = 2f;
    private float reloadTimer = 0f;

    private Texture crosshairTexture;
    private TextureRegion[] reloadFrames;
    private float frameDuration;
    private float animationTime;

    private Cursor crosshairCursor;

    public CursorManager(AssetManager assetManager) {
        // Load textures
        crosshairTexture = assetManager.get("cursor/crosshair.png", Texture.class);

        // Custom cursor from image
        Pixmap pixmap = new Pixmap(Gdx.files.internal("cursor/crosshair.png"));
        crosshairCursor = Gdx.graphics.newCursor(pixmap, pixmap.getWidth() / 2, pixmap.getHeight() / 2);
        Gdx.graphics.setCursor(crosshairCursor);
        pixmap.dispose();

        // Load reload animation frames (assumed 8 frames)
        reloadFrames = new TextureRegion[12];
        for (int i = 0; i < 12; i++) {
            Texture texture = assetManager.get("cursor/reload" + i + ".png", Texture.class);
            reloadFrames[i] = new TextureRegion(texture);
        }
        frameDuration = reloadTime / reloadFrames.length;
    }

    public void update(float delta) {
        if (reloading) {
            reloadTimer += delta;
            animationTime += delta;

            if (reloadTimer >= reloadTime) {
                reloading = false;
                reloadTimer = 0;
                animationTime = 0;
                currentAmmo = MAX_AMMO;
                Gdx.graphics.setCursor(crosshairCursor);
            }
        }
    }

    public void renderReloadCursor(SpriteBatch batch) {
        if (!reloading) return;

        int frameIndex = (int)(animationTime / frameDuration) % reloadFrames.length;
        TextureRegion currentFrame = reloadFrames[frameIndex];

        float x = Gdx.input.getX() - currentFrame.getRegionWidth() / 2f;
        float y = Gdx.graphics.getHeight() - Gdx.input.getY() - currentFrame.getRegionHeight() / 2f;

        batch.begin();
        batch.draw(currentFrame, x, y);
        batch.end();
    }

    public boolean canShoot() {
        return !reloading && currentAmmo > 0;
    }

    public void shoot() {
        if (canShoot()) {
            currentAmmo--;
            if (currentAmmo == 0) {
                startReload();
            }
        }
    }

    public void startReload() {
        if (!reloading) {
            reloading = true;
            reloadTimer = 0;
            animationTime = 0;
        }
    }

    public boolean isReloading() {
        return reloading;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    @Override
    public void dispose() {
        crosshairTexture.dispose();
        if (crosshairCursor != null) crosshairCursor.dispose();
    }
}

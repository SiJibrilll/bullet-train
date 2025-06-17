package studio.jawa.bullettrain.screens.uiscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CursorManager {
    private Cursor crosshairCursor;
    private Cursor emptyCursor;

    private boolean isReloading = false;
    private float reloadDuration = 2f; // total waktu reload
    private float reloadTimer = 0f;

    private TextureRegion[] reloadFrames;
    private int totalFrames = 12;
    private float frameDuration;
    private int currentFrameIndex = 0;

    private int ammo;
    private int maxAmmo;

    public interface ReloadListener {
        void onReloadComplete(int ammo);
    }
    private ReloadListener reloadListener;

    public CursorManager(AssetManager assetManager, int initialAmmo, int maxAmmo) {
        Pixmap crosshairPixmap = new Pixmap(Gdx.files.internal("cursor/crosshair2.png"));
        crosshairCursor = Gdx.graphics.newCursor(
            crosshairPixmap,
            crosshairPixmap.getWidth() / 2,
            crosshairPixmap.getHeight() / 2
        );
        crosshairPixmap.dispose();

        Pixmap emptyPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        emptyPixmap.setColor(0, 0, 0, 0);
        emptyPixmap.fill();
        emptyCursor = Gdx.graphics.newCursor(emptyPixmap, 0, 0);
        emptyPixmap.dispose();

        Gdx.graphics.setCursor(crosshairCursor);

        frameDuration = reloadDuration / totalFrames;

        reloadFrames = new TextureRegion[totalFrames];
        for (int i = 0; i < totalFrames; i++) {
            String path = "cursor/reload" + i + ".png";
            if (!assetManager.isLoaded(path)) {
                throw new RuntimeException("Missing cursor asset: " + path);
            }
            Texture texture = assetManager.get(path, Texture.class);
            reloadFrames[i] = new TextureRegion(texture);
        }

        this.ammo = initialAmmo;
        this.maxAmmo = maxAmmo;
    }

    public void setReloadListener(ReloadListener listener) {
        this.reloadListener = listener;
    }

    public void updateInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !isReloading) {
            if (ammo > 0) {
                ammo--;
                System.out.println("Sisa peluru: " + ammo);
            }
            if (ammo == 0) {
                startReload();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) && !isReloading) {
            startReload();
            ammo = 0;
        }

        if (!isReloading && ammo == 0) {
            ammo = maxAmmo;
            System.out.println("Reload selesai! " + ammo);
            if (reloadListener != null) {
                reloadListener.onReloadComplete(ammo);
            }
        }
    }

    public void update(float delta) {
        if (isReloading) {
            reloadTimer += delta;

            currentFrameIndex = Math.min((int)(reloadTimer / frameDuration), totalFrames - 1);

            if (reloadTimer >= reloadDuration) {
                stopReload();
            }
        }
    }

    public void render(Batch batch) {
        if (isReloading) {
            TextureRegion frame = reloadFrames[currentFrameIndex];

            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            batch.begin();
            batch.draw(frame, mouseX - frame.getRegionWidth() / 2f, mouseY - frame.getRegionHeight() / 2f);
            batch.end();
        }
    }

    public void startReload() {
        if (isReloading) return;

        isReloading = true;
        reloadTimer = 0f;
        currentFrameIndex = 0;

        Gdx.graphics.setCursor(emptyCursor);
    }

    public void stopReload() {
        isReloading = false;
        reloadTimer = 0f;
        currentFrameIndex = 0;

        Gdx.graphics.setCursor(crosshairCursor);
    }

    public boolean isReloading() {
        return isReloading;
    }

    public int getAmmo() {
        return ammo;
    }

    public void resetToCrosshair() {
        if (!isReloading) {
            Gdx.graphics.setCursor(crosshairCursor);
        }
    }

    public void dispose() {
        if (crosshairCursor != null) {
            crosshairCursor.dispose();
        }
        if (emptyCursor != null) {
            emptyCursor.dispose();
        }
    }
}

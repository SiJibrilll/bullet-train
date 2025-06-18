package studio.jawa.bullettrain.screens.gamescreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Game;

public class EndScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private Game game;

    public EndScreen(Game game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(1f, 1f, 1f, 1f);
        font.getData().setScale(1.5f);
        font.draw(batch, "Game Selesai", 320, 320);
        font.getData().setScale(1f);
        font.draw(batch, "Tekan ENTER untuk keluar", 300, 280);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Gdx.app.exit();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}



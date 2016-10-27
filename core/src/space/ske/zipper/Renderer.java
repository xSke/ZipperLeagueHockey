package space.ske.zipper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Renderer {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private FrameBuffer fbo;

    private float screenshake;

    public Renderer() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 20, 18);

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 160, 144, false);
        fbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }

    public void update(float dt) {
        screenshake = Math.max(0, screenshake - dt * 4);
    }

    public void begin(boolean screenSpace) {
        Vector3 oldPos = camera.position.cpy();
        camera.position.x += round(MathUtils.random(-screenshake, screenshake));
        camera.position.y += round(MathUtils.random(-screenshake, screenshake));
        camera.update();

        if (!screenSpace) {
            batch.setProjectionMatrix(camera.combined);
            shapeRenderer.setProjectionMatrix(camera.combined);
        } else {
            batch.getProjectionMatrix().setToOrtho2D(0, 0, 160, 144);
            shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, 160, 144);
        }
        camera.position.set(oldPos);

        fbo.begin();

        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public ShapeRenderer shapeRenderer() {
        if (batch.isDrawing()) batch.end();
        if (!shapeRenderer.isDrawing()) shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        return shapeRenderer;
    }

    public SpriteBatch batch() {
        if (shapeRenderer.isDrawing()) shapeRenderer.end();
        if (!batch.isDrawing()) batch.begin();
        return batch;
    }

    public void end() {
        if (shapeRenderer.isDrawing()) shapeRenderer.end();
        if (batch.isDrawing()) batch.end();

        fbo.end();

        batch.getProjectionMatrix().setToOrtho2D(0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        batch.begin();
        batch.draw(fbo.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    public float round(float num) {
        return MathUtils.round(num * 8) / 8f;
    }

    public void shake(float shake) {
        screenshake = Math.max(screenshake, shake);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}

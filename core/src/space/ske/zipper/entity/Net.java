package space.ske.zipper.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import space.ske.zipper.Assets;
import space.ske.zipper.Renderer;
import space.ske.zipper.Zipper;

public class Net extends Entity {
    private final Fixture goalSensor;
    private boolean top;

    public Net(boolean top) {
        z = 10;
        this.top = top;

        if (top) {
            createEdge(-2.2f, 0.5f, 2.2f, 0.5f);
        } else {
            createEdge(-2.2f, -0.5f, 2.2f, -0.5f);
        }

        if (top) {
            createEdge(-2.2f, -1f, -2.2f, 0.5f);
            createEdge(2.2f, -1f, 2.2f, 0.5f);
        } else {
            createEdge(-2.2f, -0.5f, -2.2f, 1f);
            createEdge(2.2f, -0.5f, 2.2f, 1f);
        }

        goalSensor = createRectangle(4.0f, 0.6f);
        goalSensor.setSensor(true);

        body.setTransform(30, top ? 48 : 6f, 0);
    }

    @Override
    public void render(Renderer r) {
        SpriteBatch b = r.batch();
        TextureRegion t = Assets.net;
        Vector2 p = body.getPosition();
        if (top) {
            b.draw(t, r.round(p.x - t.getRegionWidth() / 16f), r.round(p.y + t.getRegionHeight() / 16f), t.getRegionWidth() / 8f, -t.getRegionHeight() / 8f);
        } else {
            b.draw(t, r.round(p.x - t.getRegionWidth() / 16f), r.round(p.y - t.getRegionHeight() / 16f), t.getRegionWidth() / 8f, t.getRegionHeight() / 8f);
        }
    }

    @Override
    public void collide(Fixture thisFixture, Entity other) {
        if (thisFixture == goalSensor && other instanceof Puck && ((Puck) other).getAnchor() == null) {
            Zipper.i.scored(top);
        }
    }
}

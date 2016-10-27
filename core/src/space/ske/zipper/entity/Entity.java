package space.ske.zipper.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import space.ske.zipper.Renderer;
import space.ske.zipper.Zipper;

public class Entity {
    public Body body;
    private boolean shouldDestroy;
    public float z;

    public Entity() {
        body = Zipper.i.getWorld().createBody(new BodyDef());
        body.setUserData(this);
    }

    public Fixture createCircle(float radius) {
        CircleShape cs = new CircleShape();
        cs.setRadius(radius);

        Fixture fixture = body.createFixture(cs, 1);
        cs.dispose();
        return fixture;
    }
    public Fixture createRectangle(float width, float height) {
        return createRectangle(width, height, 0, 0);
    }

    public Fixture createRectangle(float width, float height, float offsetX, float offsetY) {
        PolygonShape ps = new PolygonShape();
        ps.setAsBox(width / 2f, height / 2f, new Vector2(offsetX, offsetY), 0);

        Fixture fixture = body.createFixture(ps, 1);
        ps.dispose();
        return fixture;
    }

    public Fixture createEdge(float x1, float y1, float x2, float y2) {
        EdgeShape es = new EdgeShape();
        es.set(x1, y1, x2, y2);

        Fixture fixture = body.createFixture(es, 1);
        es.dispose();
        return fixture;
    }

    public void update(float dt) {

    }

    public void render(Renderer renderer) {

    }

    public void collide(Fixture thisFixture, Entity other) {

    }

    public void destroy() {
        shouldDestroy = true;
    }

    public boolean shouldDestroy() {
        return shouldDestroy;
    }
}

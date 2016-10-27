package space.ske.zipper.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import space.ske.zipper.Assets;
import space.ske.zipper.Renderer;
import space.ske.zipper.Zipper;

public class Puck extends Entity {
    private final Fixture fixture;
    private Player anchor;

    public Puck() {
        fixture = createCircle(0.5f);
        fixture.setRestitution(0.6f);
        Filter filter = new Filter();
        filter.categoryBits = 0x2;
        fixture.setFilterData(filter);
        new FixtureDef().

        body.setType(BodyDef.BodyType.DynamicBody);
        body.setTransform(30, 27, 0);
        body.setLinearDamping(0.3f);
    }

    @Override
    public void update(float dt) {
        if (!Zipper.i.isGameGoing() || Zipper.i.isGameDone()) body.setLinearVelocity(0, 0);

        if (anchor != null) {
            fixture.setSensor(true);

            float angle;
            if (anchor.body.getAngle() > 0.05f) {
                angle = 160;
            } else {
                angle = -40;
            }
            Vector2 target = new Vector2(1.3f, 0).rotate(angle).add(anchor.body.getPosition());
            body.setTransform(target, 0);
        } else {
            fixture.setSensor(false);
        }
    }

    @Override
    public void render(Renderer r) {
        SpriteBatch batch = r.batch();
        Vector2 p = body.getPosition();
        TextureRegion t = Assets.puck;
        batch.draw(t, r.round(p.x - t.getRegionWidth() / 16f), r.round(p.y - t.getRegionHeight() / 16f), t.getRegionWidth() / 8f, t.getRegionHeight() / 8f);
    }

    public void grabbedBy(Player player) {
        anchor = player;
    }

    @Override
    public void collide(Fixture thisFixture, Entity other) {
        super.collide(thisFixture, other);
        Zipper.i.getRenderer().shake(0.3f);
    }

    public void shot() {
        if (anchor == null) return;

        body.setLinearVelocity(new Vector2(50, 0).rotateRad(anchor.body.getAngle()));
        anchor = null;

        fixture.setSensor(false);
    }

    public Player getAnchor() {
        return anchor;
    }
}

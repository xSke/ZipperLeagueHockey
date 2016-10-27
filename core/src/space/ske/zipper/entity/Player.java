package space.ske.zipper.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import space.ske.zipper.Assets;
import space.ske.zipper.Renderer;
import space.ske.zipper.Zipper;
import space.ske.zipper.ai.AI;
import space.ske.zipper.util.SpringingContext2D;

public class Player extends Entity {
    private AI ai;
    private boolean team;
    private int sprite;
    private Vector2 startPos;

    private final Fixture sensor;
    private SpringingContext2D velocity = new SpringingContext2D(1, 4);

    private float timer;
    private float stepTimer;
    private boolean isShooting;
    private Puck holding;

    private boolean mouseLeftDown = false;
    private boolean playerPlaying = true;

    public Player(AI ai, boolean team, int sprite, Vector2 startPos) {
        this.ai = ai;
        this.team = team;
        this.sprite = sprite;
        this.startPos = startPos;
        ai.setPlayer(this);
        body.setType(BodyDef.BodyType.DynamicBody);

        Fixture collider = createCircle(1.0f);
        Filter filter = new Filter();
        filter.maskBits = 0x1;
        collider.setFilterData(filter);

        sensor = createCircle(0.9f);
        sensor.setSensor(true);

        body.setTransform(startPos, 0);
        body.setFixedRotation(true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        if (!Zipper.i.isGameGoing()) {
            body.setTransform(startPos, 0);
            timer = 0;
            isShooting = false;
            return;
        }
        if (Zipper.i.isGameDone()) {
            body.setLinearVelocity(0, 0);
            return;
        }
        timer += dt;

        Vector2 input = new Vector2();
        if (Zipper.i.getFocus() == this && playerPlaying) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) input.y += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) input.y -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) input.x -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) input.x += 1;
        } else {
            input = ai.update(dt);
        }

        if (!input.isZero(0.1f) && Zipper.i.getFocus() == this) {
            stepTimer += dt;
            if (stepTimer > 0.2f) {
                stepTimer -= 0.2f;

                Assets.step.play(0.1f, MathUtils.random(0.9f, 1.1f), 0);
            }
        }

        velocity.target.set(input.nor().scl(20));
        velocity.update(dt);

        body.setLinearVelocity(velocity.value);
        if (!body.getLinearVelocity().isZero(0.01f)) {
            body.setTransform(body.getPosition(), velocity.value.angleRad());
        }

        boolean mouseLeftDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean shouldShoot;
        if (Zipper.i.getFocus() == this && team && playerPlaying) {
            shouldShoot = Gdx.input.isKeyJustPressed(Input.Keys.O) || Gdx.input.isKeyJustPressed(Input.Keys.Z) || (mouseLeftDown && !this.mouseLeftDown);
        } else {
            shouldShoot = ai.shouldShoot();
        }

        if (shouldShoot && !isShooting) {
            Zipper.i.getPuck().shot();
            isShooting = true;
            timer = 0;
            Zipper.i.getRenderer().shake(0.5f);
            holding = null;
            Assets.shoot.play();
        }

        if (isShooting && timer > 0.4f) {
            isShooting = false;
        }

        this.mouseLeftDown = mouseLeftDown;
    }

    @Override
    public void collide(Fixture thisFixture, Entity other) {
        if (other instanceof Puck) {
            Player anchor = ((Puck) other).getAnchor();
            boolean canSteal = anchor == null;
            if (!canSteal && anchor.isTeam() != team) {
                if (MathUtils.randomBoolean(0.5f)) {
                    canSteal = true;
                    Assets.steal.play();
                }
            }
            if (!isShooting && thisFixture == sensor && canSteal) {
                Zipper.i.getRenderer().shake(0.3f);
                ((Puck) other).grabbedBy(this);
                holding = (Puck) other;

                if (team) {
                    Zipper.i.setFocus(this);
                    Assets.focus.play();
                }
            }
        }
    }

    @Override
    public void render(Renderer r) {
        SpriteBatch b = r.batch();

        boolean backwards = body.getLinearVelocity().y > 0;

        TextureRegion t;
        if (isShooting) {
            if (!backwards) {
                t = Assets.shootFront.get(sprite).getKeyFrame(timer);
            } else {
                t = Assets.shootBack.get(sprite).getKeyFrame(timer);
            }
        } else if (body.getLinearVelocity().isZero(0.01f)) {
            if (!backwards) {
                t = Assets.players[0][sprite];
            } else {
                t = Assets.players[1][sprite];
            }
        } else {
            if (!backwards) {
                t = Assets.skateFront.get(sprite).getKeyFrame(timer);
            } else {
                t = Assets.skateBack.get(sprite).getKeyFrame(timer);
            }
        }

        Vector2 p = body.getPosition();
        p.y += 0.25f;
        b.draw(t, r.round(p.x - t.getRegionWidth() / 16f), r.round(p.y - t.getRegionHeight() / 16f), t.getRegionWidth() / 8f, t.getRegionHeight() / 8f);
    }

    public boolean isTeam() {
        return team;
    }
}

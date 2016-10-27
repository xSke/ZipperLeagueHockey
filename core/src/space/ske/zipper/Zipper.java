package space.ske.zipper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import space.ske.zipper.ai.PlayerAI;
import space.ske.zipper.entity.*;
import space.ske.zipper.util.SpringingContext2D;

import java.util.Comparator;

public class Zipper extends ScreenAdapter {
    public static Zipper i;

    private SpringingContext2D cameraSpring = new SpringingContext2D(1, 6);

    private Renderer renderer;

    private World world;

    private Array<Entity> entities = new Array<Entity>();
    private Array<Player> players = new Array<Player>();
    private Puck puck;
    private Player focus;
    private boolean justScored = false;

    private float countdownTimer = 4;
    private boolean isGameGoing = false;
    private boolean gameDone = false;

    private int homeGoal;
    private int awayGoal;
    private float gameTimer = 180;
    private float uiTimer;
    private boolean overtime;
    private float restartTimer;

    public Zipper() {
        i = this;
        renderer = new Renderer();
        world = new World(new Vector2(), false);
        entities.add(new Rink());
        entities.add(new Player(new PlayerAI(), true, 3, new Vector2(27, 31)));
        entities.add(new Player(new PlayerAI(), true, 3, new Vector2(30, 32)));
        entities.add(new Player(new PlayerAI(), true, 3, new Vector2(33, 31)));
        entities.add(new Player(new PlayerAI(), false, 1, new Vector2(27, 23)));
        entities.add(new Player(new PlayerAI(), false, 1, new Vector2(30, 22)));
        entities.add(new Player(new PlayerAI(), false, 1, new Vector2(33, 23)));
        entities.add(puck = new Puck());
        entities.add(new Net(false));
        entities.add(new Net(true));

        for (Entity entity : entities) {
            if (entity instanceof Player) players.add((Player) entity);
        }

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.getFixtureA().getBody().getUserData() instanceof Entity && contact.getFixtureB().getBody().getUserData() instanceof Entity) {
                    ((Entity) contact.getFixtureA().getBody().getUserData()).collide(contact.getFixtureA(), (Entity) contact.getFixtureB().getBody().getUserData());
                    ((Entity) contact.getFixtureB().getBody().getUserData()).collide(contact.getFixtureB(), (Entity) contact.getFixtureA().getBody().getUserData());
                }
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(dt, 20, 20);
        handleCamera(dt);

        renderer.update(dt);
        renderer.begin(false);

        entities.sort(new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                if (o1.z != o2.z) return Float.compare(o1.z, o2.z);
                return Float.compare(-o1.body.getPosition().y, -o2.body.getPosition().y);
            }
        });

        Array.ArrayIterator<Entity> entities = new Array.ArrayIterator<Entity>(this.entities);
        for (Entity entity : entities) {
            entity.update(dt);
            if (entity.shouldDestroy()) {
                entities.remove();
                world.destroyBody(entity.body);
            }
        }

        for (Entity entity : this.entities) {
            entity.render(renderer);
        }
        renderer.end();

        uiTimer += dt;

        renderer.begin(true);
        renderUI();
        renderer.end();

        countdownTimer -= dt;
        if (!isGameGoing) {
            if (countdownTimer < 3 && countdownTimer + dt > 3) Assets.beep1.play();
            if (countdownTimer < 2 && countdownTimer + dt > 2) Assets.beep1.play();
            if (countdownTimer < 1 && countdownTimer + dt > 1) Assets.beep1.play();
            if (countdownTimer < 0) {
                isGameGoing = true;
                Assets.beep2.play();
            }
        } else {
            gameTimer -= dt;
        }

        if (gameTimer < 0 && !gameDone) {
            if (homeGoal == awayGoal) {
                if (!overtime) {
                    overtime = true;
                    Assets.end.play();
                    countdownTimer = 4;
                    isGameGoing = false;
                }
                gameTimer = 0;
            } else {
                gameDone = true;
            }
        }

        if (gameDone) {
            restartTimer += dt;
            if (restartTimer > 3) {
                Game.i.setScreen(new MainMenu());
            }
        }

        //new Box2DDebugRenderer().render(world, renderer.getCamera().combined);
    }

    void reset() {
        countdownTimer = 4;
        isGameGoing = false;
        gameDone = false;
        homeGoal = 0;
        awayGoal = 0;
        overtime = false;
        restartTimer = 0;
        gameTimer = 180;
        justScored = false;
        focus = null;
        if (puck != null) {
            puck.body.setTransform(30, 27, 0);
        }
    }

    private void renderUI() {
        SpriteBatch batch = renderer.batch();

        if (!isGameGoing) {
            int ss = 13;


            if (overtime) {
                if (countdownTimer < 4 && countdownTimer > 3.3f)
                    batch.draw(Assets.o, ss, 130 - (countdownTimer + 0.1f) * 14);
                if (countdownTimer < 3.95f && countdownTimer > 3.25f)
                    batch.draw(Assets.v, ss + 17, 130 - (countdownTimer + 0.2f) * 14);
                if (countdownTimer < 3.9f && countdownTimer > 3.2f)
                    batch.draw(Assets.e, ss + 34, 130 - (countdownTimer + 0.3f) * 14);
                if (countdownTimer < 3.85f && countdownTimer > 3.15f)
                    batch.draw(Assets.r, ss + 51, 130 - (countdownTimer + 0.4f) * 14);
                if (countdownTimer < 3.8f && countdownTimer > 3.1f)
                    batch.draw(Assets.t, ss + 68, 130 - (countdownTimer + 0.5f) * 14);
                if (countdownTimer < 3.75f && countdownTimer > 3.05f)
                    batch.draw(Assets.i, ss + 85, 130 - (countdownTimer + 0.6f) * 14);
                if (countdownTimer < 3.7f && countdownTimer > 3.0f)
                    batch.draw(Assets.m, ss + 102, 130 - (countdownTimer + 0.7f) * 14);
                if (countdownTimer < 3.65f && countdownTimer > 2.95f)
                    batch.draw(Assets.e, ss + 119, 130 - (countdownTimer + 0.8f) * 14);
            }

            if (countdownTimer < 3 && countdownTimer > 2.5f) {
                batch.draw(Assets.bigThree, 80 - 16, 85 - (countdownTimer - 2) * 8);
            }

            if (countdownTimer < 2 && countdownTimer > 1.5f) {
                batch.draw(Assets.bigTwo, 80 - 16, 85 - (countdownTimer - 1) * 8);
            }

            if (countdownTimer < 1 && countdownTimer > 0.5f) {
                batch.draw(Assets.bigOne, 80 - 16, 85 - countdownTimer * 8);
            }
        } else {
            if (countdownTimer < 0 && countdownTimer > -0.7f) {
                batch.draw(Assets.g, 80 - 20f, 80 - (countdownTimer) * 10);
            }
            if (countdownTimer < 0.1f && countdownTimer > -0.8f) {
                batch.draw(Assets.o, 80 - 3f, 80 - (countdownTimer + 0.1f) * 10);
            }
            if (countdownTimer < 0.2f && countdownTimer > -0.9f) {
                batch.draw(Assets.exclamation, 80 + 10, 80 - (countdownTimer + 0.2f) * 10);
            }
        }

        batch.draw(Assets.scoreboard, 0, 0);

        int home1 = homeGoal / 10;
        int home2 = homeGoal % 10;
        batch.draw(Assets.numbers[home1], 30, 6);
        batch.draw(Assets.numbers[home2], 38, 6);

        int away1 = awayGoal / 10;
        int away2 = awayGoal % 10;
        batch.draw(Assets.numbers[away1], 137, 6);
        batch.draw(Assets.numbers[away2], 145, 6);

        if (!overtime) {
            float t = gameTimer;
            if (t < 0) t = 0;
            int timer1 = (int) (t / 60 / 10);
            int timer2 = (int) (t / 60 % 10);
            int timer3 = (int) (t % 60 / 10);
            int timer4 = (int) (t % 60 % 10);
            batch.draw(Assets.numbers[timer1], 71, 6);
            batch.draw(Assets.numbers[timer2], 78, 6);
            batch.draw(Assets.numbers[timer3], 88, 6);
            batch.draw(Assets.numbers[timer4], 95, 6);
        } else {
            batch.draw(Assets.smallo, 78, 6);
            batch.draw(Assets.smallt, 85, 6);
        }

        if (gameDone) {
            if (homeGoal <= awayGoal) {
                int s = 14;
                batch.draw(Assets.y, s, 75 + MathUtils.sin(uiTimer * 3) * 5);
                batch.draw(Assets.o, s + 17, 75 + MathUtils.sin(uiTimer * 3 + 0.2f) * 5);
                batch.draw(Assets.u, s + 34, 75 + MathUtils.sin(uiTimer * 3 + 0.4f) * 5);
                batch.draw(Assets.l, s + 56, 75 + MathUtils.sin(uiTimer * 3 + 0.6f) * 5);
                batch.draw(Assets.o, s + 73, 75 + MathUtils.sin(uiTimer * 3 + 0.8f) * 5);
                batch.draw(Assets.s, s + 90, 75 + MathUtils.sin(uiTimer * 3 + 1.0f) * 5);
                batch.draw(Assets.e, s + 107, 75 + MathUtils.sin(uiTimer * 3 + 1.2f) * 5);
                batch.draw(Assets.exclamation, s + 120, 75 + MathUtils.sin(uiTimer * 3 + 1.4f) * 5);
            } else {
                int s = 22;
                batch.draw(Assets.y, s, 75 + MathUtils.sin(uiTimer * 5) * 8);
                batch.draw(Assets.o, s + 17, 75 + MathUtils.sin(uiTimer * 5 + 0.2f) * 8);
                batch.draw(Assets.u, s + 34, 75 + MathUtils.sin(uiTimer * 5 + 0.4f) * 8);
                batch.draw(Assets.w, s + 56, 75 + MathUtils.sin(uiTimer * 5 + 0.6f) * 8);
                batch.draw(Assets.i, s + 73, 75 + MathUtils.sin(uiTimer * 5 + 0.8f) * 8);
                batch.draw(Assets.n, s + 90, 75 + MathUtils.sin(uiTimer * 5 + 1.0f) * 8);
                batch.draw(Assets.exclamation, s + 105, 75 + MathUtils.sin(uiTimer * 5 + 1.2f) * 8);
            }
        }
    }

    private void handleCamera(float dt) {
        Renderer r = Zipper.i.getRenderer();
        OrthographicCamera c = r.getCamera();
        calculateCameraTarget(cameraSpring.target);

        cameraSpring.update(dt);
        c.position.set(cameraSpring.value, 0);
        c.position.x = r.round(MathUtils.clamp(c.position.x, c.viewportWidth / 2f, 60 - c.viewportWidth / 2f));
        c.position.y = r.round(MathUtils.clamp(c.position.y, c.viewportHeight / 2f, 54 - c.viewportHeight / 2f));
        c.update();
    }

    public void calculateCameraTarget(Vector2 out) {
        if (focus != null) {
            Body body = focus.body;
            out.set(body.getPosition());
            out.add(Zipper.i.getPuck().body.getPosition());
            out.scl(0.5f);

            Vector2 playerCenter = new Vector2();
            int weight = 0;
            for (Player player : players) {
                weight += 1;
                playerCenter.add(player.body.getPosition());
            }
            playerCenter.scl(1f / weight);

            out.add(playerCenter).scl(0.5f);

            Vector2 deltaFromPlayer = out.cpy().sub(body.getPosition());
            deltaFromPlayer.limit(6);
            out.set(body.getPosition()).add(deltaFromPlayer);
        } else {
            out.set(puck.body.getPosition());
        }
    }

    @Override
    public void dispose() {
    }

    public World getWorld() {
        return world;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void scored(boolean top) {
        if (justScored) return;
        Assets.goal.play();

        if (!overtime) {
            isGameGoing = false;
            countdownTimer = 4;
        }

        puck.destroy();
        justScored = true;

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                puck = new Puck();
                entities.add(puck);
                justScored = false;
            }
        });

        renderer.shake(2);

        if (top) awayGoal++;
        else homeGoal++;
    }

    public Puck getPuck() {
        return puck;
    }

    public Player getFocus() {
        return focus;
    }

    public void setFocus(Player focus) {
        this.focus = focus;
    }

    public Array<Player> getPlayers() {
        return players;
    }

    public boolean isGameGoing() {
        return isGameGoing;
    }

    public boolean isGameDone() {
        return gameDone;
    }
}

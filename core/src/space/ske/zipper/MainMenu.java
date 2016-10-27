package space.ske.zipper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import space.ske.zipper.util.SpringingContext1D;

public class MainMenu extends ScreenAdapter {
    private float timer;

    private SpringingContext1D zY = new SpringingContext1D(1, 2);
    private SpringingContext1D bannerX = new SpringingContext1D(1, 2);
    private SpringingContext1D rinkY = new SpringingContext1D(1, 2);

    boolean done = false;

    public MainMenu() {
        super();

        zY.value = 100;
        zY.target = 0;

        bannerX.value = 100;
        bannerX.target = 100;

        rinkY.value = -100;
        rinkY.target = -100;

        Assets.beep2.play();
    }

    @Override
    public void render(float delta) {
        timer += delta;
        zY.update(delta);
        bannerX.update(delta);
        rinkY.update(delta);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Zipper.i.getRenderer().begin(true);
        SpriteBatch b = Zipper.i.getRenderer().batch();

        b.draw(Assets.menuBG, 0, 0);

        b.draw(Assets.menuZ, 0, zY.value);
        if (timer > 0.45f) {
            if (timer - delta <= 0.45f) Assets.step.play();
            b.draw(Assets.menuI, Math.min(0, (timer - 0.6f) * 15), 0);
        }
        if (timer > 0.55f) {
            if (timer - delta <= 0.55f) Assets.step.play();
            b.draw(Assets.menuP1, Math.min(0, (timer - 0.7f) * 15), 0);
        }
        if (timer > 0.65f) {
            if (timer - delta <= 0.65f) Assets.step.play();
            b.draw(Assets.menuP2, Math.min(0, (timer - 0.8f) * 15), 0);
        }
        if (timer > 0.75f) {
            if (timer - delta <= 0.75f) Assets.step.play();
            b.draw(Assets.menuE, Math.min(0, (timer - 0.9f) * 15), 0);
        }
        if (timer > 0.85f) {
            if (timer - delta <= 0.85f) Assets.step.play();
            b.draw(Assets.menuR, Math.min(0, (timer - 1.0f) * 15), 0);
        }

        if (timer > 1.2f) {
            if (timer - delta <= 1.2f) Assets.focus.play();
            bannerX.target = 0;

            b.draw(Assets.menuBanner, bannerX.value, 0);
        }


        if (timer > 1.4f) {
            if (timer - delta <= 1.4f) Assets.focus.play();
            rinkY.target = 0;

            b.draw(Assets.menuRink, 0, rinkY.value);
        }

        b.draw(Assets.menuBorder, 0, 0);

        Zipper.i.getRenderer().end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Zipper.i.reset();
            Game.i.setScreen(Zipper.i);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}

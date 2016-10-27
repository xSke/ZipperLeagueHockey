package space.ske.zipper;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import space.ske.zipper.util.SpringingContext1D;

public class Splash extends ScreenAdapter {
    float timer;

    private SpringingContext1D pineconeX = new SpringingContext1D(1, 2);
    private SpringingContext1D skeX = new SpringingContext1D(1, 2);
    private SpringingContext1D borderX = new SpringingContext1D(1, 2);

    public Splash() {
        pineconeX.target = 0;
        pineconeX.value = 160;

        skeX.target = 160;
        skeX.value = 160;

        borderX.target = 160;
        borderX.value = 160;
    }

    @Override
    public void render(float delta) {
        timer += delta;

        if (timer > 0 && timer - delta <= 0) {
            Assets.beep1.play();
        }

        if (timer > 3 && timer - delta <= 3) {
            Assets.beep1.play();
        }
        if (timer > 6 && timer - delta <= 6) {
            Assets.beep1.play();
        }

        pineconeX.update(delta);
        skeX.update(delta);
        borderX.update(delta);

        Renderer r = Zipper.i.getRenderer();
        r.begin(true);
        SpriteBatch b = r.batch();

        if (timer > 3f) {
            pineconeX.target = -160;
            skeX.target = 0;
        }
        if (timer > 6) {
            skeX.target = -160;
            borderX.target = 0;
        }
        if (timer > 7) {
            Game.i.setScreen(new MainMenu());
        }

        b.draw(Assets.menuBG, 0, 0);
        b.draw(Assets.pinecone, pineconeX.value, 0);
        b.draw(Assets.ske, skeX.value, 0);
        b.draw(Assets.menuBorder, borderX.value, 0);
        r.end();
    }
}

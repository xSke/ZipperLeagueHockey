package space.ske.zipper;

public class Game extends com.badlogic.gdx.Game {
    public static Game i;

    @Override
    public void create() {
        i = this;

        new Zipper();
        setScreen(new Splash());
    }
}

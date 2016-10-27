package space.ske.zipper.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import space.ske.zipper.Game;
import space.ske.zipper.Zipper;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.width = 480;
        config.height = 432;
        new LwjglApplication(new Game(), config);
    }
}

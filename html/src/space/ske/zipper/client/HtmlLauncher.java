package space.ske.zipper.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import space.ske.zipper.Game;
import space.ske.zipper.Zipper;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 432);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Game();
        }
}
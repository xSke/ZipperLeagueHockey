package space.ske.zipper.entity;

import space.ske.zipper.Assets;
import space.ske.zipper.Renderer;

public class Rink extends Entity {
    public Rink() {
        z = -10;

        createRectangle(11.625f, 60, 11.625f / 2f, 30);
        createRectangle(11.625f, 60, 48.375f + 11.625f / 2f, 30);

        createRectangle(54, 1.5f, 27, 0.75f);
        createRectangle(54, 1.5f, 27, 52.5f + 0.75f);

        /*createEdge(11.625f, 1.5f, 48.375f, 1.5f);
        createEdge(11.625f, 1.5f, 11.625f, 52.5f);

        createEdge(11.625f, 52.5f, 48.375f, 52.5f);
        createEdge(48.375f, 1.5f, 48.375f, 52.5f);*/
    }

    @Override
    public void render(Renderer renderer) {
        renderer.batch().draw(Assets.rink, 0, 0, 60, 54);
    }
}

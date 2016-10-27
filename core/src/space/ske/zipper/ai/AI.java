package space.ske.zipper.ai;

import com.badlogic.gdx.math.Vector2;
import space.ske.zipper.entity.Player;

public abstract class AI {
    public Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract Vector2 update(float dt);
    public abstract boolean shouldShoot();
}

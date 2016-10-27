package space.ske.zipper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Assets {
    public static TextureRegion net = new TextureRegion(new Texture("net.png"));
    public static TextureRegion[][] players = TextureRegion.split(new Texture("players.png"), 20, 20);
    public static TextureRegion puck = new TextureRegion(new Texture("puck.png"));
    public static TextureRegion rink = new TextureRegion(new Texture("rink.png"));

    public static Texture menuBG = new Texture("title/bg.png");
    public static Texture menuZ = new Texture("title/z.png");
    public static Texture menuI = new Texture("title/i.png");
    public static Texture menuP1 = new Texture("title/p1.png");
    public static Texture menuP2 = new Texture("title/p2.png");
    public static Texture menuE = new Texture("title/e.png");
    public static Texture menuR = new Texture("title/r.png");
    public static Texture menuBanner = new Texture("title/banner.png");
    public static Texture menuRink = new Texture("title/rink.png");
    public static Texture menuBorder = new Texture("title/border.png");

    public static Array<Animation> skateFront = new Array<Animation>();
    public static Array<Animation> skateBack = new Array<Animation>();
    public static Array<Animation> shootFront = new Array<Animation>();
    public static Array<Animation> shootBack = new Array<Animation>();
    public static Array<Animation> checkFront = new Array<Animation>();
    public static Array<Animation> checkBack = new Array<Animation>();

    public static Sound shoot = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
    public static Sound goal = Gdx.audio.newSound(Gdx.files.internal("goal.wav"));
    public static Sound focus = Gdx.audio.newSound(Gdx.files.internal("focus.wav"));
    public static Sound step = Gdx.audio.newSound(Gdx.files.internal("step.wav"));
    public static Sound steal = Gdx.audio.newSound(Gdx.files.internal("steal.wav"));

    public static Sound beep1 = Gdx.audio.newSound(Gdx.files.internal("beep1.wav"));
    public static Sound beep2 = Gdx.audio.newSound(Gdx.files.internal("beep2.wav"));
    public static Sound end = Gdx.audio.newSound(Gdx.files.internal("end.wav"));

    public static Texture bigZero = new Texture("bignumbers/0.png");
    public static Texture bigOne = new Texture("bignumbers/1.png");
    public static Texture bigTwo = new Texture("bignumbers/2.png");
    public static Texture bigThree = new Texture("bignumbers/3.png");

    public static Texture scoreboard = new Texture("scoreboard.png");

    public static Texture exclamation = new Texture("bigletters/!.png");
    public static Texture a = new Texture("bigletters/a.png");
    public static Texture e = new Texture("bigletters/e.png");
    public static Texture g = new Texture("bigletters/g.png");
    public static Texture i = new Texture("bigletters/i.png");
    public static Texture l = new Texture("bigletters/l.png");
    public static Texture m = new Texture("bigletters/m.png");
    public static Texture n = new Texture("bigletters/n.png");
    public static Texture o = new Texture("bigletters/o.png");
    public static Texture r = new Texture("bigletters/r.png");
    public static Texture s = new Texture("bigletters/s.png");
    public static Texture t = new Texture("bigletters/t.png");
    public static Texture u = new Texture("bigletters/u.png");
    public static Texture v = new Texture("bigletters/v.png");
    public static Texture w = new Texture("bigletters/w.png");
    public static Texture y = new Texture("bigletters/y.png");
    public static TextureRegion[] numbers = TextureRegion.split(new Texture("numbers.png"), 8, 8)[0];

    public static Texture smallo = new Texture("o.png");
    public static Texture smallt = new Texture("t.png");

    public static Texture pinecone = new Texture("pinecone.png");
    public static Texture ske = new Texture("ske.png");

    static {
        for (int i = 1; i <= 4; i++) {
            TextureRegion[][] split1 = TextureRegion.split(new Texture("t" + i + "skate.png"), 40, 20);
            Animation skf = new Animation(0.1f, split1[0]);
            skf.setPlayMode(Animation.PlayMode.LOOP);
            Animation skb = new Animation(0.1f, split1[1]);
            skb.setPlayMode(Animation.PlayMode.LOOP);

            TextureRegion[][] split2 = TextureRegion.split(new Texture("t" + i + "shoot.png"), 40, 40);
            Animation shf = new Animation(0.1f, split2[0]);
            Animation shb = new Animation(0.1f, split2[1]);

            skateFront.add(skf);
            skateBack.add(skb);
            shootFront.add(shf);
            shootBack.add(shb);
        }
    }
}

package conceptm.content;

import arc.graphics.g2d.Fill;
import arc.math.Rand;
import arc.math.geom.Vec2;
import mindustry.entities.Effect;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.color;

public class Fx {
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();
    public static final Effect
            shootMantel = new Effect(10, e -> {
                color(Pal.lightOrange, e.color, e.fin());
                float w = 2.3f + 10 * e.fout();
                Drawf.tri(e.x, e.y, w, 70f * e.fout(), e.rotation);
                Drawf.tri(e.x, e.y, w, 12f * e.fout(), e.rotation + 180f);
            }),
            shootSmokeMantel = new Effect(70f, e -> {
                rand.setSeed(e.id);
                float w = 20;
                for(int i = 0; i < w; i++){
                    v.trns(e.rotation + rand.range(30f), rand.random(e.finpow() * 40f));
                    Vec2 v0 = new Vec2(0, i - (w/2f)).rotate(e.rotation + rand.range(30f));
                    e.scaled(e.lifetime * rand.random(0.3f, 1f), b -> {
                        color(e.color, Pal.lightishGray, b.fin());
                        Fill.circle(e.x + v.x + v0.x, e.y + v.y + v0.y, b.fout() * 3.4f + 0.3f);
                    });
                }
            })

            ;
}

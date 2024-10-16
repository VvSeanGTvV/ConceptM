package conceptm.world.draw;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import conceptm.graphics.BlendingCustom;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Pal;

import static arc.math.Mathf.rand;

public class CircleForceDraw extends DrawPart {
    public float x = 0, y = 0;
    public float orbRadius = 4.1f, orbMidScl = 0.33f, orbSinScl = 8f, orbSinMag = 1f, layerOffset = 0f;
    public Color color = Pal.suppress;
    public float layer = -1;
    public boolean under;

    public int particles = 15;
    public float particleSize = 4f;
    public float particleLen = 7f;
    public float rotateScl = 3f;
    public float particleLife = 110f;
    public boolean active = true;
    public Interp particleInterp = f -> Interp.circleOut.apply(Interp.slope.apply(f));
    public Color particleColor = Pal.sap.cpy();
    public int id;
    public Blending blending = BlendingCustom.Bloom;

    public CircleForceDraw() {
    }

    @Override
    public void draw(PartParams params) {
        float xC = params.x;
        float yC = params.y;
        float rotation = params.rotation;

        float z = Draw.z();
        if (under) Draw.z(z - 0.0001f);
        if (layer > 0) Draw.z(layer);
        Draw.z(Draw.z() + layerOffset);

        float rad = orbRadius + Mathf.absin(orbSinScl, orbSinMag);
        Tmp.v1.set(x, y).rotate(rotation);
        float rx = xC + Tmp.v1.x, ry = yC + Tmp.v1.y;

        /*Draw.alpha(0.35f);
        Draw.blend(Blending.additive);
        //Draw.z(Draw.z() - 0.0001f);
        Fill.circle(rx, ry, rad + Lines.getStroke() * orbMidScl);
        Draw.blend();
        Draw.alpha(1f);*/

        //Draw.blend(blending);
        float base = (Time.time / particleLife);
        rand.setSeed(id + hashCode());
        Draw.color(particleColor);
        Draw.blend(blending);
        for (int i = 0; i < particles; i++) {
            float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
            float angle = rand.random(360f) + (Time.time / rotateScl + rotation) % 360f;
            float len = particleLen * particleInterp.apply(fout);
            Fill.circle(
                    rx + Angles.trnsx(angle, len),
                    ry + Angles.trnsy(angle, len),
                    particleSize * Mathf.slope(fin)
            );
        }

        Draw.blend(blending);
        Lines.stroke(2f);

        Draw.color(color);
        Lines.circle(rx, ry, rad);

        Draw.color(color);
        Fill.circle(rx, ry, rad * orbMidScl);

        Draw.alpha(0.025f);
        Draw.scl(0.25f);
        Draw.blend(blending);
        Draw.color(color);
        TextureRegion particle = Core.atlas.find("circle-shadow");
        Draw.rect(particle, rx, ry, rotation);

        if (active) {
            //TODO draw range when selected?
        }

        Draw.reset();
    }

    @Override
    public void load(String name) {

    }
}

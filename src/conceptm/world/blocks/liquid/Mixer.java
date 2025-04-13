package conceptm.world.blocks.liquid;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.*;
import arc.util.Log;
import arc.util.Time;
import conceptm.world.blocks.CustomBlock;
import conceptm.world.type.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.meta.*;

import java.util.*;

import static conceptm.ModTemplate.ui;
import static mindustry.Vars.content;

public class Mixer extends CustomLiquidBlock {
    public TextureRegion rotorRegion, topRegion, bottomRegion;

    public Mixer(String name) {
        super(name);

        hasCustomItem = false;
        hasCustomLiquid = hasLiquids = true;
        update = true;
        solid = true;

        outputsLiquid = false;
        group = BlockGroup.liquids;
        configurable = true;
        envEnabled |= Env.space | Env.underwater;
    }

    @Override
    public void load() {
        super.load();
        bottomRegion = Core.atlas.find(name + "-bottom");
        topRegion = Core.atlas.find(name + "-top");
        rotorRegion = Core.atlas.find(name + "-rotor");
    }

    @Override
    public void setBars() {
        super.setBars();

        removeBar("liquid");
        removeBar("liquid-custom");
        addCustomLiquidBar((MixerBuild build) -> build.output);
    }

    public class MixerBuild extends CustomLiquidBuild {
        public float warmup;
        public float totalProgress, progress;

        boolean onceBar0 = false, onceBar1 = false;
        public Liquid select0, select1;
        public CustomLiquid output, select0c, select1c;

        public float getAmount(Object liquid){
            return (liquid instanceof Liquid liq) ? liquids.get(liq) :
                    (liquid instanceof CustomLiquid liq) ? customLiquids.get(liq) :
                            0f;
        }

        public float getCapacity(Object liquid){
            return (liquid instanceof Liquid) ? liquidCapacity :
                    (liquid instanceof CustomLiquid) ? customLiquidCapacity :
                            0f;
        }

        @Override
        public void draw() {

            Object l0 = (select0 != null && liquids.get(select0) > 0.001f) ? select0 :
                    (select0c != null && customLiquids.get(select0c) > 0.001f) ? select0c : null;

            Object l1 = (select1 != null && liquids.get(select1) > 0.001f) ? select1 :
                    (select1c != null && customLiquids.get(select1c) > 0.001f) ? select1c : null;

            float speed = (select0 != null && liquids.get(select0) > 0.001f) ? select0.viscosity :
                    (select0c != null && customLiquids.get(select0c) > 0.001f) ? select0c.viscosity : 1f;

            Draw.rect(bottomRegion, x, y);

            if (l0 != null) drawTiledFrames(size, x, y, 4f, l0, 0.5f * (getAmount(l0) / getCapacity(l0)));
            if (l1 != null) drawTiledFrames(size, x, y, 4f, l1, 0.5f * (getAmount(l1) / getCapacity(l1)));

            Drawf.spinSprite(rotorRegion, x, y, totalProgress * 2f);
            Draw.rect(region, x, y);

            if (output != null && customLiquids.get(output) > 0.001f) drawTiledFrames(2, x, y, 6f, output, getAmount(output) / getCapacity(output));

            Draw.rect(topRegion, x, y);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return this.block.hasLiquids;
        }

        @Override
        public boolean acceptCustomLiquid(Building source, CustomLiquid liquid) {
            return output == null || !Objects.equals(liquid.name, output.name);
        }

        @Override
        public void buildConfiguration(Table table) {
            table.table(t -> {
                if (output != null) {
                    t.add(output.localizedName, Styles.defaultLabel).pad(4f);
                    t.row();
                    //t.image(Styles.black8).fillX().height(3.0F).pad(3.0F).row();
                    t.row();
                } else {
                    t.add("???", Styles.defaultLabel).pad(4f);
                    t.row();
                    //t.image(Styles.black8).fillX().height(3.0F).pad(3.0F).row();
                    t.row();
                }
                t.table(a -> {
                    if (select0 != null) a.image(select0.fullIcon).pad(4f);
                    else if (select0c != null) a.image(select0c.fullIcon).color(select0c.color).pad(4f);
                    else a.add("?").pad(4f);

                    a.add("+").pad(4f);
                    if (select1 != null) a.image(select1.fullIcon).pad(4f);
                    else if (select1c != null) a.image(select1c.fullIcon).color(select1c.color).pad(4f);
                    else a.add("?").pad(4f);

                    a.image(Icon.rightSmall).pad(4f);
                    if (output != null) a.image(output.fullIcon).color(output.color).pad(4f);
                    else a.add("?").pad(4f);

                    if (output != null) a.button("?", Styles.flatBordert, () -> ui.content.showLiquid(output)).size(40f).pad(10).right().grow();
                }).growX().padTop(16f).get().setFillParent(true);
            }).growX().center().get().background(Styles.black8).setFillParent(true);
        }

        public void combine(float min){
            Object item0 = (select0 != null) ? select0 : (select0c != null) ? select0c : null;
            Object item1 = (select1 != null) ? select1 : (select1c != null) ? select1c : null;

            if (item0 != null && item1 != null) {
                CustomLiquid newCombo = new CustomLiquid(item0, item1);

                if (customLiquids.get(newCombo) < customLiquidCapacity) {
                    customLiquids.add(newCombo, min);
                    consumeLiquidObject(item0, item1);
                }

                output = newCombo;
            }

            progress %= 1f;
        }

        @Override
        public void updateTile() {
            float min = 1f;
            if (output != null && hasOutputs(output)) dumpLiquid(output);
            boolean valid = (select0 != null && select1 != null && (liquids.get(select0) >= min) && (liquids.get(select1) >= min)) ||
                    (select0c != null && select1 != null && (customLiquids.get(select0c) >= min)  && (liquids.get(select1) >= min) ) ||
                    (select0 != null && select1c != null && (customLiquids.get(select1c) >= min)  && (liquids.get(select0) >= min) ) ||
                    (select0c != null && select1c != null && (customLiquids.get(select0c) >= min)  && (customLiquids.get(select1c) >= min));

            boolean minvalid = (select0 != null && select1 != null && (liquids.get(select0) >= 0.001f) && (liquids.get(select1) >= 0.001f)) ||
                    (select0c != null && select1 != null && (customLiquids.get(select0c) >= 0.001f)  && (liquids.get(select1) >= 0.001f) ) ||
                    (select0 != null && select1c != null && (customLiquids.get(select1c) >= 0.001f)  && (liquids.get(select0) >= 0.001f) ) ||
                    (select0c != null && select1c != null && (customLiquids.get(select0c) >= 0.001f)  && (customLiquids.get(select1c) >= 0.001f));

            efficiency = (valid ? 1f : 0f);

            progress += getProgressIncrease(30f);
            if (minvalid && (output == null || customLiquids.get(output) < customLiquidCapacity)){
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, 0.02f);
            }
            
            totalProgress += warmup * edelta();
            
            if (liquids != null) {
                for (Liquid liq : content.liquids()) {
                    if (select0 == null && (liquids.get(liq) > min) && select0c == null && liq != select1) select0 = liq;
                    if (select1 == null && (liquids.get(liq) > min) && select1c == null && liq != select0) select1 = liq;
                }
            }

            if (customLiquids.any() && customLiquids != null) {
                for (CustomLiquidStack itemc : customLiquids.liquids) {
                    if (select0c == null && (customLiquids.get(itemc.liq) >= min) && select0 == null && (select1c == null || !Objects.equals(itemc.liq.name, select1c.name))) select0c = itemc.liq;
                    if (select1c == null && (customLiquids.get(itemc.liq) >= min) && select1 == null && (select0c == null || !Objects.equals(itemc.liq.name, select0c.name))) select1c = itemc.liq;
                }
            }


            if (valid) {
                reactLiquid(min);
                if (progress >= 1f) combine(min);
            }
        }

        public void reactLiquid(float min){
            float otherflame = (select1 != null && liquids != null && (liquids.get(select1) > min)) ? select1.flammability :
                    (select1c != null && customLiquids != null && (customLiquids.get(select1c) > min)) ? select1c.flammability : 0f;

            float destflame = (select0 != null && liquids != null && (liquids.get(select0) > min)) ? select0.flammability :
                    (select0c != null && customLiquids != null && (customLiquids.get(select0c) > min)) ? select0c.flammability : 0f;

            float othertemp = (select1 != null && liquids != null && (liquids.get(select1) > min)) ? select1.temperature :
                    (select1c != null && customLiquids != null && (customLiquids.get(select1c) > min)) ? select1c.temperature : 0f;

            float desttemp = (select0 != null && liquids != null && (liquids.get(select0) > min)) ? select0.temperature :
                    (select0c != null && customLiquids != null && (customLiquids.get(select0c) > min)) ? select0c.temperature : 0f;

            if ((!(otherflame > 0.3F) || !(desttemp > 0.7F)) && (!(destflame > 0.3F) || !(othertemp > 0.7F))) {
                if (desttemp > 0.7F && othertemp < 0.55F || othertemp > 0.7F && desttemp < 0.55F) {
                    if (Mathf.chanceDelta(0.20000000298023224)) {
                        Fx.steam.at(x, y);
                    }
                }
            } else {
                if (Mathf.chanceDelta(0.1)) {
                    Fx.fire.at(x, y);
                }
                if (Mathf.chanceDelta(0.05000000298023224)) {
                    Fx.steam.at(x, y);
                }
            }
        }
    }
}

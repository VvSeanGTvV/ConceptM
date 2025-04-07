package conceptm.world.blocks.liquid;

import arc.scene.ui.layout.Table;
import arc.struct.*;
import conceptm.world.blocks.CustomBlock;
import conceptm.world.type.*;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.meta.*;

import java.util.*;

import static conceptm.ModTemplate.ui;
import static mindustry.Vars.content;

public class Mixer extends CustomBlock {
    public Mixer(String name) {
        super(name);

        hasCustomLiquid = hasLiquids = true;
        update = true;
        solid = true;

        group = BlockGroup.liquids;
        configurable = true;
        envEnabled |= Env.space | Env.underwater;
    }

    @Override
    public void setBars() {
        super.setBars();

        removeBar("liquid");
    }

    public class MixerBuild extends CustomBuilding {
        protected OrderedMap<String, Boolean> liqBarKey = new OrderedMap<>();
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return this.block.hasLiquids;
        }

        public Liquid select0, select1;
        public CustomLiquid output, select0c, select1c;

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

        public void combine(){
            Object item0 = (select0 != null) ? select0 : (select0c != null) ? select0c : null;
            Object item1 = (select1 != null) ? select1 : (select1c != null) ? select1c : null;

            if (item0 != null && item1 != null) {
                CustomLiquid newCombo = new CustomLiquid(item0, item1);
                if (hasOutputs(newCombo)) {
                    customLiquids.add(newCombo, 1f);
                    consumeObject(item0, item1);
                    dumpLiquid(newCombo);
                }
                output = newCombo;
            }

            //progress %= 1f;
        }

        @Override
        public void updateTile() {
            float min = 1f;

            String keep0 = (select0 != null) ? select0.name : (select0c != null) ? select0c.name : "";
            String keep1 = (select1 != null) ? select1.name : (select1c != null) ? select1c.name : "";
            boolean valid = (select0 != null && select1 != null && (liquids.get(select0) > min) && (liquids.get(select0) > min)) ||
                    (select0c != null && select1 != null && (customLiquids.get(select0c) > min)  && (liquids.get(select1) > min) ) ||
                    (select0 != null && select1c != null && (customLiquids.get(select1c) > min)  && (liquids.get(select0) > min) ) ||
                    (select0c != null && select1c != null && (customLiquids.get(select0c) > min)  && (customLiquids.get(select1c) > min) );

            if (liquids != null) {
                for (Liquid liq : content.liquids()) {
                    if (select0 == null && (liquids.get(liq) > min) && select0c == null && liq != select1) select0 = liq;
                    if (select1 == null && (liquids.get(liq) > min) && select1c == null && liq != select0) select1 = liq;
                }

                if ((select0 != null && !(liquids.get(select0) > min))) select0 = null;
                if ((select1 != null && !(liquids.get(select1) > min))) select1 = null;
            } else {
                select0 = select1 = null;
            }

            if (customLiquids.any() && customLiquids != null) {
                for (CustomLiquidStack itemc : customLiquids.liquids) {
                    if (select0c == null && customLiquids.has(itemc.liq) && select0 == null && (select1c == null || !Objects.equals(itemc.liq.name, select1c.name))) select0c = itemc.liq;
                    if (select1c == null && customLiquids.has(itemc.liq) && select1 == null && (select0c == null || !Objects.equals(itemc.liq.name, select0c.name))) select1c = itemc.liq;
                }

                if (select0c != null && !customLiquids.has(select0c)) select0c = null;
                if (select1c != null && !customLiquids.has(select1c)) select1c = null;
            } else {
                select0c = select1c = null;
            }

            if (valid) combine();
        }

        @Override
        public void handleLiquid(Building source, Liquid liq, float amount) {
            super.handleLiquid(source, liq, amount);
        }
    }
}

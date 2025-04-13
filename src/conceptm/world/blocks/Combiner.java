package conceptm.world.blocks;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.io.*;
import conceptm.world.type.CustomItem;
import conceptm.world.type.CustomItemStack;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.meta.*;

import java.util.Objects;

import static conceptm.ModTemplate.ui;
import static mindustry.Vars.content;

public class Combiner extends CustomBlock {

    /** The time in number of ticks to make single item */
    public float craftTime = 30f;

    public float warmupSpeed = 0.019f;

    public TextureRegion botRegion, botMidRegion, pistonRegion0, pistonRegion1;
    public Combiner(String name) {
        super(name);

        update = true;
        solid = true;

        hasItems = true;
        configurable = true;
        saveConfig = false;
    }

    @Override
    public void setStats() {
        stats.timePeriod = craftTime;
        super.setStats();
        if((hasItems && itemCapacity > 0) || (hasCustomItem && customItemCapacity > 0)){
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }
    }

    @Override
    public void load() {
        super.load();
        botRegion = Core.atlas.find(name + "-bottom");
        botMidRegion = Core.atlas.find(name + "-bottom-mid");
        pistonRegion0 = Core.atlas.find(name + "-gate-0");
        pistonRegion1 = Core.atlas.find(name + "-gate-1");
    }

    public class CombinerBuild extends CustomBuilding {
        public float progress;
        public float totalProgress;
        public float warmup;

        public Item select0, select1;
        public CustomItem output, select0c, select1c;

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

                    if (output != null) a.button("?", Styles.flatBordert, () -> ui.content.showItem(output)).size(40f).pad(10).right().grow();
                }).growX().padTop(16f).get().setFillParent(true);
            }).growX().center().get().background(Styles.black8).setFillParent(true);
        }

        public void combine(){
            Object item0 = (select0 != null) ? select0 : (select0c != null) ? select0c : null;
            Object item1 = (select1 != null) ? select1 : (select1c != null) ? select1c : null;

            if (item0 != null && item1 != null) {
                CustomItem newCombo = new CustomItem(item0, item1);
                if (hasOutputs(newCombo)) {
                    consumeObject(item0, item1);
                    outputCombo(newCombo);
                }
            }

            progress %= 1f;
        }

        public boolean check(){
            Object item0 = (select0 != null) ? select0 : (select0c != null) ? select0c : null;
            Object item1 = (select1 != null) ? select1 : (select1c != null) ? select1c : null;
            if (item0 != null && item1 != null) {
                CustomItem newCombo = new CustomItem(item0, item1);
                output = newCombo;
                return hasOutputs(newCombo);
            }
            return false;
        }

        public float warmupTarget(){
            return 1f;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public void updateTile() {
            boolean valid = (select0 != null && select1 != null && items.has(select0) && items.has(select1)) ||
                    (select0c != null && select1 != null && customItems.has(select0c) && items.has(select1)) ||
                    (select0 != null && select1c != null && customItems.has(select1c) && items.has(select0)) ||
                    (select0c != null && select1c != null && customItems.has(select1c) && customItems.has(select0c));

            efficiency = (valid && check() ? 1f : 0f);

            if (efficiency > 0){
                progress += getProgressIncrease(craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta;

            if(progress >= 1f){
                combine();
                for (int i=0; i<10; i++) if (Mathf.chanceDelta(0.05000000298023224)) Fx.smeltsmoke.at(x, y);
            }

            if (items.any() && items != null) {
                for (Item item : content.items()) {
                    if (select0 == null && items.has(item) && select0c == null && item != select1) select0 = item;
                    if (select1 == null && items.has(item) && select1c == null && item != select0) select1 = item;
                }

                if ((select0 != null && !items.has(select0))) select0 = null;
                if ((select1 != null && !items.has(select1))) select1 = null;
            } else {
                select0 = select1 = null;
            }

            if (customItems.any() && customItems != null) {
                for (CustomItemStack itemc : customItems.items) {
                    if (select0c == null && customItems.has(itemc.item) && select0 == null && (select1c == null || !Objects.equals(itemc.item.name, select1c.name))) select0c = itemc.item;
                    if (select1c == null && customItems.has(itemc.item) && select1 == null && (select0c == null || !Objects.equals(itemc.item.name, select0c.name))) select1c = itemc.item;
                }

                if (select0c != null && !customItems.has(select0c)) select0c = null;
                if (select1c != null && !customItems.has(select1c)) select1c = null;
            } else {
                select0c = select1c = null;
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public boolean isOutput(CustomBuilding source) {
            return true;
        }

        @Override
        public void draw() {
            //super.draw();

            Draw.rect(botRegion, x, y);

            float offset = 2.65f;
            float pistonOffset = 7.4f;
            float itemSize = 6f;

            if (Mathf.clamp(progress * 3f) >= 1f) {

                float op = Math.max(1f - progress * 2f, 0f);
                float op1 = Math.max(1f - progress, 0f);
                Draw.rect(botMidRegion, x, y);
                if (output != null) output.draw(x, y, op1 * itemSize);

                Draw.xscl = Mathf.clamp(op);
                if (Mathf.clamp(op) > 0f) Draw.rect(pistonRegion0, x - (pistonOffset + ((1f - Mathf.clamp(op)) * (itemSize / 2))), y);

                Draw.xscl = -Mathf.clamp(op);
                if (Mathf.clamp(op) > 0f) Draw.rect(pistonRegion1, x + (pistonOffset + ((1f - Mathf.clamp(op)) * (itemSize / 2))), y);
            } else {
                if (select1 != null)
                    Draw.rect(select1.fullIcon, x + (offset - (progress * (itemSize / 2)) * 3f) * size, y, itemSize, itemSize);
                if (select0 != null)
                    Draw.rect(select0.fullIcon, x - (offset - (progress * (itemSize / 2)) * 3f) * size, y, itemSize, itemSize);

                if (select1c != null)
                    select1c.draw(x + (offset - (progress * (itemSize / 2)) * 3f) * size, y, itemSize);
                if (select0c != null)
                    select0c.draw(x - (offset - (progress * (itemSize / 2)) * 3f) * size, y, itemSize);


                Draw.xscl = Mathf.clamp(progress * 3f);
                if (Mathf.clamp(progress * 3f) > 0f) Draw.rect(pistonRegion0, x - (pistonOffset + ((1f - Mathf.clamp(progress * 3f)) * (itemSize / 2))), y);

                Draw.xscl = -Mathf.clamp(progress * 3f);
                if (Mathf.clamp(progress * 3f) > 0f) Draw.rect(pistonRegion1, x + (pistonOffset + ((1f - Mathf.clamp(progress * 3f)) * (itemSize / 2))), y);
            }
            Draw.xscl = 1f;

            Draw.rect(region, x, y);
        }

        @Override
        public void write(Writes write) {
            /*write.str((select0 != null) ? select0.name : "unknown");
            write.i(items.get(select0));
            write.str((select0c != null) ? select0c.name : "unknown");
            write.i(customItems.get(select0c));
            write.str((select1 != null) ? select1.name : "unknown");
            write.i(items.get(select1));
            write.str((select1c != null) ? select1c.name : "unknown");
            write.i(customItems.get(select1c));*/
            super.write(write);
        }

        public Object parseSide(String side){
            if(side.startsWith("{")){
                return decodeAndCreateCustomItem(side); // recursively decode
            }

            // Try core item
            Item coreItem = content.items().find(i -> i.name.equals(side));
            if(coreItem != null) return coreItem;

            return null; // or customItem lookup if needed
        }

        public CustomItem decodeAndCreateCustomItem(String input){
            if(input == null || input.isEmpty()) return null;

            // Strip outer braces
            if(input.startsWith("{") && input.endsWith("}")){
                input = input.substring(1, input.length() - 1);
            }

            int depth = 0;
            for(int i = 0; i < input.length(); i++){
                char c = input.charAt(i);
                if(c == '{') depth++;
                else if(c == '}') depth--;
                else if(c == '+' && depth == 0){
                    String left = input.substring(0, i);
                    String right = input.substring(i + 1);

                    Object a = parseSide(left.trim());
                    Object b = parseSide(right.trim());

                    if(a == null || b == null) return null;

                    return new CustomItem(a, b);
                }
            }

            return null; // if no + found at base level
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            /*String vanillaStr0 = read.str();
            int total0 = read.i();
            String vanillaStr0c = read.str();
            int total0c = read.i();
            String vanillaStr1 = read.str();
            int total1 = read.i();
            String vanillaStr1c = read.str();
            int total1c = read.i();
            select0 = (Objects.equals(vanillaStr0, "unknown")) ? null : content.item(vanillaStr0);
            select0c = (Objects.equals(vanillaStr0c, "unknown")) ? null : decodeAndCreateCustomItem(vanillaStr0c);
            select1 = (Objects.equals(vanillaStr1, "unknown")) ? null : content.item(vanillaStr1);
            select1c = (Objects.equals(vanillaStr1c, "unknown")) ? null : decodeAndCreateCustomItem(vanillaStr1c);
            if (select0c != null) customItems.add(select0c, total0c);
            //if (select1 != null) items.add(select1, total1);
            if (select1c != null) customItems.add(select1c, total1c);*/
        }
    }
}

package conceptm.world.blocks.liquid;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import conceptm.world.type.*;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.heat.HeatConsumer;

import java.util.Objects;

public class Crucible extends CustomLiquidBlock {

    public float heatRequirement = 1000f;
    public float warmupSpeed = 0.019f;
    public Crucible(String name) {
        super(name);

        hasCustomItem = true;
        hasCustomLiquid = true;
        hasItems = true;

        hasLiquids = false;
    }

    public class CrucibleBuild extends CustomLiquidBuild implements HeatConsumer {
        public float[] sideHeat = new float[4];
        public float heat = 0f, warmup = 0f, totalProgress = 0f, progress = 0f;

        public Item select;
        public CustomItem selectc;
        public CustomLiquid output;

        public Seq<itemDisplay> itemDisplays = new Seq<>(Math.max(customItemCapacity, itemCapacity));

        @Override
        public void updateTile() {
            heat = calculateHeat(sideHeat);

            Object item = (select != null) ? select : (selectc != null) ? selectc : null;
            int amount = (select != null) ? items.get(select) : (selectc != null) ? customItems.get(selectc) : 0;
            if (heat > 0f && item != null && amount > 0){
                progress += getProgressIncrease(30f);
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);

                totalProgress += warmup * Time.delta;
                int hardness = (select != null) ? select.hardness : (selectc != null) ? selectc.hardness : 0;
                float requirement =  Mathf.floor((0.4f + hardness * 0.05f) * 100f);

                if(heat >= requirement) {
                    if (output == null) output = new CustomLiquid(item);
                    if (customLiquids.get(output) < customLiquidCapacity) {
                        handleCustomLiquid(this, output, (((output.gas) ? 1.5f : 0.85f) * (1 * (1f + hardness * 0.05f))) / 5f);
                    }
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            if (output != null && customLiquids.get(output) > 0.001f) {
                dumpLiquid(output);
            }

            if (progress >= 1f && item != null && output != null && customLiquids.get(output) < customLiquidCapacity) {
                consumeObject(item);
                if (itemDisplays.size > 0) itemDisplays.remove(0);
                progress %= 1;
            }
        }

        @Override
        public void draw() {
            Draw.rect(bottomRegion, x, y);
            if (itemDisplays.size > 0){
                for (var itemDisplay : itemDisplays){
                    itemDisplay.draw(x, y, 4f);
                }
            }

            if (output != null && customLiquids.get(output) > 0.001f){
                drawTiledFrames(size, x, y, 0f, output, customLiquids.get(output) / customLiquidCapacity);
            }
            Draw.rect(region, x, y);
        }

        @Override
        public void handleItem(Building source, Item item) {
            super.handleItem(source, item);
            select = item;
            itemDisplays.add(new itemDisplay(item.fullIcon, items.get(item), item.name));
        }

        @Override
        public void handeCustomItem(Building source, CustomItem item) {
            super.handeCustomItem(source, item);
            selectc = item;
            itemDisplay itemDisplay = new itemDisplay(item.fullIcon, customItems.get(item), item.name);
            itemDisplay.useColor = true;
            itemDisplay.color = item.color;
            itemDisplays.add(itemDisplay);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return ((select == null || item == select) && (items.get(item) < getMaximumAccepted(item))) && selectc == null;
        }

        @Override
        public boolean acceptCustomItem(Building source, CustomItem item) {
            return ((selectc == null || Objects.equals(item.name, selectc.name)) && (customItems.get(item) < getMaximumAccepted(item))) && select == null;
        }

        @Override
        public boolean acceptCustomLiquid(Building source, CustomLiquid liquid) {
            return false;
        }

        @Override
        public float[] sideHeat() {
            return sideHeat;
        }

        @Override
        public float heatRequirement() {
            return heatRequirement;
        }

        public class itemDisplay {
            TextureRegion textureRegion;
            Vec2 pos;
            String name;

            public boolean useColor;
            public Color color;

            public itemDisplay(TextureRegion textureRegion, int count, String name){
                float spread = Mathf.clamp(count * 0.5f, size, size + 2f);
                this.textureRegion = textureRegion;
                this.pos = new Vec2(Mathf.range(spread), Mathf.range(spread));
                this.name = name;
            }

            public void draw(float x, float y, float size){
                Draw.color((color != null && useColor) ? color : Color.white);
                Draw.rect(textureRegion, x + pos.x, y + pos.y, size, size);
                Draw.color();
            }
        }
    }
}

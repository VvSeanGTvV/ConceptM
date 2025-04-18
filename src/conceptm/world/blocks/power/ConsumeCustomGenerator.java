package conceptm.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import conceptm.world.type.CustomItem;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

import java.util.Objects;

import static conceptm.ModTemplate.ui;

public class ConsumeCustomGenerator extends GeneratorCustom {

    /** The time in number of ticks during which a single item will produce power. */
    public float itemDuration = 120f;

    public float warmupSpeed = 0.05f;
    public float effectChance = 0.01f;
    public Effect generateEffect = Fx.none, consumeEffect = Fx.none;
    public float generateEffectRange = 3f;
    public float baseLightRadius = 65f;


    public ConsumeCustomGenerator(String name) {
        super(name);

        configurable = true;
    }

    @Override
    public void init() {
        super.init();

        emitLight = true;
        lightRadius = baseLightRadius * size;
    }

    public class ConsumeComboGeneratorBuild extends GeneratorComboBuild{
        public float warmup, totalTime, efficiencyMultiplier = 1f, itemDurationMultiplier = 1;
        public CustomItem item;

        @Override
        public void buildConfiguration(Table table) {
            if (item != null) {
                table.table(c -> {
                    c.table(t -> {
                        t.image(item.fullIcon).color(item.color).pad(4f);
                        t.add(item.localizedName).pad(2f);
                        t.button("?", Styles.flatBordert, () -> ui.content.showItem(item)).size(40f).pad(10).right().grow();
                    }).pad(4f);

                    c.row();

                    c.table(a -> {
                        a.add(Core.bundle.format("stat.productiontime") + ": ").right();
                        a.add((int) ((itemDuration * itemDurationMultiplier) / 60) + " " + Core.bundle.format("unit.seconds")).color(Pal.items).left();
                        a.row();
                        a.add(Core.bundle.format("stat.basepowergeneration") + ": ").right();
                        a.add((Mathf.floor((powerProduction * productionEfficiency) * 6000) / 100) + " " + Core.bundle.format("unit.powersecond")).color(Pal.powerBar).left();
                    }).pad(10f);
                }).growX().center().get().background(Styles.black8).setFillParent(true);
            }
        }

        @Override
        public void updateEfficiencyMultiplier(){
            efficiencyMultiplier = 1f;
            if(item != null){
                float m = item.flammability;
                if(m > 0) efficiencyMultiplier *= m;
            }
        }

        @Override
        public void updateTile(){

            efficiency = ((customItems.any() && item != null) ? 1f : 0f);
            boolean valid = efficiency > 0;

            warmup = Mathf.lerpDelta(warmup, valid ? 1f : 0f, warmupSpeed);

            productionEfficiency = efficiency * efficiencyMultiplier;
            totalTime += warmup * Time.delta;

            //randomly produce the effect
            if(valid && Mathf.chanceDelta(effectChance)){
                generateEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
            }

            //make sure the multiplier doesn't change when there is nothing to consume while it's still running
            if(item != null && valid){
                itemDurationMultiplier = item.flammability - (item.explosiveness / 4);
            }

            //take in items periodically
            if(hasCustomItem && valid && generateTime <= 0f){
                customItems.remove(item, 1);
                consumeEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
                generateTime = 1f;
            }

            /*if(outputLiquid != null){ //TODO comboLiquid
                float added = Math.min(productionEfficiency * delta() * outputLiquid.amount, liquidCapacity - liquids.get(outputLiquid.liquid));
                liquids.add(outputLiquid.liquid, added);
                dumpLiquid(outputLiquid.liquid);

                if(explodeOnFull && liquids.get(outputLiquid.liquid) >= liquidCapacity - 0.01f){
                    kill();
                    Events.fire(new GeneratorPressureExplodeEvent(this));
                }
            }*/

            //generation time always goes down, but only at the end so consumeTriggerValid doesn't assume fake items
            generateTime -= delta() / (itemDuration * itemDurationMultiplier);
            if (!customItems.any()) item = null;
        }

        @Override
        public void handeCustomItem(Building source, CustomItem item) {
            if (item.flammability > 0) {
                super.handeCustomItem(source, item);
                this.item = item;
                updateEfficiencyMultiplier();
            }
        }

        @Override
        public boolean acceptCustomItem(Building source, CustomItem item) {
            return
                    ((this.customItems.get(item) < this.getMaximumAccepted(item) && this.item == null) ||
                    (this.customItems.get(item) < this.getMaximumAccepted(item) && this.item != null && Objects.equals(item.name, this.item.name))) && item.flammability > 0;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalTime;
        }

        @Override
        public void drawLight(){
            //???
            drawer.drawLight(this);
            //TODO hard coded
            Drawf.light(x, y, (60f + Mathf.absin(10f, 5f)) * size, Color.orange, 0.5f * warmup);
        }
    }
}

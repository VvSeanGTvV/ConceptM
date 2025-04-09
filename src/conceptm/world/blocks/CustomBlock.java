package conceptm.world.blocks;

import arc.Core;
import arc.func.Func;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.*;
import conceptm.world.modules.*;
import conceptm.world.type.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Puddles;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.world.*;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

public class CustomBlock extends Block {
    public int customItemCapacity = 10;

    public float customLiquidCapacity = 10f;

    public boolean hasCustomItem = true, hasCustomLiquid = false;

    public DrawBlock drawer = new DrawDefault();

    public CustomBlock(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        if (this.hasCustomItem && this.configurable) {
            this.addBar("customItems", (CustomBuilding entity) -> new Bar(() -> Core.bundle.format("bar.combos", entity.customItems.total()), () -> Pal.items, () -> (float)entity.customItems.total() / (float)this.customItemCapacity));
        }

        if (this.hasCustomLiquid) {
            addCustomLiquidBar(build -> build.customLiquids.current());
        }
    }

    public <T extends CustomBuilding> void addCustomLiquidBar(Func<T, CustomLiquid> current){
        addBar("liquid", (CustomBuilding entity) -> new Bar(
                () -> current.get((T)entity) == null || entity.customLiquids.get(current.get((T)entity)) <= 0.001f ? Core.bundle.get("bar.liquid") : current.get((T)entity).localizedName,
                () -> current.get((T)entity) == null ? Color.clear : current.get((T)entity).barColor,
                () -> current.get((T)entity) == null ? 0f : entity.customLiquids.get(current.get((T)entity)) / customLiquidCapacity)
        );
    }

    public class CustomBuilding extends Building {
        public CustomItemModule customItems;
        public CustomLiquidModule customLiquids;
        @Override
        public Building create(Block block, Team team) {

            if (hasCustomItem) {
                this.customItems = new CustomItemModule();
            }

            if (hasCustomLiquid) {
                this.customLiquids = new CustomLiquidModule();
            }

            return super.create(block, team);
        }

        public void outputCombo(CustomItem item) {
            int dump = this.cdump;

            for(int i = 0; i < this.proximity.size; ++i) {
                this.incrementDump(this.proximity.size);
                Building otherBuild = this.proximity.get((i + dump) % this.proximity.size);
                if (otherBuild instanceof CustomBuilding other) {
                    if (other.acceptCustomItem(this, item) && this.canDump(other, item)) {
                        other.handeCustomItem(this, item);
                        break;
                    }
                }
            }
        }

        public boolean hasOutputs(CustomItem item){
            int dump = this.cdump;

            for(int i = 0; i < this.proximity.size; ++i) {
                this.incrementDump(this.proximity.size);
                Building otherBuild = this.proximity.get((i + dump) % this.proximity.size);
                if (otherBuild instanceof CustomBuilding other) {
                    if (other.acceptCustomItem(this, item) && this.canDump(other, item)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean hasOutputs(CustomLiquid item){
            int dump = this.cdump;

            for(int i = 0; i < this.proximity.size; ++i) {
                this.incrementDump(this.proximity.size);
                Building otherBuild = this.proximity.get((i + dump) % this.proximity.size);
                if (otherBuild instanceof CustomBuilding other) {
                    if (other.acceptCustomLiquid(this, item) && this.canDumpLiquid(other, item)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean outputCombo(Building next, CustomItem item) {
            if (next instanceof CustomBuilding comboBuilding) {
                if (comboBuilding.acceptCustomItem(this, item) && this.canDump(comboBuilding, item)) {
                    comboBuilding.handeCustomItem(this, item);
                    return true;
                }
            }

            return false;
        }

        public void dumpLiquid(CustomLiquid liquid) {
            this.dumpLiquid(liquid, 2.0F);
        }

        public void dumpLiquid(CustomLiquid liquid, float scaling) {
            this.dumpLiquid(liquid, scaling, -1);
        }

        public void dumpLiquid(CustomLiquid liquid, float scaling, int outputDir) {
            int dump = this.cdump;
            if (!(this.customLiquids.get(liquid) <= 1.0E-4F)) {
                for(int i = 0; i < this.proximity.size; ++i) {
                    this.incrementDump(this.proximity.size);
                    CustomBuilding other = (this.proximity.get((i + dump) % this.proximity.size) instanceof CustomBuilding customBuilding) ? customBuilding : null;
                    if (other != null) {
                        if (outputDir == -1 || (outputDir + this.rotation) % 4 == this.relativeTo(other)) {
                            other = other.getLiquidDestination(this, liquid);
                            if (other.block instanceof CustomBlock customBlock && customBlock.hasCustomLiquid && this.canDumpLiquid(other, liquid) && other.customLiquids != null) {
                                float ofract = other.customLiquids.get(liquid) / customBlock.customLiquidCapacity;
                                float fract = this.customLiquids.get(liquid) / customLiquidCapacity;
                                if (ofract < fract) {

                                    this.transferLiquid(other, (fract - ofract) * customLiquidCapacity / scaling, liquid);
                                }
                            }
                        }
                    }
                }
            }
        }

        public float moveLiquid(CustomBuilding next, CustomLiquid liquid) {
            if (next == null) {
                return 0.0F;
            } else {
                next = next.getLiquidDestination(this, liquid);
                if (next.team == this.team && next.block.hasLiquids && this.customLiquids.get(liquid) > 0.0F) {
                    float ofract = next.customLiquids.get(liquid) / ((CustomBlock) next.block).customLiquidCapacity;
                    float fract = this.customLiquids.get(liquid) / customLiquidCapacity * this.block.liquidPressure;
                    float flow = Math.min(Mathf.clamp(fract - ofract) * customLiquidCapacity, this.customLiquids.get(liquid));
                    flow = Math.min(flow, ((CustomBlock) next.block).customLiquidCapacity - next.customLiquids.get(liquid));
                    if (flow > 0.0F && ofract <= fract && next.acceptCustomLiquid(this, liquid)) {
                        next.handeCustomLiquid(this, liquid, flow);
                        this.customLiquids.remove(liquid, flow);
                        return flow;
                    }

                    if (next.customLiquids.currentAmount() / ((CustomBlock) next.block).customLiquidCapacity > 0.1F && fract > 0.1F) {
                        float fx = (this.x + next.x) / 2.0F;
                        float fy = (this.y + next.y) / 2.0F;
                        CustomLiquid other = next.customLiquids.current();
                        if (other.blockReactive && liquid.blockReactive) {
                            if ((!(other.flammability > 0.3F) || !(liquid.temperature > 0.7F)) && (!(liquid.flammability > 0.3F) || !(other.temperature > 0.7F))) {
                                if (liquid.temperature > 0.7F && other.temperature < 0.55F || other.temperature > 0.7F && liquid.temperature < 0.55F) {
                                    this.customLiquids.remove(liquid, Math.min(this.customLiquids.get(liquid), 0.7F * Time.delta));
                                    if (Mathf.chanceDelta(0.20000000298023224)) {
                                        Fx.steam.at(fx, fy);
                                    }
                                }
                            } else {
                                this.damageContinuous(1.0F);
                                next.damageContinuous(1.0F);
                                if (Mathf.chanceDelta(0.1)) {
                                    Fx.fire.at(fx, fy);
                                }
                            }
                        }
                    }
                }

                return 0.0F;
            }
        }

        public float moveLiquidForward(CustomLiquid liquid) {
            Tile next = this.tile.nearby(this.rotation);
            if (next == null) {
                return 0.0F;
            } else if (next.build != null && next.build instanceof CustomBuilding nextCustom) {
                return this.moveLiquid(nextCustom, liquid);
            } else {
                return 0.0F;
            }
        }

        public void transferLiquid(CustomBuilding next, float amount, CustomLiquid liquid) {
            float flow = Math.min(((CustomBlock) next.block).customLiquidCapacity  - next.customLiquids.get(liquid), amount);
            if (next.acceptCustomLiquid(this, liquid)) {
                next.handeCustomLiquid(this, liquid, flow);
                this.customLiquids.remove(liquid, flow);
            }
        }

        public boolean canDumpLiquid(Building to, CustomLiquid liquid) {
            return true;
        }

        public CustomBuilding getLiquidDestination(Building from, CustomLiquid liquid) {
            return this;
        }

        public void consumeObject(Object item0, Object item1, int amount){
            if (item0 instanceof Item item) items.remove(item, amount);
            if (item1 instanceof Item item) items.remove(item, amount);
            if (item0 instanceof CustomItem item) customItems.remove(item, amount);
            if (item1 instanceof CustomItem item) customItems.remove(item, amount);
        }

        public void consumeObject(Object item0, Object item1, float amount){
            if (item0 instanceof Liquid item) liquids.remove(item, amount);
            if (item1 instanceof Liquid item) liquids.remove(item, amount);
            if (item0 instanceof CustomLiquid item) customLiquids.remove(item, amount);
            if (item1 instanceof CustomLiquid item) customLiquids.remove(item, amount);
        }

        public void consumeObject(Object item0, Object item1){
            consumeObject(item0, item1, 1);
        }

        public boolean canDump(Building to, CustomItem item) {
            return true;
        }

        public void handeCustomItem(Building source, CustomItem item) {
            customItems.add(item);
        }
        public boolean acceptCustomItem(Building source, CustomItem item) {
            return hasCustomItem && this.customItems.get(item) < this.getMaximumAccepted(item);
        }

        public void handeCustomLiquid(Building source, CustomLiquid liq, float amount) {
            customLiquids.add(liq, amount);
        }

        public boolean acceptCustomLiquid(Building source, CustomLiquid liquid) {
            return hasCustomLiquid;
        }

        public boolean isOutput(CustomBuilding source) {
            return false;
        }

        public Building atSide(int side, int i) {
            switch (side) {
                case 0: Tmp.p1.set(size, i); break;
                case 1: Tmp.p1.set(size - i - 1, size); break;
                case 2: Tmp.p1.set(-1, size - i - 1); break;
                case 3: Tmp.p1.set(i, -1); break;
            }
            Tmp.p1.rotate(rotation);
            return nearby(Tmp.p1.x, Tmp.p1.y);
        }

        public int getMaximumAccepted(CustomItem item) {
            return customItemCapacity;
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }
    }
}

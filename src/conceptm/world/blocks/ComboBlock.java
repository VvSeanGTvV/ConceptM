package conceptm.world.blocks;

import arc.Core;
import arc.util.Tmp;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import conceptm.world.modules.ComboItemModule;
import conceptm.world.type.ComboItem;

public class ComboBlock extends Block {
    public int comboCapacity = 1;

    public boolean hasCombo = true;

    public ComboBlock(String name) {
        super(name);
    }

    @Override
    public void setBars() {
        super.setBars();

        if (this.hasCombo && this.configurable) {
            this.addBar("combos", (ComboBuilding entity) -> new Bar(() -> Core.bundle.format("bar.combos", entity.combos.total()), () -> Pal.items, () -> (float)entity.combos.total() / (float)this.comboCapacity));
        }
    }

    public class ComboBuilding extends Building {
        public ComboItemModule combos;
        @Override
        public Building create(Block block, Team team) {

            if (hasCombo) {
                this.combos = new ComboItemModule();
            }

            return super.create(block, team);
        }

        public void outputCombo(ComboItem item) {
            int dump = this.cdump;

            for(int i = 0; i < this.proximity.size; ++i) {
                this.incrementDump(this.proximity.size);
                Building otherBuild = this.proximity.get((i + dump) % this.proximity.size);
                if (otherBuild instanceof ComboBuilding other) {
                    if (other.acceptCombo(this, item) && this.canDump(other, item)) {
                        other.handleCombo(this, item);
                    }
                }
            }
        }

        public boolean hasOutputs(ComboItem item){
            int dump = this.cdump;

            for(int i = 0; i < this.proximity.size; ++i) {
                this.incrementDump(this.proximity.size);
                Building otherBuild = this.proximity.get((i + dump) % this.proximity.size);
                if (otherBuild instanceof ComboBuilding other) {
                    if (other.acceptCombo(this, item) && this.canDump(other, item)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean outputCombo(Building next, ComboItem item) {
            if (next instanceof ComboBuilding comboBuilding) {
                if (comboBuilding.acceptCombo(this, item) && this.canDump(comboBuilding, item)) {
                    comboBuilding.handleCombo(this, item);
                    return true;
                }
            }

            return false;
        }

        public boolean canDump(Building to, ComboItem item) {
            return true;
        }

        public void handleCombo(Building source, ComboItem item) {
            combos.add(item);
        }
        public boolean acceptCombo(Building source, ComboItem item) {
            return false;
        }

        public boolean isOutput(ComboBuilding source) {
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

        public int getMaximumAccepted(ComboItem item) {
            return comboCapacity;
        }
    }
}

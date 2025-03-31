package world.blocks;

import arc.graphics.g2d.Draw;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import conceptm.ModTemplate;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;
import world.modules.ComboItemModule;
import world.type.*;

import static mindustry.Vars.content;

public class Combiner extends ComboBlock {
    public Combiner(String name) {
        super(name);

        update = true;
        solid = true;

        hasItems = true;
        configurable = true;
        saveConfig = false;


    }

    public class CombinerBuild extends ComboBuilding {
        public float progress;
        public float totalProgress;
        public float warmup;

        public Item select0, select1;
        public ComboItem output, select0c, select1c;

        public ComboItemStack s0c, s1c;

        @Override
        public void buildConfiguration(Table table) {
            table.table(t -> {
                if (output != null) {
                    t.add(output.name, Styles.defaultLabel).pad(4f);
                    t.row();
                    t.image(Styles.black8).fillX().height(3.0F).pad(3.0F).row();
                    t.row();

                    t.table(a -> {
                        if (output.item1 != null) a.image(output.item1.fullIcon).pad(4f);
                        else if (output.item1c != null) a.image(output.item1c.fullIcon).pad(4f);

                        a.add("+").pad(4f);
                        if (output.item2 != null) a.image(output.item2.fullIcon).pad(4f);
                        else if (output.item2c != null) a.image(output.item2c.fullIcon).pad(4f);

                        a.image(Icon.rightSmall).pad(4f);
                        if (output.item1 != null) a.image(output.item1.fullIcon).color(output.color).pad(4f);
                        else if (output.item1c != null) a.image(output.item1c.fullIcon).pad(4f);

                    }).growX().get().setFillParent(true);
                }
            }).growX().center().get().background(Styles.black8).setFillParent(true);
        }

        @Override
        public void updateTile() {
            if (select0 != null && select1 != null && items.has(select0) && items.has(select1)){
                ComboItem newCombo = new ComboItem(select0, select1);
                if (hasOutputs(newCombo)) {
                    items.remove(select0, 1);
                    items.remove(select1, 1);
                    outputCombo(newCombo);
                    output = newCombo;
                }
            }

            if (select0c != null && select1 != null && combos.has(select0c) && items.has(select1)){
                ComboItem newCombo = new ComboItem(select0c, select1);
                if (hasOutputs(newCombo)) {
                    combos.remove(select0c, 1);
                    items.remove(select1, 1);
                    outputCombo(newCombo);
                    output = newCombo;
                }
            }

            if (select0 != null && select1c != null && combos.has(select1c) && items.has(select0)){
                ComboItem newCombo = new ComboItem(select0, select1c);
                if (hasOutputs(newCombo)) {
                    items.remove(select0, 1);
                    combos.remove(select1c, 1);
                    outputCombo(newCombo);
                    output = newCombo;
                }
            }

            if (select0c != null && select1c != null && combos.has(select1c) && combos.has(select0c)){
                ComboItem newCombo = new ComboItem(select0c, select1c);
                if (hasOutputs(newCombo)) {
                    combos.remove(select0c, 1);
                    combos.remove(select1c, 1);
                    outputCombo(newCombo);
                    output = newCombo;
                }
            }

            if (items.any() && items != null) {
                for (Item item : content.items()) {
                    if (select0 == null && items.has(item) && item != select1) select0 = item;
                    if (select1 == null && items.has(item) && item != select0) select1 = item;
                }

                if ((select0 != null && !items.has(select0))) select0 = null;
                if ((select1 != null && !items.has(select1))) select1 = null;
            } else {
                select0 = select1 = null;
            }

            if (combos.any() && combos != null) {
                for (ComboItemStack itemc : combos.items) {
                    if (select0c == null && combos.has(itemc.item) && !itemc.equals(select1c)) select0c = itemc.item;
                    if (select1c == null && combos.has(itemc.item) && !itemc.equals(select0c)) select1c = itemc.item;
                }

                if ((select0c != null && !combos.has(select0c)) || select0 != null) select0c = null;
                if ((select1c != null && !combos.has(select1c)) || select1 != null) select1c = null;
            } else {
                select0c = select1c = null;
            }
        }

        @Override
        public boolean acceptCombo(Building source, ComboItem item) {
            return true;
        }

        @Override
        public void handleCombo(Building source, ComboItem item) {
            combos.add(item);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public boolean isOutput(ComboBuilding source) {
            return true;
        }

        @Override
        public void draw() {
            super.draw();

            if (select0 != null) Draw.rect(select0.fullIcon, x - 2 * size, y);
            if (select1 != null) Draw.rect(select1.fullIcon, x + 2 * size, y);

            if (select0c != null) select0c.draw(x - 2 * size, y, 6f);
            if (select1c != null) select1c.draw(x + 2 * size, y, 6f);
        }
    }
}

package world.blocks;

import arc.graphics.g2d.Draw;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import conceptm.ModTemplate;
import mindustry.Vars;
import mindustry.gen.*;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.Block;
import world.type.ComboItem;

public class Combiner extends Block {
    public Combiner(String name) {
        super(name);

        update = true;
        solid = true;

        hasItems = true;
        configurable = true;
        saveConfig = true;

        config(Item.class, (CombinerBuild build, Item value) -> {
            if (build.mode) {
                build.select1 = value;
            } else {
                build.select0 = value;
            }
            build.mode = !build.mode;
        });

        configClear((CombinerBuild build) -> {
            build.mode = false;
            build.select0 = build.select1 = null;
        });
    }

    public class CombinerBuild extends Building {
        public float progress;
        public float totalProgress;
        public float warmup;

        public Item select0, select1;
        public boolean mode;

        @Override
        public void buildConfiguration(Table table) {
            int i = 0;

            final ButtonGroup<Button> group = new ButtonGroup<>();
            group.setMinCheckCount(0);

            for (var item : Vars.content.items()){
                ImageButton button = table.button(new TextureRegionDrawable(item.fullIcon), Styles.cleari, () -> {}).size(40f).group(group).get();
                if (i % 4 == 3){
                    table.row();
                }
                button.changed(() -> configure(button.isChecked() ? item : null));
                button.update(() -> button.setChecked(select0 == item || select1 == item));
                i++;
            }
        }

        @Override
        public void updateTile() {
            if (select0 != null && select1 != null && items.has(select0) && items.has(select1)){
                items.remove(select0, 1);
                items.remove(select1, 1);
                offload(ModTemplate.findItem(select0, select1));
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return (select0 == item || select1 == item) && this.items.get(item) < this.getMaximumAccepted(item);
        }

        @Override
        public void draw() {
            super.draw();

            if (select0 != null) Draw.rect(select0.fullIcon, x - 2 * size, y);
            if (select1 != null) Draw.rect(select1.fullIcon, x + 2 * size, y);
        }
    }
}

package conceptm.ui.dialogs;

import arc.Core;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Scaling;
import conceptm.world.type.ComboItem;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.Stats;
import mindustry.ui.Styles;

import static arc.Core.keybinds;
import static arc.Core.settings;
import static mindustry.Vars.iconXLarge;
import static conceptm.ModTemplate.ui;

public class ModdedContentInfoDialog extends BaseDialog {

    public ModdedContentInfoDialog() {
        super("@info.title");

        addCloseButton();

        keyDown(key -> {
            if(key == keybinds.get(Binding.block_info).key){
                Core.app.post(this::hide);
            }
        });
    }

    public void showItem(ComboItem Item){
        cont.clear();

        Table table = new Table();
        table.margin(10);

        table.table(title1 -> {
            title1.image(Item.fullIcon).size(iconXLarge).scaling(Scaling.fit).color(Item.color);
            title1.add("[accent]" + Item.name + (settings.getBool("console") ? "\n[gray]" + Item.name : "")).padLeft(5);
        });

        table.row();

        table.table(a -> {
            if (Item.item1 != null) a.image(Item.item1.fullIcon).pad(4f);
            else if (Item.item1c != null) {
                a.image(Item.item1c.fullIcon).color(Item.item1c.color).pad(4f);
                a.button("?", Styles.flatBordert, () -> ui.content.showItem(Item.item1c)).size(40f).pad(10).right().grow();
            }
            else a.add("?").pad(4f);

            a.add("+").pad(4f);
            if (Item.item2 != null) a.image(Item.item2.fullIcon).pad(4f);
            else if (Item.item2c != null) {
                a.image(Item.item2c.fullIcon).color(Item.item2c.color).pad(4f);
                a.button("?", Styles.flatBordert, () -> ui.content.showItem(Item.item2c)).size(40f).pad(10).right().grow();
            }
            else a.add("?").pad(4f);

            a.image(Icon.rightSmall).pad(4f);
            a.image(Item.fullIcon).color(Item.color).pad(4f);
        });

        table.row();
        table.add("@category.general").fillX().color(Pal.accent);
        table.row();

        Stats stats = Item.stats;

        for(StatCat cat : stats.toMap().keys()){
            OrderedMap<Stat, Seq<StatValue>> map = stats.toMap().get(cat);

            if(map.size == 0) continue;

            if(stats.useCategories){
                table.add("@category." + cat.name).color(Pal.accent).fillX();
                table.row();
            }

            for(Stat stat : map.keys()){
                table.table(inset -> {
                    inset.left();
                    inset.add("[lightgray]" + stat.localized() + ":[] ").left().top();
                    Seq<StatValue> arr = map.get(stat);
                    for(StatValue value : arr){
                        value.display(inset);
                        inset.add().size(10f);
                    }

                }).fillX().padLeft(10);
                table.row();
            }
        }

        ScrollPane pane = new ScrollPane(table);
        cont.add(pane);

        show();
    }
}

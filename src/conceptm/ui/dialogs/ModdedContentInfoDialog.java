package conceptm.ui.dialogs;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Scaling;
import conceptm.world.type.*;
import mindustry.Vars;
import mindustry.gen.*;
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

    public void addStats(Table table, Stats stats){
        table.row();
        table.add("@category.general").fillX().color(Pal.accent);
        table.row();

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
    }

    public void showItem(CustomItem Item){
        cont.clear();

        Table table = new Table();
        table.margin(10);

        table.table(title1 -> {
            title1.image(Item.fullIcon).size(iconXLarge).scaling(Scaling.fit).color(Item.color);
            title1.add("[accent]" + Item.localizedName + (settings.getBool("console") ? "\n[gray]" + Item.name : "")).padLeft(5);
        });

        table.row();

        table.table(a -> {
            if (Item.item1 != null) {
                a.image(Item.item1.fullIcon).pad(4f);
                a.button("?", Styles.flatBordert, () -> Vars.ui.content.show(Item.item1)).size(40f).pad(10).right().grow();
            }
            else if (Item.item1c != null) {
                a.image(Item.item1c.fullIcon).color(Item.item1c.color).pad(4f);
                a.button("?", Styles.flatBordert, () -> ui.content.showItem(Item.item1c)).size(40f).pad(10).right().grow();
            }
            else a.add("?").pad(4f);

            a.add("+").pad(4f);
            if (Item.item2 != null) {
                a.image(Item.item2.fullIcon).pad(4f);
                a.button("?", Styles.flatBordert, () -> Vars.ui.content.show(Item.item2)).size(40f).pad(10).right().grow();
            }
            else if (Item.item2c != null) {
                a.image(Item.item2c.fullIcon).color(Item.item2c.color).pad(4f);
                a.button("?", Styles.flatBordert, () -> ui.content.showItem(Item.item2c)).size(40f).pad(10).right().grow();
            }
            else a.add("?").pad(4f);

            a.image(Icon.rightSmall).pad(4f);
            a.image(Item.fullIcon).color(Item.color).pad(4f);
        });
        addStats(table, Item.stats);

        ScrollPane pane = new ScrollPane(table);
        cont.add(pane);

        show();
    }

    public void showLiquid(CustomLiquid Liquid){
        cont.clear();

        Table table = new Table();
        table.margin(10);

        table.table(title1 -> {
            title1.image(Liquid.fullIcon).size(iconXLarge).scaling(Scaling.fit).color(Liquid.color);
            title1.add("[accent]" + Liquid.localizedName + (settings.getBool("console") ? "\n[gray]" + Liquid.name : "")).padLeft(5);
        });

        table.row();

        if (Liquid.item0 != null || Liquid.item0c != null) {
            int hardness = Liquid.item0 != null ? Liquid.item0.hardness : Liquid.item0c.hardness;
            table.table(a -> {
                if (Liquid.item0 != null) {
                    a.image(Liquid.item0.fullIcon).pad(4f);
                    a.button("?", Styles.flatBordert, () -> Vars.ui.content.show(Liquid.item0)).size(40f).pad(10).right().grow();
                } else if (Liquid.item0c != null) {
                    a.image(Liquid.item0c.fullIcon).color(Liquid.item0c.color).pad(4f);
                    a.button("?", Styles.flatBordert, () -> ui.content.showItem(Liquid.item0c)).size(40f).pad(10).right().grow();
                } else a.add("?").pad(4f);

                a.add("+").pad(4f);
                a.add("[red]" + Iconc.waves + "[] " + Math.floor(0.4f + hardness * 0.05f) + Core.bundle.get("unit.heatunits")).pad(4f);

                a.image(Icon.rightSmall).pad(4f);
                a.image(Liquid.fullIcon).color(Liquid.color).pad(4f);
            });
        } else {
            table.table(a -> {
                if (Liquid.liq1 != null) {
                    a.image(Liquid.liq1.fullIcon).pad(4f);
                    a.button("?", Styles.flatBordert, () -> Vars.ui.content.show(Liquid.liq1)).size(40f).pad(10).right().grow();
                } else if (Liquid.liq1c != null) {
                    a.image(Liquid.liq1c.fullIcon).color(Liquid.liq1c.color).pad(4f);
                    a.button("?", Styles.flatBordert, () -> ui.content.showLiquid(Liquid.liq1c)).size(40f).pad(10).right().grow();
                } else a.add("?").pad(4f);

                a.add("+").pad(4f);
                if (Liquid.liq2 != null) {
                    a.image(Liquid.liq2.fullIcon).pad(4f);
                    a.button("?", Styles.flatBordert, () -> Vars.ui.content.show(Liquid.liq2)).size(40f).pad(10).right().grow();
                } else if (Liquid.liq2c != null) {
                    a.image(Liquid.liq2c.fullIcon).color(Liquid.liq2c.color).pad(4f);
                    a.button("?", Styles.flatBordert, () -> ui.content.showLiquid(Liquid.liq2c)).size(40f).pad(10).right().grow();
                } else a.add("?").pad(4f);

                a.image(Icon.rightSmall).pad(4f);
                a.image(Liquid.fullIcon).color(Liquid.color).pad(4f);
            });
        }
        addStats(table, Liquid.stats);

        ScrollPane pane = new ScrollPane(table);
        cont.add(pane);

        show();
    }
}

package conceptm.world.type;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.type.Item;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import java.util.*;

import static conceptm.graphics.BlendColor.blendColorsVibrant;

public class CustomItem extends CustomUnlockable {
    public Color color;

    /** how explosive this item is. */
    public float explosiveness = 0f;
    /** flammability above 0.3 makes this eligible for item burners. */
    public float flammability = 0f;
    /** how radioactive this item is. */
    public float radioactivity;
    /** how electrically potent this item is. */
    public float charge = 0f;
    /** drill hardness of the item */
    public int hardness = 0;
    /**
     * base material cost of this item, used for calculating place times
     * 1 cost = 1 tick added to build time
     */
    public float cost = 1f;
    /** When this item is present in the build cost, a block's <b>default</b> health is multiplied by 1 + scaling, where 'scaling' is summed together for all item requirement types. */
    public float healthScaling = 0f;
    /** if true, this item is of the lowest priority to drills. */
    public boolean lowPriority;

    /** If true, this material is used by buildings. If false, this material will be incinerated in certain cores. */
    public boolean buildable = true;
    public boolean hidden = false;

    public Item item1, item2;
    public CustomItem item1c, item2c;
    public CustomLiquid liqc;
    public CustomItem(String name, Object a0, Object b0, boolean loading){
        var aHard = (a0 instanceof Item item) ? item.hardness : (a0 instanceof CustomItem comboItem) ? comboItem.hardness : 0;
        var bHard = (b0 instanceof Item item) ? item.hardness : (b0 instanceof CustomItem comboItem) ? comboItem.hardness : 0;
        var aflame = (a0 instanceof Item item) ? item.flammability : (a0 instanceof CustomItem comboItem) ? comboItem.flammability : 0;
        var bflame = (b0 instanceof Item item) ? item.flammability : (b0 instanceof CustomItem comboItem) ? comboItem.flammability : 0;
        var aradio = (a0 instanceof Item item) ? item.radioactivity : (a0 instanceof CustomItem comboItem) ? comboItem.radioactivity : 0;
        var bradio = (b0 instanceof Item item) ? item.radioactivity : (b0 instanceof CustomItem comboItem) ? comboItem.radioactivity : 0;
        var acharge = (a0 instanceof Item item) ? item.charge : (a0 instanceof CustomItem comboItem) ? comboItem.charge : 0;
        var bcharge = (b0 instanceof Item item) ? item.charge : (b0 instanceof CustomItem comboItem) ? comboItem.charge : 0;
        var aexplode = (a0 instanceof Item item) ? item.explosiveness : (a0 instanceof CustomItem comboItem) ? comboItem.explosiveness : 0;
        var bexplode = (b0 instanceof Item item) ? item.explosiveness : (b0 instanceof CustomItem comboItem) ? comboItem.explosiveness : 0;
        var acost = (a0 instanceof Item item) ? item.cost : (a0 instanceof CustomItem comboItem) ? comboItem.cost : 1f;
        var bcost = (b0 instanceof Item item) ? item.cost : (b0 instanceof CustomItem comboItem) ? comboItem.cost : 1f;
        var ascale = (a0 instanceof Item item) ? item.healthScaling : (a0 instanceof CustomItem comboItem) ? comboItem.healthScaling : 0;
        var bscale = (b0 instanceof Item item) ? item.healthScaling : (b0 instanceof CustomItem comboItem) ? comboItem.healthScaling : 0;

        // Combine numeric properties (average them)
        float div = 1.5f;
        this.hardness = (aHard + bHard);
        this.charge = (acharge + bcharge) / div;
        this.radioactivity = (aradio + bradio) / div;
        this.flammability = (aflame + bflame) / div;
        this.explosiveness = (aexplode + bexplode) / div;
        this.cost = (acost + bcost);
        this.healthScaling = (ascale + bscale);

        item1 = (a0 instanceof Item item) ? item : null;
        item2 = (b0 instanceof Item item) ? item : null;

        item1c = (a0 instanceof CustomItem item) ? item : null;
        item2c = (b0 instanceof CustomItem item) ? item : null;

        List<Boolean> boolList = Arrays.asList(
                item1 != null && item1.lowPriority,
                item2 != null && item2.lowPriority,
                item1c != null && item1c.lowPriority,
                item2c != null && item2c.lowPriority
                );
        this.lowPriority = boolList.stream().reduce(false, Boolean::logicalOr);

        boolList = Arrays.asList(
                item1 != null && item1.buildable,
                item2 != null && item2.buildable,
                item1c != null && item1c.buildable,
                item2c != null && item2c.buildable
        );
        this.buildable = boolList.stream().reduce(false, Boolean::logicalOr);

        this.color = blendColorsVibrant(
                (item1 != null) ? item1.color : (item1c != null) ? item1c.color : Color.lightGray.cpy(),
                (item2 != null) ? item2.color : (item2c != null) ? item2c.color : Color.lightGray.cpy()
        );



        String an = (a0 instanceof Item ai) ? ai.name : (a0 instanceof CustomItem ac) ? ac.name : "";
        String bn = (b0 instanceof Item bi) ? bi.name : (b0 instanceof CustomItem bc) ? bc.name : "";

        this.localizedName = (loading) ? name : nameRegistry.putNameFor(name, a0, b0);
        this.name = nameRegistry.generateRegistryKeyWithBracket(an, bn, "{", "}");
        createIcons(item1, item2, item1c, item2c);
        setStats();

        short i0d = (item1 != null) ? item1.id : (item1c != null) ? item1c.id : (short) 0;
        short i1d = (item2 != null) ? item2.id : (item2c != null) ? item2c.id : (short) 0;
        this.id = (short) (i0d + i1d);
    }

    public CustomItem(Object item1, Object item2) {
        this(
                nameRegistry.getNameFor(item1, item2, false),
                item1,
                item2,
                false
        );
    }

    public void setStats() {
        stats.addPercent(Stat.explosiveness, explosiveness);
        stats.addPercent(Stat.flammability, flammability);
        stats.addPercent(Stat.radioactivity, radioactivity);
        stats.addPercent(Stat.charge, charge);
    }

    public void draw(float x, float y, float size) {
        draw(x, y, size, color);
    }

    public void draw(float x, float y, float size, Color color){
        Draw.color(color);
        Draw.rect(fullIcon, x, y, size, size);
        Draw.color();
    }

    public void createIcons(Item item0, Item item1, CustomItem customItem0, CustomItem customItem1) {
        // Get the pixmaps for both items
        var icon1 = (item0 != null) ? item0.fullIcon : (customItem0 != null) ? customItem0.fullIcon : Core.atlas.find("white");
        var icon2 = (item1  != null) ? item1.fullIcon : (customItem1 != null) ? customItem1.fullIcon : Core.atlas.find("white");
        fullIcon = icon1;
    }
}

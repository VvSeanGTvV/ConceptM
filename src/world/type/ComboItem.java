package world.type;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.util.Log;
import mindustry.graphics.*;
import mindustry.type.Item;

public class ComboItem extends Item {
    public final Item item1;
    public final Item item2;

    public ComboItem(String name, Item item1, Item item2) {
        super(name, blendColors(item1.color, item2.color));

        this.item1 = item1;
        this.item2 = item2;

        // Combine numeric properties (average them)
        this.hardness = (item1.hardness + item2.hardness) / 2;
        this.charge = (item1.charge + item2.charge) / 2;
        this.radioactivity = (item1.radioactivity + item2.radioactivity) / 2;
        this.flammability = (item1.flammability + item2.flammability) / 2;
        this.explosiveness = (item1.explosiveness + item2.explosiveness) / 2;
        this.cost = (item1.cost + item2.cost) / 2;
        this.healthScaling = (item1.healthScaling + item2.healthScaling) / 2;

        // Combine boolean properties (OR operation)
        this.lowPriority = item1.lowPriority || item2.lowPriority;
        this.buildable = item1.buildable || item2.buildable;


    }

    @Override
    public void loadIcon() {
        super.loadIcon();

        fullIcon = uiIcon = Core.atlas.find(name + "-full");
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        var atlasA = Core.atlas.find(item1.name).asAtlas();
        var atlasB = Core.atlas.find(item2.name).asAtlas();

        if (atlasA != null && atlasB != null) {
            String regionName = item1.name + "-" + item2.name;
            Pixmap combined = Pixmaps.blend(Core.atlas.getPixmap(atlasA), Core.atlas.getPixmap(atlasB), 0.5f);

            packer.add(MultiPacker.PageType.main, regionName + "-full", combined);
            //Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(atlasA), outlineColor, outlineRadius);
        }
    }

    // Alternative constructor that generates a name automatically
    public ComboItem(Item item1, Item item2) {
        this(
                generateCombinedName(item1, item2),
                item1,
                item2
        );
    }

    private static String generateCombinedName(Item item1, Item item2) {
        // Simple name combination logic
        return item1.name + "-" + item2.name;
    }

    private static Color blendColors(Color c1, Color c2) {
        // Simple color blending - average RGBA values
        return new Color(
                (c1.r + c2.r) / 2f,
                (c1.g + c2.g) / 2f,
                (c1.b + c2.b) / 2f,
                (c1.a + c2.a) / 2f
        );
    }
}

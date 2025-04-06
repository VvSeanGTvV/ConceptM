package conceptm.world.type;

import arc.graphics.g2d.TextureRegion;
import conceptm.generator.NameRegistry;
import mindustry.world.meta.Stats;

public class CustomUnlockable {
    public String localizedName, name;
    public Stats stats = new Stats();
    public TextureRegion fullIcon;

    public static final NameRegistry nameRegistry = new NameRegistry();
}

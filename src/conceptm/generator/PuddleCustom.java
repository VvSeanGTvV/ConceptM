package conceptm.generator;

import arc.util.pooling.Pools;
import conceptm.entities.PuddlesCustom;
import conceptm.world.type.CustomLiquid;
import mindustry.entities.Puddles;
import mindustry.gen.Puddle;
import mindustry.type.Liquid;
import mindustry.world.Tile;

public class PuddleCustom extends Puddle {

    public CustomLiquid customLiquid;

    public static PuddleCustom create() {
        return (PuddleCustom) Pools.obtain(PuddleCustom.class, PuddleCustom::new);
    }

    @Override
    public void afterSync() {
        if (this.customLiquid != null) {
            PuddlesCustom.register(this);
        }
    }
}

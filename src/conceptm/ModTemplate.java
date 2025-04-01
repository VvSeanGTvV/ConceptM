package conceptm;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.Structs;
import mindustry.Vars;
import mindustry.graphics.MultiPacker;
import mindustry.graphics.MultiPacker.*;
import mindustry.mod.*;
import conceptm.gen.*;

import static mindustry.Vars.*;

public class ModTemplate extends Mod{

    public static String internalMod = "concept-m";
    public static Mods.LoadedMod mod;

    @Override
    public void init() {
        mod = mods.locateMod(internalMod);
    }

    @Override
    public void loadContent(){
        EntityRegistry.register();
        ConceptBlocks.load();
    }
}

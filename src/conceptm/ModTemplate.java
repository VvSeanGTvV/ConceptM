package conceptm;

import conceptm.content.ConceptBlocks;
import conceptm.core.exUI;
import mindustry.mod.*;
import conceptm.gen.*;

import static mindustry.Vars.*;

public class ModTemplate extends Mod{

    public static String internalMod = "concept-m";
    public static Mods.LoadedMod mod;

    public static exUI ui;

    @Override
    public void init() {
        mod = mods.locateMod(internalMod);
        ui = new exUI();

        ui.init();
    }

    @Override
    public void loadContent(){
        EntityRegistry.register();
        ConceptBlocks.load();
    }
}

package conceptm;

import arc.func.Func;
import conceptm.content.*;
import mindustry.mod.*;
import conceptm.gen.*;

import static arc.Core.bundle;

public class ConceptMod extends Mod{
    /** Mod's current Version **/
    public static String ModVersion = "1.0 ALPHA";
    /** Mod's current Build **/
    public static final String BuildVer = "1";
    public static String internalMod = "conceptm";
    @Override
    public void loadContent(){
        EntityRegistry.register();
        UnitTypes.load();
        Blocks.load();
    }

    public static Func<String, String> getModBundle = value -> bundle.get("mod." + value);

    public static Func<String, String> getStatBundle = value -> bundle.get("stat." + value);
}

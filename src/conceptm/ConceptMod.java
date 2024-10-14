package conceptm;

import conceptm.content.*;
import mindustry.mod.*;
import conceptm.gen.*;

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
}

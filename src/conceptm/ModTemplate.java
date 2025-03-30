package conceptm;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.mod.*;
import conceptm.gen.*;
import mindustry.type.Item;
import world.type.ComboItem;

public class ModTemplate extends Mod{

    public static Seq<ComboItem> combo = new Seq<>();

    public static ComboItem findItem(Item item0, Item item1){
        ComboItem r = null;
        for (var a : combo){
            if (a.item1 == item0 && a.item2 == item1) r = a;
        }
        return r;
    }

    @Override
    public void loadContent(){
        EntityRegistry.register();
        ConceptBlocks.load();

        for (var item0 : Vars.content.items()){
            int i = 0;
            for (var item1 : Vars.content.items()){
                if (canCombine(item0, item1) && i < 10){
                    combo.add(new ComboItem(item0, item1));
                    i++;
                } else {
                    break;
                }
            }
            if (i >= 10) break;
        }

    }

    // Example filter method
    boolean canCombine(Item a, Item b) {
        return !a.isHidden() && !b.isHidden() && a != b; // Example: don't combine two low priority items
    }
}

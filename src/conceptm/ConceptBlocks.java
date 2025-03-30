package conceptm;

import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.world.Block;
import world.blocks.Combiner;

import static mindustry.type.ItemStack.with;

public class ConceptBlocks {
    public static Block

    Combine;

    public static void load(){
        Combine = new Combiner("test-combine"){{
            requirements(Category.distribution, with(Items.copper, 1));
            size = 2;
        }};
    }
}

package conceptm.content;

import conceptm.world.blocks.Combiner;
import conceptm.world.blocks.ComboConveyor;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.world.Block;

import static mindustry.type.ItemStack.with;

public class ConceptBlocks {
    public static Block

    Combine, ComboConveyor;

    public static void load(){
        Combine = new Combiner("test-combine"){{
            requirements(Category.distribution, with(Items.copper, 1));
            size = 2;
        }};

        ComboConveyor = new ComboConveyor("shape-conveyor"){{
            requirements(Category.distribution, with(Items.copper, 1));
            size = 1;

            speed = 0.03f;
            displayedSpeed = 4.2f;
        }};
    }
}

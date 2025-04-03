package conceptm.content;

import conceptm.world.blocks.Combiner;
import conceptm.world.blocks.ComboConveyor;
import conceptm.world.blocks.power.ConsumeComboGenerator;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;

import static mindustry.type.ItemStack.with;

public class ConceptBlocks {
    public static Block

    Combine, ComboConveyor, ComboGen;

    public static void load(){
        Combine = new Combiner("test-combine"){{
            comboCapacity = 10;
            requirements(Category.distribution, with(Items.copper, 1));
            size = 2;
        }};

        ComboGen = new ConsumeComboGenerator("test-combine-gen"){{
            requirements(Category.distribution, with(Items.copper, 1));

            powerProduction = 1.25f;
            itemDuration = 120f;

            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.03f;
            generateEffect = Fx.generatespark;

            size = 1;
        }};

        ComboConveyor = new ComboConveyor("shape-conveyor"){{
            requirements(Category.distribution, with(Items.copper, 1));
            size = 1;

            speed = 0.03f;
            displayedSpeed = 4.2f;
        }};
    }
}

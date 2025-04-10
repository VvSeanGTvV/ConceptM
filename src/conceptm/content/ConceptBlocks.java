package conceptm.content;

import arc.graphics.Color;
import conceptm.world.blocks.Combiner;
import conceptm.world.blocks.CustomConveyor;
import conceptm.world.blocks.liquid.*;
import conceptm.world.blocks.power.ConsumeCustomGenerator;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawFlame;
import mindustry.world.draw.DrawLiquidTile;
import mindustry.world.draw.DrawMulti;

import static mindustry.type.ItemStack.with;

public class ConceptBlocks {
    public static Block

    Combine, ComboConveyor, ComboGen, Mixer, ComboConduit;

    public static void load(){
        Combine = new Combiner("compressor"){{
            customItemCapacity = 10;
            requirements(Category.distribution, with(Items.copper, 1));
            size = 3;
        }};

        Mixer = new Mixer("mixer"){{

            customLiquidCapacity = liquidCapacity = 50;
            requirements(Category.distribution, with(Items.copper, 1));
            size = 3;
        }};

        ComboConduit = new CustomConduit("test-conduit"){{
            requirements(Category.distribution, with(Items.copper, 1));
            size = 1;
        }};

        ComboGen = new ConsumeCustomGenerator("combustion-gen"){{
            requirements(Category.distribution, with(Items.copper, 1));

            powerProduction = 1.25f;
            itemDuration = 120f;

            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.03f;
            generateEffect = Fx.generatespark;

            size = 1;
        }};

        ComboConveyor = new CustomConveyor("shape-conveyor"){{
            requirements(Category.distribution, with(Items.copper, 1));
            size = 1;

            speed = 0.03f;
            displayedSpeed = 4.2f;
        }};
    }
}

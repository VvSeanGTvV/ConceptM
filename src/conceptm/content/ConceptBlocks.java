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
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawFlame;
import mindustry.world.draw.DrawLiquidTile;
import mindustry.world.draw.DrawMulti;

import static mindustry.type.ItemStack.with;

public class ConceptBlocks {
    public static Block

    combine, comboConveyor, comboGen, mixer, comboConduit, comboLiquidTank, crucible, unloaderLiquid;

    public static void load(){
        combine = new Combiner("compressor"){{
            customItemCapacity = 10;
            requirements(Category.distribution, with(Items.copper, 1));
            size = 3;
        }};

        unloaderLiquid = new UnloaderLiquid("unloader-liquid"){{
            requirements(Category.effect, ItemStack.with(Items.titanium, 25, Items.silicon, 30));
            speed = 7f;
        }};

        crucible = new Crucible("crucible"){{
            requirements(Category.crafting, with(Items.lead, 55, Items.titanium, 30));
            itemCapacity = customItemCapacity = 50;
            customLiquidCapacity = 50f;

            size = 3;
        }};

        mixer = new Mixer("mixer"){{

            customLiquidCapacity = liquidCapacity = 50;
            requirements(Category.distribution, with(Items.copper, 1));
            size = 3;
        }};

        comboLiquidTank = new CustomLiquidRouter("liquid-tank"){{

            customLiquidCapacity = liquidCapacity = 2700f;
            requirements(Category.distribution, with(Items.copper, 1));
            size = 3;
        }};

        comboConduit = new CustomConduit("test-conduit"){{
            requirements(Category.distribution, with(Items.copper, 1));
            size = 1;
        }};

        comboGen = new ConsumeCustomGenerator("combustion-gen"){{
            requirements(Category.distribution, with(Items.copper, 1));

            powerProduction = 1.25f;
            itemDuration = 120f;

            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.03f;
            generateEffect = Fx.generatespark;

            size = 1;
        }};

        comboConveyor = new CustomConveyor("shape-conveyor"){{
            requirements(Category.distribution, with(Items.copper, 1));
            size = 1;

            speed = 0.03f;
            displayedSpeed = 4.2f;
        }};
    }
}

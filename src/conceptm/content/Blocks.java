package conceptm.content;



import arc.Core;
import arc.graphics.g2d.TextureRegion;
import conceptm.world.blocks.campaign.NewAccelerator;
import conceptm.world.draw.DrawTurretAdvance;
import mindustry.content.Items;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class Blocks {
    public static Block
            // AEA Unlimited Turrets
            bunkerHeavyArtillary,

            // Pump

            // Campaign
            interplanetaryAccelerator;
            ;

    public static void load() {

        interplanetaryAccelerator = new NewAccelerator("interplanetary-accelerator"){{
            requirements(Category.effect, BuildVisibility.campaignOnly, with(Items.copper, 16000, Items.silicon, 11000, Items.thorium, 13000, Items.titanium, 12000, Items.surgeAlloy, 6000, Items.phaseFabric, 5000));
            researchCostMultiplier = 0.1f;
            size = 7;
            hasPower = true;
            consumePower(10f);
            buildCostMultiplier = 0.5f;
            scaledHealth = 80;
            squareSprite = false;
        }
            @Override
            public TextureRegion[] icons() {
                return new TextureRegion[]{region, Core.atlas.find(name + "-team-" + "sharded")};
            }
        };

        bunkerHeavyArtillary = new PowerTurret("bunker-turret") {
            final int turnSpeed = 2;
            final float turnAcceleration = 0.9f;

            {
                outlineIcon = false;
                requirements(Category.turret, with());
                targetAir = false;
                health = 5800;
                range = 280;
                consumePower(1f/60f);
                recoil = (float) 8 /2;
                reload = (float) 150 /2;
                rotateSpeed = turnSpeed*2/turnAcceleration;
                size = 3;
                shootSound = Sounds.mediumCannon;
                drawer = new DrawTurretAdvance("bunker-",0,5);
                shootType = new ArtilleryBulletType(6,0){{
                    lifetime = 17;
                    splashDamage = 200;
                    splashDamageRadius = 65;
                    collidesAir = false;
                    ammoMultiplier = 1f;
                    drawSize = 40*4;
                    hitSize = drawSize/10;

                    //for visual stats only.
                    //buildingDamageMultiplier = 0.25f;
                }};
            }
        };
    }
}

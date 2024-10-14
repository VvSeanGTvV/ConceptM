package conceptm.content;


import conceptm.world.blocks.DrawTurretAdvance;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;

import static mindustry.type.ItemStack.with;

public class Blocks {
    public static Block
            // AEA Unlimited Turrets
            bunkerHeavyArtillary

            ;

    public static void load() {
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

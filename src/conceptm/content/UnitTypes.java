package conceptm.content;

import arc.graphics.*;
import arc.math.geom.Rect;
import conceptm.entities.bullet.BallBulletType;
import conceptm.type.unit.MantisRayType;
import conceptm.world.draw.CircleForceDraw;
import ent.anno.Annotations.EntityDef;
import mindustry.content.Fx;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.part.RegionPart;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.type.unit.TankUnitType;

import static conceptm.ConceptMod.internalMod;

public class UnitTypes {
    // region standard

    // ocean
    public static @EntityDef({Unitc.class, Flyingc.class}) UnitType ckat;

    // tank
    public static @EntityDef({Unitc.class, Tankc.class}) UnitType mantel;

    public static void load(){
        ckat = new MantisRayType("ckat"){{
            constructor = UnitEntity::create;
            health = 1000f;
            hitSize = 20f;

            flying = true;
            canAttack = false;

            targetable = false;
            targetAir = targetGround = false;
            tailSpeed = -0.1f;
        }};

        mantel = new TankUnitType("mantel"){{
            constructor = TankUnit::create;
            hitSize = 44f;
            treadPullOffset = 1;
            speed = 0.48f;
            health = 20000;
            armor = 25f;
            crushDamage = 22f;
            rotateSpeed = 0.9f;
            float xo = 231f/2f, yo = 231f/2f;
            treadRects = new Rect[]{new Rect(27 - xo, 152 - yo, 56, 73), new Rect(24 - xo, 51 - yo, 29, 17), new Rect(59 - xo, 18 - yo, 39, 19)};

            //TODO maybe different shoot
            weapons.add(new Weapon(internalMod + "-mantel-weapon"){{
                shootSound = Sounds.largeCannon;
                layerOffset = 0.0001f;
                reload = 120f;
                shootY = (71f / 4f) - 2f;
                shake = 5f;
                recoil = 4f;
                rotate = true;
                rotateSpeed = 0.6f;
                mirror = false;
                x = 0f;
                shadow = 32f;
                y = -5f;
                heatColor = Color.valueOf("f9350f");
                cooldownTime = 80f;

                parts.addAll(
                        new RegionPart("-side"){{
                            outlineLayerOffset = 0f;
                            progress = PartProgress.heat;
                            mirror = true;
                            under = true;
                            moveY = -4.25f;
                            moveX = 2.25f;
                            moveRot = -10f;
                            x = 10.5f;
                            y = 8.85f;
                        }}
                );

                bullet = new BallBulletType(8f, 110){{
                    float orbRad = 7f, partRad = 3f;
                    int parts = 10;

                    orbRadius = orbRad;
                    particleSize = partRad;
                    particles = parts;

                    lifetime = 30f;
                    hitSize = 6f;
                    shootEffect = conceptm.content.Fx.shootMantel;
                    smokeEffect = conceptm.content.Fx.shootSmokeMantel;
                    pierceCap = 2;
                    pierce = true;
                    pierceBuilding = true;
                    //hitColor = backColor = trailColor = Color.valueOf("feb380");
                    particleColor = ballColor = hitColor = trailColor = Color.valueOf("feb380");
                    //frontColor = Color.white;
                    trailWidth = 3.1f;
                    trailLength = 8;
                    hitEffect = despawnEffect = Fx.blastExplosion;
                    hitSound = Sounds.explosion;

                    fragBullets = 8;
                    fragBullet = new FlakBulletType(5f, 15){{
                        width = 10f;
                        height = 12f;
                        shrinkX = shrinkY = 1f;
                        lifetime = 15f;
                        backColor = ballColor;
                        frontColor = ballColor;

                        splashDamage = 20f * 1.5f;
                        splashDamageRadius = 18f;
                        lightning = 2;
                        lightningLength = 7;
                    }};
                }};

                parts.addAll(
                        new CircleForceDraw(){{
                            float orbRad = 7f, partRad = 3f;
                            int parts = 10;

                            color = Color.valueOf("feb380");
                            particleColor = Color.valueOf("b17d59");

                            x = 8f;
                            under = true;

                            orbRadius = orbRad;
                            particleSize = partRad;
                            particles = parts;
                        }}
                );
            }});

            //TODO could change color when shooting
            parts.addAll(
                    new RegionPart("-glow"){{
                        color = Pal.turretHeat.cpy();
                        blending = Blending.additive;
                        layer = -1f;
                        outline = mirror = false;
                    }}
            );
        }};
    }
}

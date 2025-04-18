package conceptm.world.blocks.power;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.EnumSet;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import conceptm.world.blocks.CustomBlock;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class GeneratorCustom extends CustomBlock {
    /** The amount of power produced per tick in case of an efficiency of 1.0, which represents 100%. */
    public float powerProduction;
    public Stat generationType = Stat.basePowerGeneration;
    public DrawBlock drawer = new DrawDefault();

    public int explosionRadius = 12;
    public int explosionDamage = 0;
    public Effect explodeEffect = Fx.none;
    public Sound explodeSound = Sounds.none;

    public int explosionPuddles = 10;
    public float explosionPuddleRange = tilesize * 2f;
    public float explosionPuddleAmount = 100f;
    public @Nullable Liquid explosionPuddleLiquid;
    public float explosionMinWarmup = 0f;

    public float explosionShake = 0f, explosionShakeDuration = 6f;

    public GeneratorCustom(String name) {
        super(name);

        sync = true;
        baseExplosiveness = 5f;
        flags = EnumSet.of(BlockFlag.generator);

        consumesPower = false;
        outputsPower = true;

        update = true;
        solid = true;
        hasPower = true;
        group = BlockGroup.power;
    }

    public float getDisplayedPowerProduction(){
        return powerProduction;
    }

    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
    }


    @Override
    public void setBars() {
        super.setBars();

        if (hasPower && outputsPower) {
            addBar("power", (GeneratorComboBuild entity) -> new Bar(() ->
                    Core.bundle.format("bar.poweroutput",
                            Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
                    () -> Pal.powerBar,
                    () -> entity.productionEfficiency));
        }

    }

    public class GeneratorComboBuild extends CustomBuilding {
        public float generateTime;
        /** The efficiency of the producer. An efficiency of 1.0 means 100% */
        public float productionEfficiency = 0.0f;

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public float warmup(){
            return enabled ? productionEfficiency : 0f;
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();

            if(Vars.state.rules.reactorExplosions){
                createExplosion();
            }
        }

        public boolean shouldExplode(){
            return warmup() >= explosionMinWarmup;
        }

        public void createExplosion(){
            if(shouldExplode()){
                if(explosionDamage > 0){
                    Damage.damage(x, y, explosionRadius * tilesize, explosionDamage);
                }

                explodeEffect.at(this);
                explodeSound.at(this);

                if(explosionPuddleLiquid != null){
                    for(int i = 0; i < explosionPuddles; i++){
                        Tmp.v1.trns(Mathf.random(360f), Mathf.random(explosionPuddleRange));
                        Tile tile = world.tileWorld(x + Tmp.v1.x, y + Tmp.v1.y);
                        Puddles.deposit(tile, explosionPuddleLiquid, explosionPuddleAmount);
                    }
                }

                if(explosionShake > 0){
                    Effect.shake(explosionShake, explosionShakeDuration, this);
                }
            }
        }

        @Override
        public void drawLight(){
            super.drawLight();
            drawer.drawLight(this);
        }

        @Override
        public float ambientVolume(){
            return Mathf.clamp(productionEfficiency);
        }

        @Override
        public float getPowerProduction(){
            return enabled ? powerProduction * productionEfficiency : 0f;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(productionEfficiency);
            write.f(generateTime);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            productionEfficiency = read.f();
            if(revision >= 1){
                generateTime = read.f();
            }
        }
    }
}

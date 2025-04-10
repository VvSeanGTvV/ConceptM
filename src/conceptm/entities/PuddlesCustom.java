package conceptm.entities;

import arc.math.Mathf;
import arc.struct.IntMap;
import arc.util.Time;
import conceptm.generator.PuddleCustom;
import conceptm.world.type.CustomLiquid;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.Puddle;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.meta.Env;

import static mindustry.entities.Puddles.maxLiquid;

public class PuddlesCustom {

    private static final IntMap<PuddleCustom> map = new IntMap<>();

    public static void deposit(Tile tile, Tile source, CustomLiquid liquid, float amount){
        deposit(tile, source, liquid, amount, true);
    }

    /** Deposits a Puddle at a tile. */
    public static void deposit(Tile tile, CustomLiquid liquid, float amount){
        deposit(tile, tile, liquid, amount, true);
    }

    /** Returns the Puddle on the specified tile. May return null. */
    public static PuddleCustom get(Tile tile){
        return map.get(tile.pos());
    }

    public static void deposit(Tile tile, Tile source, CustomLiquid liquid, float amount, boolean initial){
        deposit(tile, source, liquid, amount, initial, false);
    }

    public static void deposit(Tile tile, Tile source, CustomLiquid liquid, float amount, boolean initial, boolean cap){
        if(tile == null) return;

        float ax = (tile.worldx() + source.worldx()) / 2f, ay = (tile.worldy() + source.worldy()) / 2f;

        if(liquid.willBoil()){
            if(Mathf.chanceDelta(0.16f)){
                liquid.vaporEffect.at(ax, ay, liquid.gasColor);
            }
            return;
        }

        if(Vars.state.rules.hasEnv(Env.space)){
            if(Mathf.chanceDelta(0.11f) && tile != source){
                Bullets.spaceLiquid.create(null, source.team(), ax, ay, source.angleTo(tile) + Mathf.range(50f), -1f, Mathf.random(0f, 0.2f), Mathf.random(0.6f, 1f), liquid);
            }
            return;
        }

        if(tile.floor().isLiquid){
            reactPuddle(tile.floor().liquidDrop, liquid, amount, tile, ax, ay);

            Puddle p = map.get(tile.pos());

            if(initial && p != null && p.lastRipple <= Time.time - 40f){
                Fx.ripple.at(ax, ay, 1f, tile.floor().liquidDrop.color);
                p.lastRipple = Time.time;
            }
            return;
        }

        if(tile.floor().solid) return;

        PuddleCustom p = map.get(tile.pos());
        if(p == null || p.liquid == null){
            if(!Vars.net.client()){
                //do not create puddles clientside as that destroys syncing
                PuddleCustom puddle = PuddleCustom.create();
                puddle.tile = tile;
                puddle.customLiquid = liquid;
                puddle.amount = amount;
                puddle.set(ax, ay);
                map.put(tile.pos(), puddle);
                puddle.add();
            }
        }else if(p.customLiquid == liquid){
            p.accepting = Math.max(amount, p.accepting);

            if(initial && p.lastRipple <= Time.time - 40f && p.amount >= maxLiquid / 2f){
                Fx.ripple.at(ax, ay, 1f, p.liquid.color);
                p.lastRipple = Time.time;
            }
        }else{
            float added = reactPuddle(p.customLiquid, liquid, amount, p.tile, (p.x + source.worldx())/2f, (p.y + source.worldy())/2f);

            if(cap){
                added = Mathf.clamp(maxLiquid - p.amount, 0f, added);
            }

            p.amount += added;
        }
    }

    public static void remove(Tile tile){
        if(tile == null) return;

        map.remove(tile.pos());
    }

    public static void register(PuddleCustom puddle){
        map.put(puddle.tile().pos(), puddle);
    }

    private static float reactPuddle(Object dest, Object liquid, float amount, Tile tile, float x, float y){

        var flamed = (dest instanceof Liquid liq) ? liq.flammability : (dest instanceof CustomLiquid liqc) ? liqc.flammability : 0f;
        var flamef = (liquid instanceof Liquid liq) ? liq.flammability : (liquid instanceof CustomLiquid liqc) ? liqc.flammability : 0f;

        var temd = (dest instanceof Liquid liq) ? liq.temperature : (dest instanceof CustomLiquid liqc) ? liqc.temperature : 0f;
        var temf = (liquid instanceof Liquid liq) ? liq.temperature : (liquid instanceof CustomLiquid liqc) ? liqc.temperature : 0f;
        if(dest == null) return 0f;

        if((flamed > 0.3f && temf > 0.7f) ||
                (flamef > 0.3f && temd > 0.7f)){ //flammable liquid + hot liquid
            Fires.create(tile);
            if(Mathf.chance(0.006 * amount)){
                Bullets.fireball.createNet(Team.derelict, x, y, Mathf.random(360f), -1f, 1f, 1f);
            }
        }else if(temd > 0.7f && temf < 0.55f){ //cold liquid poured onto hot Puddle
            if(Mathf.chance(0.5f * amount)){
                Fx.steam.at(x, y);
            }
            return -0.1f * amount;
        }else if(temf > 0.7f && temd < 0.55f){ //hot liquid poured onto cold Puddle
            if(Mathf.chance(0.8f * amount)){
                Fx.steam.at(x, y);
            }
            return -0.4f * amount;
        }
        return 0f;
    }

    /**
     * Returns whether the first liquid can 'stay' on the second one.
     */
    private static boolean canStayOn(CustomLiquid liquid, CustomLiquid other){
        return liquid.canStayOn.contains(other);
    }
}

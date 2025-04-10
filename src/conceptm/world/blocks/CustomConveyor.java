package conceptm.world.blocks;

import arc.Core;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.meta.*;
import conceptm.world.type.CustomItem;

import static mindustry.Vars.tilesize;

public class CustomConveyor extends CustomBlock implements Autotiler{
    private static final float itemSpace = 0.5f;
    private static final int capacity = 1;

    public TextureRegion[][] regions;

    public float speed = 0f;
    public float displayedSpeed = 0f;

    public @Nullable Block junctionReplacement, bridgeReplacement;

    public CustomConveyor(String name){
        super(name);
        group = BlockGroup.transportation;
        priority = TargetPriority.transport;
        conveyorPlacement = true;
        underBullets = true;

        ambientSound = Sounds.conveyor;
        ambientSoundVolume = 0.0022f;
        unloadable = false;
        noUpdateDisabled = false;

        update = true;
        solid = true;
        rotate = true;
    }

    @Override
    public void load() {
        super.load();
        regions = new TextureRegion[2][4];
        for (int i = 0; i < regions.length; i++)
            for (int j = 0; j < regions[i].length; j++)
                regions[i][j] = Core.atlas.find(name + "-" + i + "-" + j);
        region = fullIcon = uiIcon = regions[0][0];
    }

    @Override
    public void setStats(){
        super.setStats();

        //have to add a custom calculated speed, since the actual movement speed is apparently not linear
        stats.add(Stat.itemsMoved, displayedSpeed, StatUnit.itemsSecond);
    }

    @Override
    public void init(){
        super.init();

        if(junctionReplacement == null) junctionReplacement = Blocks.junction;
        if(bridgeReplacement == null || !(bridgeReplacement instanceof ItemBridge)) bridgeReplacement = Blocks.itemBridge;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        int[] bits = getTiling(plan, list);

        if(bits == null) return;

        TextureRegion region = regions[0][0];
        Draw.rect(region, plan.drawx(), plan.drawy(), region.width * bits[1] * region.scl(), region.height * bits[2] * region.scl(), plan.rotation * 90);
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        return (otherblock.outputsItems() || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasItems))
                && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
    }

    //stack conveyors should be bridged over, not replaced
    @Override
    public boolean canReplace(Block other){
        return super.canReplace(other) && !(other instanceof StackConveyor);
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        if(bridgeReplacement == null) return;

        Placement.calculateBridges(plans, (ItemBridge)bridgeReplacement);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{regions[0][0]};
    }

    @Override
    public boolean isAccessible(){
        return true;
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof mindustry.world.blocks.distribution.Conveyor || req.block instanceof Junction));
        return cont.get(Geometry.d4(req.rotation)) &&
                cont.get(Geometry.d4(req.rotation - 2)) &&
                req.tile() != null &&
                req.tile().block() instanceof mindustry.world.blocks.distribution.Conveyor &&
                Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    public class ComboConveyorBuild extends CustomBuilding implements ChainedBuilding{
        //parallel array data
        public CustomItem[] ids = new CustomItem[capacity];
        public float[] xs = new float[capacity], ys = new float[capacity];
        //amount of items, always < capacity
        public int len = 0;
        //next entity
        public @Nullable Building next, last;
        public @Nullable ComboConveyorBuild nextc;
        //whether the next conveyor's rotation == tile rotation
        public boolean aligned;

        public int lastInserted, mid;
        public float minitem = 1;

        public int blendbits, blending;
        public int blendsclx = 1, blendscly = 1;

        public float clogHeat = 0f;

        @Override
        public void draw(){
            int frame = enabled && clogHeat <= 0.5f ? (int)(((Time.time * speed * 8f * timeScale * efficiency)) % 4) : 0;

            //draw extra conveyors facing this one for non-square tiling purposes
            Draw.z(Layer.blockUnder);
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = rotation - i;
                    float rot = i == 0 ? rotation * 90 : (dir)*90;

                    Draw.rect(sliced(regions[0][frame], i != 0 ? SliceMode.bottom : SliceMode.top), x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, rot);
                }
            }

            Draw.z(Layer.block - 0.2f);

            Draw.rect(regions[blendbits][frame], x, y, tilesize * blendsclx, tilesize * blendscly, rotation * 90);

            Draw.z(Layer.block - 0.1f);

            for(int i = 0; i < len; i++){
                CustomItem item = ids[i];
                Tmp.v1.trns(rotation * 90, tilesize, 0);
                Tmp.v2.trns(rotation * 90, -tilesize / 2f, xs[i] * tilesize / 2f);

                item.draw(x + Tmp.v1.x * ys[i] + Tmp.v2.x, y + Tmp.v1.y * ys[i] + Tmp.v2.y, 5f);
            }
        }

        @Override
        public void payloadDraw(){
            Draw.rect(block.fullIcon,x, y);
        }

        @Override
        public void drawCracks(){
            Draw.z(Layer.block - 0.15f);
            super.drawCracks();
        }

        @Override
        public void overwrote(Seq<Building> builds){
            if(builds.first() instanceof ComboConveyorBuild build){
                ids = build.ids.clone();
                xs = build.xs.clone();
                ys = build.ys.clone();
                len = build.len;
                clogHeat = build.clogHeat;
                lastInserted = build.lastInserted;
                mid = build.mid;
                minitem = build.minitem;
            }
        }

        @Override
        public boolean shouldAmbientSound(){
            return clogHeat <= 0.5f;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            next = front();
            last = null;
            boolean connectBack = back() instanceof CustomBuilding && ((CustomBuilding) back()).isOutput(this);
            boolean connectLeft = left() instanceof CustomBuilding && ((CustomBuilding) left()).isOutput(this);
            boolean connectRight = right() instanceof CustomBuilding && ((CustomBuilding) right()).isOutput(this);
            if (connectBack) last = back();
            else if (connectLeft && !connectRight) last = left();
            else if (!connectLeft && connectRight) last = right();
            nextc = next instanceof ComboConveyorBuild && next.team == team ? (ComboConveyorBuild)next : null;
            aligned = nextc != null && rotation == next.rotation;

            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = last == null || last == back() ? 0 : 1;
            blendsclx = 1;
            blendscly = last == null || (last.rotation + 1) % 4 == rotation ? 1 : -1;
            blending = bits[4];
        }

        @Override
        public void unitOn(Unit unit){

            if(clogHeat > 0.5f) return;

            noSleep();

            float mspeed = speed * tilesize * 55f;
            float centerSpeed = 0.1f;
            float centerDstScl = 3f;
            float tx = Geometry.d4x(rotation), ty = Geometry.d4y(rotation);

            float centerx = 0f, centery = 0f;

            if(Math.abs(tx) > Math.abs(ty)){
                centery = Mathf.clamp((y - unit.y()) / centerDstScl, -centerSpeed, centerSpeed);
                if(Math.abs(y - unit.y()) < 1f) centery = 0f;
            }else{
                centerx = Mathf.clamp((x - unit.x()) / centerDstScl, -centerSpeed, centerSpeed);
                if(Math.abs(x - unit.x()) < 1f) centerx = 0f;
            }

            if(len * itemSpace < 0.9f){
                unit.impulse((tx * mspeed + centerx) * delta(), (ty * mspeed + centery) * delta());
            }
        }

        @Override
        public void updateTile(){
            minitem = 1f;
            mid = 0;

            //skip updates if possible

            if(len == 0){
                clogHeat = 0f;
                sleep();
                return;
            }
            float nextMax = aligned ? 1f - Math.max(itemSpace - nextc.minitem, 0) : 1f;
            float moved = speed * edelta();

            for(int i = len - 1; i >= 0; i--){
                float nextpos = (i == len - 1 ? 100f : ys[i + 1]) - itemSpace;
                float maxmove = Mathf.clamp(nextpos - ys[i], 0, moved);

                ys[i] += maxmove;

                if(ys[i] > nextMax) ys[i] = nextMax;
                if(ys[i] > 0.5 && i > 0) mid = i - 1;
                xs[i] = Mathf.approach(xs[i], 0, moved*2);

                if(ys[i] >= 1f && pass(ids[i])){
                    //align X position if passing forwards
                    if(aligned){
                        nextc.xs[nextc.lastInserted] = xs[i];
                    }
                    //remove last item
                    len = Math.min(i, len);
                }else if(ys[i] < minitem){
                    minitem = ys[i];
                }
            }

            if(minitem < itemSpace + (blendbits == 1 ? 0.3f : 0f)){
                clogHeat = Mathf.approachDelta(clogHeat, 1f, 1f / 60f);
            }else{
                clogHeat = 0f;
            }

            noSleep();
        }

        public boolean pass(CustomItem item){
            return item != null && next != null && next.team == team && outputCombo(next, item);
        }

        @Override
        public void getStackOffset(Item item, Vec2 trns){
            trns.trns(rotdeg() + 180f, tilesize / 2f);
        }

        @Override
        public boolean acceptCustomItem(Building source, CustomItem item) {
            if(len >= capacity) return false;
            return source == last && minitem >= itemSpace && !(source.block.rotate && next == source);
        }

        @Override
        public void handeCustomItem(Building source, CustomItem item) {
            if(len >= capacity) return;
            noSleep();
            float x = source == right() ? -1 : 1;
            if (source == right() || source == left()) {
                add(mid);
                xs[mid] = x;
                ys[mid] = 0.5F;
                ids[mid] = item;
            } else if (source == back()) {
                add(0);
                xs[0] = 0;
                ys[0] = 0;
                ids[0] = item;
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(len);

            for(int i = 0; i < len; i++){
                write.str(ids[i].item1.name);
                write.str(ids[i].item2.name);
                write.f(xs[i]);
                write.f(ys[i]);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int amount = read.i();
            len = Math.min(amount, capacity);

            for(int i = 0; i < amount; i++){
                String uitem0 = read.str();
                String uitem1 = read.str();
                float x = read.f();
                float y = read.f();
                if(i < capacity){
                    ids[i] = new CustomItem(Vars.content.item(uitem0), Vars.content.item(uitem1));
                    xs[i] = x;
                    ys[i] = y;
                }
            }

            //this updates some state
            updateTile();
        }

        @Override
        public Object senseObject(LAccess sensor){
            if(sensor == LAccess.firstItem && len > 0) return ids[len - 1];
            return super.senseObject(sensor);
        }

        public final void add(int o){
            for(int i = Math.min(o + 1, len); i > o; i--){
                ids[i] = ids[i - 1];
                xs[i] = xs[i - 1];
                ys[i] = ys[i - 1];
            }

            len++;
        }

        public final void remove(int o){
            for(int i = o; i < len - 1; i++){
                ids[i] = ids[i + 1];
                xs[i] = xs[i + 1];
                ys[i] = ys[i + 1];
            }

            len--;
        }

        @Override
        public boolean isOutput(CustomBuilding source) {
            return atSide(0, 0) == source;
        }

        @Nullable
        @Override
        public Building next(){
            return nextc;
        }
    }
}

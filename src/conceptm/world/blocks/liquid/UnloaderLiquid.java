package conceptm.world.blocks.liquid;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Comparator;

import static mindustry.Vars.content;

public class UnloaderLiquid extends LiquidBlock {
    //public @Load(value = "@-center", fallback = "unloader-center") TextureRegion centerRegion;

    public float speed = 1f;

    /** Cached result of content.items() */
    static Liquid[] allLiquids;

    public UnloaderLiquid(String name) {
        super(name);
        update = true;
        solid = true;
        health = 70;
        hasItems = false;
        hasLiquids = true;
        configurable = true;
        saveConfig = true;
        itemCapacity = 0;
        noUpdateDisabled = true;
        clearOnDoubleTap = true;
        unloadable = false;

        //config(Item.class, (UnloaderBuild tile, Item item) -> tile.sortItem = item);
        //configClear((UnloaderBuild tile) -> tile.sortItem = null);
    }

    @Override
    public void init(){
        super.init();

        allLiquids = content.liquids().toArray(Liquid.class);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.speed, 60f / speed, StatUnit.liquidSecond);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquids");
    }

    public static class ContainerStat implements Pool.Poolable {
        Building building;
        float loadFactor;
        boolean canLoad;
        boolean canUnload;
        /** Cached !(building instanceof StorageBuild) */
        boolean notStorage;
        int lastUsed;

        @Override
        public void reset(){
            building = null;
        }
    }

    public class UnloaderLiquidBuild extends LiquidBuild {
        public float unloadTimer = 0f;
        public int rotations = 0;
        public Liquid sortItem = null;
        public ContainerStat dumpingFrom, dumpingTo;
        public final Seq<ContainerStat> possibleBlocks = new Seq<>(ContainerStat.class);

        protected final Comparator<ContainerStat> comparator = (x, y) -> {
            //sort so it gives priority for blocks that can only either receive or give (not both), and then by load, and then by last use
            //highest = unload from, lowest = unload to
            int unloadPriority = Boolean.compare(x.canUnload && !x.canLoad, y.canUnload && !y.canLoad); //priority to receive if it cannot give
            if(unloadPriority != 0) return unloadPriority;
            int loadPriority = Boolean.compare(x.canUnload || !x.canLoad, y.canUnload || !y.canLoad); //priority to give if it cannot receive
            if(loadPriority != 0) return loadPriority;
            int loadFactor = Float.compare(x.loadFactor, y.loadFactor);
            if(loadFactor != 0) return loadFactor;
            return Integer.compare(y.lastUsed, x.lastUsed); //inverted
        };

        private boolean isPossibleItem(Liquid item){
            boolean hasProvider = false,
                    hasReceiver = false,
                    isDistinct = false;

            var pbi = possibleBlocks.items;
            for(int i = 0, l = possibleBlocks.size; i < l; i++){
                var pb = pbi[i];
                var other = pb.building;

                //set the stats of buildings in possibleBlocks while we are at it
                pb.canLoad = pb.notStorage && other.acceptLiquid(this, item);
                pb.canUnload = other.canUnload() && other.items != null && (other.liquids.get(item) > 0.001f);

                //thats also handling framerate issues and slow conveyor belts, to avoid skipping items if nulloader
                isDistinct |= (hasProvider && pb.canLoad) || (hasReceiver && pb.canUnload);
                hasProvider |= pb.canUnload;
                hasReceiver |= pb.canLoad;
            }
            return isDistinct;
        }

        @Override
        public void onProximityUpdate(){
            //filter all blocks in the proximity that will never be able to trade items

            super.onProximityUpdate();
            Pools.freeAll(possibleBlocks, true);
            possibleBlocks.clear();

            for(int i = 0; i < proximity.size; i++){
                var other = proximity.get(i);
                if(!other.interactable(team)) continue; //avoid blocks of the wrong team

                //partial check
                boolean canLoad = !(other.block instanceof StorageBlock);
                boolean canUnload = other.canUnload() && other.items != null;

                if(canLoad || canUnload){ //avoid blocks that can neither give nor receive items
                    var pb = Pools.obtain(ContainerStat.class, ContainerStat::new);
                    pb.building = other;
                    pb.notStorage = canLoad;
                    //TODO store the partial canLoad/canUnload?
                    possibleBlocks.add(pb);
                }
            }
        }

        @Override
        public void updateTile(){
            if(((unloadTimer += delta()) < speed) || (possibleBlocks.size < 2)) return;
            Liquid item = null;
            boolean any = false;

            if(sortItem != null){
                if(isPossibleItem(sortItem)) item = sortItem;
            }else{
                //selects the next item for nulloaders
                //inspired of nextIndex() but for all "proximity" (possibleBlocks) at once, and also way more powerful
                for(int i = 0, l = allLiquids.length; i < l; i++){
                    int id = (rotations + i + 1) % l;
                    var possibleItem = allLiquids[id];

                    if(isPossibleItem(possibleItem)){
                        item = possibleItem;
                        break;
                    }
                }
            }

            if(item != null){
                rotations = item.id; //next rotation for nulloaders //TODO maybe if(sortItem == null)
                var pbi = possibleBlocks.items;
                int pbs = possibleBlocks.size;

                for(int i = 0; i < pbs; i++){
                    var pb = pbi[i];
                    var other = pb.building;
                    float maxAccepted = other.block.liquidCapacity;
                    pb.loadFactor = maxAccepted == 0 || other.liquids == null ? 0 : other.liquids.get(item) / maxAccepted;
                    pb.lastUsed = (pb.lastUsed + 1) % Integer.MAX_VALUE; //increment the priority if not used
                }

                possibleBlocks.sort(comparator);

                dumpingTo = null;
                dumpingFrom = null;

                //choose the building to accept the item
                for(int i = 0; i < pbs; i++){
                    if(pbi[i].canLoad){
                        dumpingTo = pbi[i];
                        break;
                    }
                }

                //choose the building to take the item from
                for(int i = pbs - 1; i >= 0; i--){
                    if(pbi[i].canUnload){
                        dumpingFrom = pbi[i];
                        break;
                    }
                }

                //trade the items
                if(dumpingFrom != null && dumpingTo != null && (dumpingFrom.loadFactor != dumpingTo.loadFactor || !dumpingFrom.canLoad)){
                    dumpingTo.building.handleLiquid(this, item, 1);
                    dumpingFrom.building.liquids.remove(item, 1);
                    dumpingTo.lastUsed = 0;
                    dumpingFrom.lastUsed = 0;
                    any = true;
                }
            }

            if(any){
                unloadTimer %= speed;
            }else{
                unloadTimer = Math.min(unloadTimer, speed);
            }
        }

        @Override
        public void draw(){
            Draw.rect(bottomRegion, x, y);
            if (sortItem != null) drawTiledFrames(size, x, y, 0f, sortItem, 1f);
            Draw.rect(region, x, y);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            drawItemSelection(sortItem);
        }

        @Override
        public void buildConfiguration(Table table){
            ItemSelection.buildTable(UnloaderLiquid.this, table, content.liquids(), () -> sortItem, this::configure, selectionRows, selectionColumns);
        }

        @Override
        public Liquid config(){
            return sortItem;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(sortItem == null ? -1 : sortItem.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int id = revision == 1 ? read.s() : read.b();
            sortItem = id == -1 ? null : content.liquid(id);
        }
    }
}

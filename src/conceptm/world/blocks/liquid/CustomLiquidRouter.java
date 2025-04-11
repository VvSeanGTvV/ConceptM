package conceptm.world.blocks.liquid;

import arc.graphics.g2d.Draw;
import conceptm.world.type.CustomLiquid;
import mindustry.gen.Building;

public class CustomLiquidRouter extends CustomLiquidBlock{
    public float liquidPadding = 0f;
    public CustomLiquidRouter(String name) {
        super(name);
    }

    public class CustomLiquidRouterBuild extends CustomLiquidBuild{
        @Override
        public void updateTile(){
            dumpLiquid(customLiquids.current());
        }

        @Override
        public void draw(){
            Draw.rect(bottomRegion, x, y);

            if(customLiquids.currentAmount() > 0.001f){
                drawTiledFrames(size, x, y, liquidPadding, customLiquids.current(), customLiquids.currentAmount() / customLiquidCapacity);
            }

            Draw.rect(region, x, y);
        }

        @Override
        public boolean acceptCustomLiquid(Building source, CustomLiquid liquid) {
            return (Objects.equals(liquid.name, customLiquids.current().name) || customLiquids.currentAmount() < 0.2f);
        }
    }
}

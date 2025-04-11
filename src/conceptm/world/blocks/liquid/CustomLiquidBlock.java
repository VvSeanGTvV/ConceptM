package conceptm.world.blocks.liquid;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.util.Tmp;
import conceptm.world.blocks.CustomBlock;
import conceptm.world.type.CustomLiquid;
import mindustry.graphics.Drawf;
import mindustry.type.Liquid;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class CustomLiquidBlock extends CustomBlock {
    public TextureRegion liquidRegion;
    public TextureRegion topRegion;
    public TextureRegion bottomRegion;

    public CustomLiquidBlock(String name) {
        super(name);

        update = true;
        solid = true;

        hasCustomLiquid = true;
        hasLiquids = false;


        hasCustomItem = hasItems = false;
        group = BlockGroup.liquids;
        outputsLiquid = true;
        envEnabled |= Env.space | Env.underwater;
    }

    @Override
    public void load() {
        super.load();

        liquidRegion = Core.atlas.find(name + "-liquid");
        topRegion = Core.atlas.find(name + "-top");
        bottomRegion = Core.atlas.find(name + "-bottom");
    }

    public static void drawTiledFrames(int size, float x, float y, float padding, Object liquid, float alpha){
        drawTiledFrames(size, x, y, padding, padding, padding, padding, liquid, alpha);
    }

    public static void drawTiledFrames(int size, float x, float y, float padLeft, float padRight, float padTop, float padBottom, Object liquid, float alpha){
        boolean isGas = (liquid instanceof Liquid liq) ? liq.gas : liquid instanceof CustomLiquid liqC && liqC.gas;
        Color colorGas = (liquid instanceof Liquid liq) ? liq.color : (liquid instanceof CustomLiquid liqC) ? liqC.color : Color.white.cpy();
        int animationFrame = (liquid instanceof Liquid liq) ? liq.getAnimationFrame() : (liquid instanceof CustomLiquid liqC) ? liqC.getAnimationFrame() : 0;

        TextureRegion region = renderer.fluidFrames[isGas ? 1 : 0][animationFrame];
        TextureRegion toDraw = Tmp.tr1;

        float leftBounds = size/2f * tilesize - padRight;
        float bottomBounds = size/2f * tilesize - padTop;
        Color color = Tmp.c1.set(colorGas).a(1f);

        for(int sx = 0; sx < size; sx++){
            for(int sy = 0; sy < size; sy++){
                float relx = sx - (size-1)/2f, rely = sy - (size-1)/2f;

                toDraw.set(region);

                //truncate region if at border
                float rightBorder = relx*tilesize + padLeft, topBorder = rely*tilesize + padBottom;
                float squishX = rightBorder + tilesize/2f - leftBounds, squishY = topBorder + tilesize/2f - bottomBounds;
                float ox = 0f, oy = 0f;

                if(squishX >= 8 || squishY >= 8) continue;

                //cut out the parts that don't fit inside the padding
                if(squishX > 0){
                    toDraw.setWidth(toDraw.width - squishX * 4f);
                    ox = -squishX/2f;
                }

                if(squishY > 0){
                    toDraw.setY(toDraw.getY() + squishY * 4f);
                    oy = -squishY/2f;
                }

                Drawf.liquid(toDraw, x + rightBorder + ox, y + topBorder + oy, alpha, color);
            }
        }
    }

    public class CustomLiquidBuild extends CustomBuilding{
        @Override
        public void draw(){
            float rotation = rotate ? rotdeg() : 0;
            Draw.rect(bottomRegion, x, y, rotation);

            if(liquids.currentAmount() > 0.001f){
                Drawf.liquid(liquidRegion, x, y, liquids.currentAmount() / liquidCapacity, liquids.current().color);
            }

            Draw.rect(topRegion, x, y, rotation);
        }
    }
}

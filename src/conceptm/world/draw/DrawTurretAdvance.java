package conceptm.world.draw;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.draw.*;

public class DrawTurretAdvance extends DrawTurret {
    /** Draw X Offset for Turret's barrel **/
    float DrawOffsetX;
    /** Draw Y Offset for Turret's barrel **/
    float DrawOffsetY;
    /** Use different variant base while keeping at the same size **/
    boolean multipleVariant = false;
    /** Different variant while in same size, enable the MultipleVariant first! **/
    int variant = 1;

    public DrawTurretAdvance(String basePrefix){
        this.basePrefix = basePrefix;
    }

    public DrawTurretAdvance(float xOffset, float yOffset){
        this.DrawOffsetX = xOffset;
        this.DrawOffsetY = yOffset;
    }

    public DrawTurretAdvance(String basePrefix, float xOffset, float yOffset){
        this.basePrefix = basePrefix;
        this.DrawOffsetX = xOffset;
        this.DrawOffsetY = yOffset;
    }

    public DrawTurretAdvance(String basePrefix, float xOffset, float yOffset, boolean multiVariant, int variant){
        this.basePrefix = basePrefix;
        this.DrawOffsetX = xOffset;
        this.DrawOffsetY = yOffset;
        this.variant = variant;
        this.multipleVariant = multiVariant;
    }

    public DrawTurretAdvance(){}

    @Override
    public void draw(Building build){
        Turret turret = (Turret)build.block;
        TurretBuild tb = (TurretBuild)build;

        Draw.rect(base, build.x, build.y);
        Draw.color();

        Draw.z(Layer.turret - 0.5f);

        Drawf.shadow(preview, build.x + Angles.trnsx(build.drawrot(), DrawOffsetX, DrawOffsetY) + tb.recoilOffset.x - turret.elevation, build.y + Angles.trnsy(build.drawrot(), DrawOffsetX, DrawOffsetY) + tb.recoilOffset.y - turret.elevation, tb.drawrot());

        Draw.z(Layer.turret);

        drawTurret(turret, tb);
        drawHeat(turret, tb);

        if(parts.size > 0){
            if(outline.found()){
                //draw outline under everything when parts are involved
                Draw.z(Layer.turret - 0.01f);
                Draw.rect(outline, build.x + Angles.trnsx(build.drawrot(), DrawOffsetX, DrawOffsetY) + tb.recoilOffset.x, build.y + Angles.trnsy(build.drawrot(), DrawOffsetX, DrawOffsetY) + tb.recoilOffset.y, tb.drawrot());
                Draw.z(Layer.turret);
            }

            float progress = tb.progress();

            //TODO no smooth reload
            var params = DrawPart.params.set(build.warmup(), 1f - progress, 1f - progress, tb.heat, tb.curRecoil, tb.charge, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation);

            for(var part : parts){
                part.draw(params);
            }
        }
    }

    @Override
    public void drawTurret(Turret block, TurretBuild build){
        if(block.region.found()){
            Draw.rect(block.region, build.x + Angles.trnsx(build.drawrot(), DrawOffsetX, DrawOffsetY) + build.recoilOffset.x, build.y + Angles.trnsy(build.drawrot(), DrawOffsetX, DrawOffsetY) + build.recoilOffset.y, build.drawrot());
        }

        if(liquid.found()){
            Liquid toDraw = liquidDraw == null ? build.liquids.current() : liquidDraw;
            Drawf.liquid(liquid, build.x + Angles.trnsx(build.drawrot(), DrawOffsetX, DrawOffsetY) + build.recoilOffset.x, build.y + Angles.trnsy(build.drawrot(), DrawOffsetX, DrawOffsetY) + build.recoilOffset.y, build.liquids.get(toDraw) / block.liquidCapacity, toDraw.color.write(Tmp.c1).a(1f), build.drawrot());
        }

        if(top.found()){
            Draw.rect(top, build.x, build.y);
        }
    }

    @Override
    public void load(Block block){
        if(!(block instanceof Turret)) throw new ClassCastException("This drawer can only be used on turrets.");

        preview = Core.atlas.find(block.name + "-preview", block.region);
        outline = Core.atlas.find(block.name + "-outline");
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
        heat = Core.atlas.find(block.name + "-heat");
        base = Core.atlas.find(block.name + "-base");

        for(var part : parts){
            part.turretShading = true;
            part.load(block.name);
        }

        //TODO test this for mods, e.g. exotic
        if(variant<0) throw new NumberFormatException("Variant numbers cannot go below 0.");
        if(!base.found() && block.minfo.mod != null) base = Core.atlas.find(block.minfo.mod.name + "-" + basePrefix + "block-" + block.size);
        if(!base.found()){
            if(!multipleVariant) {
                base = Core.atlas.find(basePrefix + "block-" + block.size);
            } else {
                base = Core.atlas.find(basePrefix + "block-" + variant + "-" + block.size);
            }
        }
    }
}

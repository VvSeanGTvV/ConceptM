package conceptm.world.modules;

import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.io.*;
import conceptm.core.io.DecoderString;
import conceptm.world.type.*;
import mindustry.Vars;
import mindustry.type.Liquid;

import java.util.Objects;

public class CustomLiquidModule {
    public Seq<CustomLiquidStack> liquids = new Seq<>();

    public CustomLiquid current = new CustomLiquid(Vars.content.liquid(0), Vars.content.liquid(1));

    public float get(CustomLiquid liq){
        var c = liquids.find(comboLiqs -> Objects.equals(comboLiqs.liq.name, liq.name));
        if (c == null) return 0;
        return c.amount;
    }

    public boolean any(){
        return liquids.any();
    }

    public boolean has(CustomLiquid liq){
        return get(liq) > 0;
    }

    public CustomLiquid current(){
        return current;
    }

    public float currentAmount(){
        return (current != null) ? get(current) : 0f;
    }

    public boolean has(CustomLiquid liq, int amount){
        return get(liq) >= amount;
    }

    public boolean empty(){
        return liquids.isEmpty();
    }

    public void add(CustomLiquid liq, float amount){
        current = liq;
        CustomLiquidStack last = liquids.find(comboLiqs -> Objects.equals(comboLiqs.liq.name, liq.name));
        if (last != null) last.set(last.liq, last.amount + amount);
        else liquids.add(new CustomLiquidStack(liq, amount));
    }

    public void remove(CustomLiquid liq, float amount){
        CustomLiquidStack last = liquids.find(comboLiqs -> Objects.equals(comboLiqs.liq.name, liq.name));
        if (last != null) {
            last.set(last.liq, last.amount - amount);
            if (last.amount <= 0) liquids.remove(last);
        }
    }

    public void write(Writes write){
        write.i(liquids.size);
        for (var liqStack : liquids){
            write.str(liqStack.liq.name);
            write.f(liqStack.amount);
        }
    }

    public void read(Reads read, byte revision){
        int size = read.i();
        for (int i=0; i<size; i++){
            String item = read.str();
            int amount = read.i();
            CustomLiquid loaded = DecoderString.loadLiquid(DecoderString.decodeString(item));
            if (loaded != null) add(loaded, amount);
        }
    }
}

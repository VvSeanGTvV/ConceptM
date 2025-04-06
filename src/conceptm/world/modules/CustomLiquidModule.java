package conceptm.world.modules;

import arc.struct.Seq;
import conceptm.world.type.*;

import java.util.Objects;

public class CustomLiquidModule {
    public Seq<CustomLiquidStack> liquids = new Seq<>();
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

    public boolean has(CustomLiquid liq, int amount){
        return get(liq) >= amount;
    }

    public boolean empty(){
        return liquids.isEmpty();
    }

    public void add(CustomLiquid liq){
        add(liq, 1);
    }

    public void add(CustomLiquid liq, int amount){
        CustomLiquidStack last = liquids.find(comboLiqs -> Objects.equals(comboLiqs.liq.name, liq.name));
        if (last != null) last.set(last.liq, last.amount + amount);
        else liquids.add(new CustomLiquidStack(liq, amount));
    }

    public void remove(CustomLiquid liq, int amount){
        CustomLiquidStack last = liquids.find(comboLiqs -> Objects.equals(comboLiqs.liq.name, liq.name));
        if (last != null) {
            last.set(last.liq, last.amount - amount);
            if (last.amount <= 0) liquids.remove(last);
        }
    }
}

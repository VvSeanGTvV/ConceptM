package world.modules;

import arc.struct.Seq;
import world.blocks.Combiner;
import world.type.*;

public class ComboItemModule {
    public Seq<ComboItemStack> items = new Seq<>();

    public int get(ComboItem item){
        var c = items.find(comboItems -> comboItems.item == item);
        if (c == null) return 0;
        return c.amount;
    }

    public ComboItem getItem(int index){
        return items.get(index).item;
    }

    public boolean any(){
        return items.any();
    }

    public boolean has(ComboItem item){
        return get(item) > 0;
    }

    public boolean has(ComboItem item, int amount){
        return get(item) >= amount;
    }

    public boolean empty(){
        return items.isEmpty();
    }

    public void add(ComboItem item){
        ComboItemStack last = items.find(comboItems -> comboItems.item == item);
        if (last != null) last.set(last.item, last.amount++); else items.add(new ComboItemStack(item, 1));
    }

    public void remove(ComboItem item, int amount){
        ComboItemStack last = items.find(comboItems -> comboItems.item == item);
        if (last != null) {
            last.set(last.item, last.amount - amount);
            if (last.amount <= 0) items.remove(last);
        }
    }
}

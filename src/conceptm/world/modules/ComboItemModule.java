package conceptm.world.modules;

import arc.struct.Seq;
import conceptm.world.type.*;

import java.util.Objects;

public class ComboItemModule {
    public Seq<ComboItemStack> items = new Seq<>();
    public int total = 0;

    public int get(ComboItem item){
        var c = items.find(comboItems -> Objects.equals(comboItems.item.name, item.name));
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
        add(item, 1);
    }

    public void add(ComboItem item, int amount){
        ComboItemStack last = items.find(comboItems -> Objects.equals(comboItems.item.name, item.name));
        if (last != null) last.set(last.item, last.amount + amount);
        else items.add(new ComboItemStack(item, amount));
        total += amount;
    }

    public void remove(ComboItem item, int amount){
        ComboItemStack last = items.find(comboItems -> Objects.equals(comboItems.item.name, item.name));
        if (last != null) {
            last.set(last.item, last.amount - amount);
            if (last.amount <= 0) items.remove(last);
            total -= amount;
            if (total < 0) total = 0;
        }
    }

    public int total() {
        return total;
    }
}

package conceptm.world.modules;

import arc.struct.Seq;
import conceptm.world.type.*;

import java.util.Objects;

public class CustomItemModule {
    public Seq<CustomItemStack> items = new Seq<>();
    public int total = 0;

    public int get(CustomItem item){
        var c = items.find(comboItems -> Objects.equals(comboItems.item.name, item.name));
        if (c == null) return 0;
        return c.amount;
    }

    public CustomItem getItem(int index){
        return items.get(index).item;
    }

    public boolean any(){
        return items.any();
    }

    public boolean has(CustomItem item){
        return get(item) > 0;
    }

    public boolean has(CustomItem item, int amount){
        return get(item) >= amount;
    }

    public boolean empty(){
        return items.isEmpty();
    }

    public void add(CustomItem item){
        add(item, 1);
    }

    public void add(CustomItem item, int amount){
        CustomItemStack last = items.find(comboItems -> Objects.equals(comboItems.item.name, item.name));
        if (last != null) last.set(last.item, last.amount + amount);
        else items.add(new CustomItemStack(item, amount));
        total += amount;
    }

    public void remove(CustomItem item, int amount){
        CustomItemStack last = items.find(comboItems -> Objects.equals(comboItems.item.name, item.name));
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

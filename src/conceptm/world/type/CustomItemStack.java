package conceptm.world.type;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Items;

public class CustomItemStack {
    public static final CustomItemStack[] empty = {};

    public CustomItem item;
    public int amount = 0;

    public CustomItemStack(CustomItem item, int amount){
        if(item == null) item = new CustomItem(Items.copper, Items.lead);
        this.item = item;
        this.amount = amount;
    }

    //serialization only
    public CustomItemStack(){
        //prevent nulls. +default
        item = new CustomItem(Items.copper, Items.lead);
    }

    public CustomItemStack set(CustomItem item, int amount){
        this.item = item;
        this.amount = amount;
        return this;
    }

    public CustomItemStack copy(){
        return new CustomItemStack(item, amount);
    }

    public boolean equals(CustomItemStack other){
        return other != null && other.item == item && other.amount == amount;
    }

    public boolean equals(CustomItem other){
        return other != null && other == item;
    }

    public static CustomItemStack[] mult(CustomItemStack[] stacks, float amount){
        var copy = new CustomItemStack[stacks.length];
        for(int i = 0; i < copy.length; i++){
            copy[i] = new CustomItemStack(stacks[i].item, Mathf.round(stacks[i].amount * amount));
        }
        return copy;
    }

    public static CustomItemStack[] with(Object... items){
        var stacks = new CustomItemStack[items.length / 2];
        for(int i = 0; i < items.length; i += 2){
            stacks[i / 2] = new CustomItemStack((CustomItem) items[i], ((Number)items[i + 1]).intValue());
        }
        return stacks;
    }

    public static Seq<CustomItemStack> list(Object... items){
        Seq<CustomItemStack> stacks = new Seq<>(items.length / 2);
        for(int i = 0; i < items.length; i += 2){
            stacks.add(new CustomItemStack((CustomItem) items[i], ((Number)items[i + 1]).intValue()));
        }
        return stacks;
    }

    public static CustomItemStack[] copy(CustomItemStack[] stacks){
        var out = new CustomItemStack[stacks.length];
        for(int i = 0; i < out.length; i++){
            out[i] = stacks[i].copy();
        }
        return out;
    }
}

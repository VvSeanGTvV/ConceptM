package conceptm.world.type;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Items;

public class ComboItemStack{
    public static final ComboItemStack[] empty = {};

    public ComboItem item;
    public int amount = 0;

    public ComboItemStack(ComboItem item, int amount){
        if(item == null) item = new ComboItem(Items.copper, Items.lead);
        this.item = item;
        this.amount = amount;
    }

    //serialization only
    public ComboItemStack(){
        //prevent nulls. +default
        item = new ComboItem(Items.copper, Items.lead);
    }

    public ComboItemStack set(ComboItem item, int amount){
        this.item = item;
        this.amount = amount;
        return this;
    }

    public ComboItemStack copy(){
        return new ComboItemStack(item, amount);
    }

    public boolean equals(ComboItemStack other){
        return other != null && other.item == item && other.amount == amount;
    }

    public boolean equals(ComboItem other){
        return other != null && other == item;
    }

    public static ComboItemStack[] mult(ComboItemStack[] stacks, float amount){
        var copy = new ComboItemStack[stacks.length];
        for(int i = 0; i < copy.length; i++){
            copy[i] = new ComboItemStack(stacks[i].item, Mathf.round(stacks[i].amount * amount));
        }
        return copy;
    }

    public static ComboItemStack[] with(Object... items){
        var stacks = new ComboItemStack[items.length / 2];
        for(int i = 0; i < items.length; i += 2){
            stacks[i / 2] = new ComboItemStack((ComboItem) items[i], ((Number)items[i + 1]).intValue());
        }
        return stacks;
    }

    public static Seq<ComboItemStack> list(Object... items){
        Seq<ComboItemStack> stacks = new Seq<>(items.length / 2);
        for(int i = 0; i < items.length; i += 2){
            stacks.add(new ComboItemStack((ComboItem) items[i], ((Number)items[i + 1]).intValue()));
        }
        return stacks;
    }

    public static ComboItemStack[] copy(ComboItemStack[] stacks){
        var out = new ComboItemStack[stacks.length];
        for(int i = 0; i < out.length; i++){
            out[i] = stacks[i].copy();
        }
        return out;
    }
}

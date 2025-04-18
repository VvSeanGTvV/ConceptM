package conceptm.core.io;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Log;
import conceptm.world.type.*;
import mindustry.Vars;
import mindustry.type.*;

import java.util.Objects;

public class DecoderString {
    static Seq<String> decode = new Seq<>(32);

    static void addDecode(String str0, String str1, Seq<String> decodeArea){
        if (!(Objects.equals(str0, "") || Objects.equals(str1, ""))) {
            decodeArea.add(str0 + "+" + str1);
        } else {
            if (!Objects.equals(str0, "")) decodeArea.add(str0);
            if (!Objects.equals(str1, "")) decodeArea.add(str1);
        }
    }

    /**
     * unpack the string data to sequenced string used to load the items entirely and/or liquid
     * @param data
     * @return Seq(String) Data
     */
    public static Seq<String> decodeString(String data){
        if (data.length() <= 0) return null;
        Seq<String> decodeTodo = new Seq<>(data.length());
        boolean sw = false, after = false, hasData;
        StringBuilder item0 = new StringBuilder(data.length()), item1 = new StringBuilder(data.length());
        for(int i=0; i<data.length(); i++){
            var letter = data.charAt(i);
            if (!(letter == '{' || letter == '}' || letter == '+')) {
                if (sw) {
                    item1.append(letter);
                } else {
                    item0.append(letter);
                }
            }
            if (letter == '+') {
                sw = true;
                after = true;
            }
            if (letter == '{' || letter == '}') {
                sw = false;
                if (after || letter == '}') {
                    hasData = (Objects.equals(item0.toString(), "") || Objects.equals(item1.toString(), ""));
                    addDecode(item0.toString(), item1.toString(), decodeTodo);
                    item0.delete(0, item0.length());
                    item1.delete(0, item1.length());
                    if (letter == '}' && hasData) decodeTodo.add("seperate");
                    after = false;
                }
            }
        }
        return decodeTodo;
    }

    /**
     * loads from sequenced data to custom combined liquid
     * @param list
     * @return CustomLiquid
     */
    public static CustomLiquid loadLiquid(Seq<String> list){
        if (list == null) return null;
        int indiviual = 0;
        boolean beforeCombo = false, finishedItem = false;
        Seq<Liquid> individualItem = new Seq<>(list.size);
        CustomLiquid baseItem = null;
        TextureRegion baseTexture = null;
        for (String item : list){
            boolean hasCombo = (item.indexOf('+') != -1);
            if (Vars.content.liquid(item) != null) {
                if (!hasCombo) {
                    individualItem.add(Vars.content.liquid(item));
                    indiviual++;
                } else {
                    individualItem.add(Vars.content.liquid(item));
                }
            }
            if (hasCombo && indiviual > 0) beforeCombo = true;
            if (hasCombo) {
                boolean sw = false;
                StringBuilder item0 = new StringBuilder(item.length());
                StringBuilder item1 = new StringBuilder(item.length());
                for (int i=0; i<item.length(); i++){
                    if (item.charAt(i) != '+') {
                        if (sw) {
                            item1.append(item.charAt(i));
                        } else {
                            item0.append(item.charAt(i));
                        }
                    } else {
                        sw = true;
                    }
                }
                baseItem = new CustomLiquid(Vars.content.liquid(item0.toString()), Vars.content.liquid(item1.toString()));
                baseTexture = (baseItem.gas) ? Core.atlas.find("concept-m" + "-gas-template") : Core.atlas.find("concept-m" + "-liquid-template");
            }
            if (baseItem != null && item.equals("seperate") && !finishedItem) {
                if (beforeCombo) {
                    var rev = individualItem.reverse();
                    for (var item1 : rev) {
                        baseItem = new CustomLiquid(item1, baseItem);
                    }
                } else {
                    for (var item1 : individualItem) {
                        baseItem = new CustomLiquid(baseItem, item1);
                    }
                }
                indiviual = 0;
                finishedItem = true;
                if (baseTexture != null) baseItem.fullIcon = baseTexture;
            }
        }
        return baseItem;
    }

    /**
     * loads from sequenced data to custom combined item
     * @param list
     * @return CustomLiquid
     */
    public static CustomItem loadItem(Seq<String> list){
        if (list == null) return null;
        int indiviual = 0;
        boolean beforeCombo = false, finishedItem = false;
        Seq<Item> individualItem = new Seq<>(list.size);
        CustomItem baseItem = null;
        TextureRegion baseTexture = null;
        for (String item : list){
            boolean hasCombo = (item.indexOf('+') != -1);
            if (Vars.content.item(item) != null) {
                if (!hasCombo) {
                    individualItem.add(Vars.content.item(item));
                    indiviual++;
                } else {
                    individualItem.add(Vars.content.item(item));
                }
            }
            if (hasCombo && indiviual > 0) beforeCombo = true;
            if (hasCombo) {
                boolean sw = false;
                StringBuilder item0 = new StringBuilder(item.length());
                StringBuilder item1 = new StringBuilder(item.length());
                for (int i=0; i<item.length(); i++){
                    if (item.charAt(i) != '+') {
                        if (sw) {
                            item1.append(item.charAt(i));
                        } else {
                            item0.append(item.charAt(i));
                        }
                    } else {
                        sw = true;
                    }
                }
                baseItem = new CustomItem(Vars.content.item(item0.toString()), Vars.content.item(item1.toString()));
                baseTexture = Vars.content.item(item0.toString()).fullIcon;
            }
            if (baseItem != null && item.equals("seperate") && !finishedItem) {
                if (beforeCombo) {
                    var rev = individualItem.reverse();
                    for (var item1 : rev) {
                        baseItem = new CustomItem(baseItem, item1);
                    }
                } else {
                    for (var item1 : individualItem) {
                        baseItem = new CustomItem(item1, baseItem);
                    }
                }
                indiviual = 0;
                finishedItem = true;
                if (baseTexture != null) baseItem.fullIcon = baseTexture;
            }
        }
        return baseItem;
    }
}

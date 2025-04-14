package conceptm.core.io;

import arc.struct.Seq;
import conceptm.world.type.CustomItem;
import mindustry.Vars;
import mindustry.type.Item;

import java.util.Objects;

public class DecoderString {
    static Seq<String> decode = new Seq<>(32);

    static void addDecode(String str0, String str1){
        if (!(Objects.equals(str0, "") || Objects.equals(str1, ""))) {
            decode.add(str0 + "+" + str1);
        } else {
            if (!Objects.equals(str0, "")) decode.add(str0);
            if (!Objects.equals(str1, "")) decode.add(str1);
        }
    }

    public static Seq<String> decodeString(String data){
        decode.clear();
        boolean sw = false, after = false, hasData = false;
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
                    addDecode(item0.toString(), item1.toString());
                    item0.delete(0, item0.length());
                    item1.delete(0, item1.length());
                    if (letter == '}' && hasData) decode.add("seperate");
                    after = false;
                }
            }
        }
        return decode;
    }

    public static void loadItem(Seq<String> list){
        int indiviual = 0;
        boolean beforeCombo = false;
        Seq<Item> individualItem = new Seq<>(list.size);
        for (String item : list){
            boolean hasCombo = (item.indexOf('s') != -1);
            if (!hasCombo) {
                individualItem.add(Vars.content.item(item));
                indiviual++;
            }
            if (hasCombo && indiviual > 0) beforeCombo = true;
            if (hasCombo) {
                boolean sw = false;
                for (int i=0; i<item.length(); i++){
                    if (sw) {

                    }
                }
            }
        }
    }
}

package conceptm.generator;

import conceptm.world.type.*;
import mindustry.type.*;

import java.util.*;

public class NameRegistry {
    private final Map<String, String> generatedNames = new HashMap<>();

    public String getNameFor(Object a, Object b, boolean isLiquid) {
        var an = (a instanceof Item i0) ? i0.localizedName : (a instanceof CustomItem c0) ? c0.localizedName : (a instanceof Liquid i0) ? i0.localizedName : (a instanceof CustomLiquid c0) ? c0.localizedName : "null";
        var bn = (b instanceof Item i1) ? i1.localizedName : (b instanceof CustomItem c1) ? c1.localizedName : (b instanceof Liquid i1) ? i1.localizedName : (b instanceof CustomLiquid c1) ? c1.localizedName : "null";
        String key = generateRegistryKey(an, bn);

        return generatedNames.computeIfAbsent(key, k -> DynamicNameGenerator.generateName(a, b));
    }

    public String putNameFor(String registry, Object a, Object b){
        var an = (a instanceof Item i0) ? i0.localizedName : (a instanceof CustomItem c0) ? c0.localizedName : (a instanceof Liquid i0) ? i0.localizedName : (a instanceof CustomLiquid c0) ? c0.localizedName : "null";
        var bn = (b instanceof Item i1) ? i1.localizedName : (b instanceof CustomItem c1) ? c1.localizedName : (b instanceof Liquid i1) ? i1.localizedName : (b instanceof CustomLiquid c1) ? c1.localizedName : "null";
        String key = generateRegistryKey(an, bn);

        return generatedNames.computeIfAbsent(key, k -> registry);
    }

    public String generateRegistryKey(String a, String b) {
        // Ensure consistent key regardless of order
        return a.compareTo(b) < 0 ?
                a + "+" + b :
                b + "+" + a;
    }

    public String generateRegistryKeyWithBracket(String a, String b, String open, String close){
        return open + generateRegistryKey(a, b) + close;
    }

}

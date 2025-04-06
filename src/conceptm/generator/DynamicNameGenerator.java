package conceptm.generator;

import conceptm.world.type.*;
import mindustry.content.StatusEffects;
import mindustry.type.*;

public class DynamicNameGenerator {
    private static final String[] COMMON_SUFFIXES = {
            "ite", "ium", "ide", "ate", "ine", "ite", "on", "alloy", "comp", "synth"
    };

    private static final String[] GAS_PARTS = {
            "vapor", "gas", "aero", "pneu", "wind", "steam", "fume", "mist"
    };

    private static String removeCommonSuffixes(String name) {
        for (String suffix : COMMON_SUFFIXES) {
            if (name.toLowerCase().endsWith(suffix)) {
                return name.substring(0, name.length() - suffix.length());
            }
        }
        return name;
    }

    private static boolean isGoodBlend(String blended) {
        // Simple heuristic - at least 5 characters and not too long
        return blended.length() >= 5 && blended.length() <= 12;
    }
    private static String getMeaningfulPart(String name) {
        // Try to find the stem of the word
        String clean = removeCommonSuffixes(name);

        // Take first 3-4 meaningful characters
        int length = Math.min(4, clean.length());
        if (isVowel(clean.charAt(length-1)) && length > 1) {
            length--; // Avoid ending with vowel
        }
        return clean.substring(0, length);
    }

    private static boolean isVowel(char c) {
        return "aeiouAEIOU".indexOf(c) != -1;
    }

    public static String generateName(Object a, Object b, boolean isLiquid) {
        // First try linguistic blending
        var ia = (a instanceof Item i0) ? i0.localizedName : (a instanceof CustomItem c0) ? c0.localizedName : (a instanceof Liquid i0) ? i0.localizedName : (a instanceof CustomLiquid c0) ? c0.localizedName : null;
        var ib = (b instanceof Item i1) ? i1.localizedName : (b instanceof CustomItem c1) ? c1.localizedName : (b instanceof Liquid i1) ? i1.localizedName : (b instanceof CustomLiquid c1) ? c1.localizedName : null;
        String blended = blendNamesLinguistically(ia, ib);
        if (isGoodBlend(blended)) {
            return blended;
        }

        String suffix = determineSuffix(a, b);
        if (!isLiquid) {
            // Fall back to material-based generation
            MaterialType typeA = classifyMaterial(a);
            MaterialType typeB = classifyMaterial(b);

            if (typeA == MaterialType.METAL && typeB == MaterialType.METAL) {
                return generateMetalName(ia, ib, suffix);
            } else if (typeA == MaterialType.CRYSTAL || typeB == MaterialType.CRYSTAL) {
                return generateCrystalName(ia, ib, suffix);
            } else if (typeA == MaterialType.ORGANIC || typeB == MaterialType.ORGANIC) {
                return generateOrganicName(ia, ib, suffix);
            } else {
                return generateDefaultName(ia, ib, suffix);
            }
        } else {
            // Fall back to material-based generation
            LiquidType typeA = classifyLiquid(ia);
            LiquidType typeB = classifyLiquid(ib);
            boolean Agas = (a instanceof Liquid i0) ? i0.gas : a instanceof CustomLiquid c0 && c0.gas;
            boolean Bgas = (b instanceof Liquid i1) ? i1.gas : b instanceof CustomLiquid c1 && c1.gas;

            if (typeA == LiquidType.METAL && typeB == LiquidType.METAL) {
                return generateMetalName(ia, ib, suffix);
            } else if (typeA == LiquidType.CRYSTAL || typeB == LiquidType.CRYSTAL) {
                return generateCrystalName(ia, ib, suffix);
            } else if (typeA == LiquidType.ORGANIC || typeB == LiquidType.ORGANIC) {
                return generateOrganicName(ia, ib, suffix);
            } else if (Agas || Bgas) {
                return generateGasName(ia, ib, suffix);
            } else {
                return generateDefaultName(ia, ib, suffix);
            }
        }
    }

    private enum MaterialType { METAL, CRYSTAL, ORGANIC, OTHER }
    private enum LiquidType { METAL, CRYSTAL, ORGANIC, GAS, OTHER }

    private static LiquidType classifyLiquid(Object liquid) {
        if (liquid instanceof Liquid a) {
            if (a.temperature > 0.7f && !a.gas) {
                return LiquidType.METAL;
            } else if (a.temperature < 0.3f && a.heatCapacity > 0.4f) {
                return LiquidType.CRYSTAL;
            } else if (a.effect != StatusEffects.none || a.localizedName.contains("bio")) {
                return LiquidType.ORGANIC;
            } else if (a.gas) {
                return LiquidType.GAS;
            }
        }

        if (liquid instanceof CustomLiquid a) {
            if (a.temperature > 0.7f && !a.gas) {
                return LiquidType.METAL;
            } else if (a.temperature < 0.3f && a.heatCapacity > 0.4f) {
                return LiquidType.CRYSTAL;
            } else if (a.effect != StatusEffects.none || a.localizedName.contains("bio")) {
                return LiquidType.ORGANIC;
            } else if (a.gas) {
                return LiquidType.GAS;
            }
        }

        return LiquidType.OTHER;
    }


    private static MaterialType classifyMaterial(Object item) {
        if (item instanceof Item a) {
            if (a.hardness > 4 && a.flammability < 0.3f) {
                return MaterialType.METAL;
            } else if (a.hardness > 5 && a.radioactivity < 0.2f) {
                return MaterialType.CRYSTAL;
            } else if (a.flammability > 0.5f || a.localizedName.contains("flesh")) {
                return MaterialType.ORGANIC;
            }
        }

        if (item instanceof CustomItem a) {
            if (a.hardness > 4 && a.flammability < 0.3f) {
                return MaterialType.METAL;
            } else if (a.hardness > 5 && a.radioactivity < 0.2f) {
                return MaterialType.CRYSTAL;
            } else if (a.flammability > 0.5f || a.localizedName.contains("flesh")) {
                return MaterialType.ORGANIC;
            }
        }
        return MaterialType.OTHER;
    }

    private static String generateGasName(String a, String b, String suffix) {
        String part1 = GAS_PARTS[Math.abs(a.hashCode()) % GAS_PARTS.length];
        String part2 = getMeaningfulPart(b);
        return capitalize(part1 + part2 + suffix);
    }

    private static String generateMetalName(String a, String b, String suffix) {
        String base = getMeaningfulPart(a) + getMeaningfulPart(b);
        return capitalize(base + suffix);
    }

    private static String generateCrystalName(String a, String b, String suffix) {
        String part1 = getMeaningfulPart(a);
        String part2 = getMeaningfulPart(b);
        return capitalize(part1 + part2 + "ite");
    }

    private static String generateOrganicName(String a, String b, String suffix) {
        String part1 = getMeaningfulPart(a);
        String part2 = getMeaningfulPart(b);
        return capitalize(part1 + part2 + "ium");
    }

    private static String generateDefaultName(String a, String b, String suffix) {
        String partA = getMeaningfulPart(a);
        String partB = getMeaningfulPart(b);
        return capitalize(partA + partB + suffix);
    }

    private static String getBaseName(String itemName) {
        String name = itemName.replaceAll("(ite|ium|ide)$", "");
        return name.substring(0, Math.min(4, name.length()));
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String blendNamesLinguistically(String name1, String name2) {
        // Clean names by removing common suffixes
        String clean1 = removeCommonSuffixes(name1);
        String clean2 = removeCommonSuffixes(name2);

        // Try to find natural blending points
        int overlap = findBestOverlap(clean1, clean2);

        if (overlap >= 2) {
            // Good overlap found - blend at this point
            return clean1.substring(0, clean1.length() - overlap) + clean2;
        } else {
            // No good overlap - take first half of first and second half of second
            int split1 = clean1.length() / 2;
            int split2 = clean2.length() / 2;
            return clean1.substring(0, split1) + clean2.substring(split2);
        }
    }

    private static int findBestOverlap(String a, String b) {
        // Look for 2-3 character overlaps
        for (int len = Math.min(3, Math.min(a.length(), b.length())); len >= 2; len--) {
            String endOfA = a.substring(a.length() - len);
            String startOfB = b.substring(0, len);
            if (endOfA.equalsIgnoreCase(startOfB)) {
                return len;
            }
        }
        return 0;
    }

    private static String determineSuffix(Object a0, Object b0) {

        var aHard = (a0 instanceof Item item) ? item.hardness : (a0 instanceof CustomItem comboItem) ? comboItem.hardness : 0;
        var bHard = (b0 instanceof Item item) ? item.hardness : (b0 instanceof CustomItem comboItem) ? comboItem.hardness : 0;

        var aflame = (a0 instanceof Item item) ? item.flammability : (a0 instanceof CustomItem comboItem) ? comboItem.flammability : 0;
        var bflame = (b0 instanceof Item item) ? item.flammability : (b0 instanceof CustomItem comboItem) ? comboItem.flammability : 0;

        var aradio = (a0 instanceof Item item) ? item.radioactivity : (a0 instanceof CustomItem comboItem) ? comboItem.radioactivity : 0;
        var bradio = (b0 instanceof Item item) ? item.radioactivity : (b0 instanceof CustomItem comboItem) ? comboItem.radioactivity : 0;

        float avgHardness = (aHard + bHard) / 2f;
        float avgFlammability = (aflame + bflame) / 2f;
        float avgRadioactivity = (aradio + bradio) / 2f;

        if (avgRadioactivity > 0.7f) return "ite";
        if (avgFlammability > 0.5f) return "ene";
        if (avgHardness > 7f) return "ite";
        if (avgHardness > 4f) return "ium";
        return "ide";
    }
}

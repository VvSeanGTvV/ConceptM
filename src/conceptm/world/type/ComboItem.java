package conceptm.world.type;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.type.Item;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import java.util.*;

public class ComboItem {

    public String name;
    public Color color;

    /** Stat storage for this content. Initialized on creation. */
    public Stats stats = new Stats();

    /** how explosive this item is. */
    public float explosiveness = 0f;
    /** flammability above 0.3 makes this eligible for item burners. */
    public float flammability = 0f;
    /** how radioactive this item is. */
    public float radioactivity;
    /** how electrically potent this item is. */
    public float charge = 0f;
    /** drill hardness of the item */
    public int hardness = 0;
    /**
     * base material cost of this item, used for calculating place times
     * 1 cost = 1 tick added to build time
     */
    public float cost = 1f;
    /** When this item is present in the build cost, a block's <b>default</b> health is multiplied by 1 + scaling, where 'scaling' is summed together for all item requirement types. */
    public float healthScaling = 0f;
    /** if true, this item is of the lowest priority to drills. */
    public boolean lowPriority;

    /** If true, this material is used by buildings. If false, this material will be incinerated in certain cores. */
    public boolean buildable = true;
    public boolean hidden = false;

    public Item item1, item2;
    public ComboItem item1c, item2c;
    private static final NameRegistry nameRegistry = new NameRegistry();

    public TextureRegion fullIcon;

    public ComboItem(String name, Object a0, Object b0){
        var aHard = (a0 instanceof Item item) ? item.hardness : (a0 instanceof ComboItem comboItem) ? comboItem.hardness : 0;
        var bHard = (b0 instanceof Item item) ? item.hardness : (b0 instanceof ComboItem comboItem) ? comboItem.hardness : 0;
        var aflame = (a0 instanceof Item item) ? item.flammability : (a0 instanceof ComboItem comboItem) ? comboItem.flammability : 0;
        var bflame = (b0 instanceof Item item) ? item.flammability : (b0 instanceof ComboItem comboItem) ? comboItem.flammability : 0;
        var aradio = (a0 instanceof Item item) ? item.radioactivity : (a0 instanceof ComboItem comboItem) ? comboItem.radioactivity : 0;
        var bradio = (b0 instanceof Item item) ? item.radioactivity : (b0 instanceof ComboItem comboItem) ? comboItem.radioactivity : 0;
        var acharge = (a0 instanceof Item item) ? item.charge : (a0 instanceof ComboItem comboItem) ? comboItem.charge : 0;
        var bcharge = (b0 instanceof Item item) ? item.charge : (b0 instanceof ComboItem comboItem) ? comboItem.charge : 0;
        var aexplode = (a0 instanceof Item item) ? item.explosiveness : (a0 instanceof ComboItem comboItem) ? comboItem.explosiveness : 0;
        var bexplode = (b0 instanceof Item item) ? item.explosiveness : (b0 instanceof ComboItem comboItem) ? comboItem.explosiveness : 0;
        var acost = (a0 instanceof Item item) ? item.cost : (a0 instanceof ComboItem comboItem) ? comboItem.cost : 0;
        var bcost = (b0 instanceof Item item) ? item.cost : (b0 instanceof ComboItem comboItem) ? comboItem.cost : 0;
        var ascale = (a0 instanceof Item item) ? item.healthScaling : (a0 instanceof ComboItem comboItem) ? comboItem.healthScaling : 0;
        var bscale = (b0 instanceof Item item) ? item.healthScaling : (b0 instanceof ComboItem comboItem) ? comboItem.healthScaling : 0;

        // Combine numeric properties (average them)
        int div = 1;
        this.hardness = (aHard + bHard) / div;
        this.charge = (acharge + bcharge) / div;
        this.radioactivity = (aradio + bradio) / div;
        this.flammability = (aflame + bflame) / div;
        this.explosiveness = (aexplode + bexplode) / div;
        this.cost = (acost + bcost) / div;
        this.healthScaling = (ascale + bscale) / div;

        item1 = (a0 instanceof Item item) ? item : null;
        item2 = (b0 instanceof Item item) ? item : null;

        item1c = (a0 instanceof ComboItem item) ? item : null;
        item2c = (b0 instanceof ComboItem item) ? item : null;

        List<Boolean> boolList = Arrays.asList(
                item1 != null && item1.lowPriority,
                item2 != null && item2.lowPriority,
                item1c != null && item1c.lowPriority,
                item2c != null && item2c.lowPriority
                );
        this.lowPriority = boolList.stream().reduce(false, Boolean::logicalOr);

        boolList = Arrays.asList(
                item1 != null && item1.buildable,
                item2 != null && item2.buildable,
                item1c != null && item1c.buildable,
                item2c != null && item2c.buildable
        );
        this.buildable = boolList.stream().reduce(false, Boolean::logicalOr);

        this.color = blendColorsVibrant(
                (item1 != null) ? item1.color : item1c.color,
                (item2 != null) ? item2.color : item2c.color
        );

        this.name = name;
        createIcons(item1, item2, item1c, item2c);
        setStats();
    }

    public ComboItem(Object item1, Object item2) {
        this(
                nameRegistry.getNameFor(item1, item2),
                item1,
                item2
        );
    }

    public void setStats() {
        stats.addPercent(Stat.explosiveness, explosiveness);
        stats.addPercent(Stat.flammability, flammability);
        stats.addPercent(Stat.radioactivity, radioactivity);
        stats.addPercent(Stat.charge, charge);
    }

    public void draw(float x, float y, float size) {
        draw(x, y, size, color);
    }

    public void draw(float x, float y, float size, Color color){
        Draw.color(color);
        Draw.rect(fullIcon, x, y, size, size);
    }

    public void createIcons(Item item0, Item item1, ComboItem comboItem0, ComboItem comboItem1) {
        // Get the pixmaps for both items
        var icon1 = (item0 != null) ? item0.fullIcon : (comboItem0 != null) ? comboItem0.fullIcon : Core.atlas.find("white");
        var icon2 = (item1  != null) ? item1.fullIcon : (comboItem1 != null) ? comboItem1.fullIcon : Core.atlas.find("white");
        fullIcon = icon1;
    }

    /*@Override
    public void loadIcon() {
        super.loadIcon();

        fullIcon = uiIcon = Core.atlas.find(name + "-full");
    }*/

    // Alternative constructor that generates a name automatically


    public static class NameRegistry {
        private final Map<String, String> generatedNames = new HashMap<>();

        public String getNameFor(Object a, Object b) {
            String an = (a instanceof Item ai) ? ai.localizedName : (a instanceof ComboItem ac) ? ac.name : "";
            String bn = (a instanceof Item bi) ? bi.localizedName : (a instanceof ComboItem bc) ? bc.name : "";
            String key = generateRegistryKey(an, bn);
            return generatedNames.computeIfAbsent(key, k -> DynamicNameGenerator.generateName(a, b));
        }

        private String generateRegistryKey(String a, String b) {
            // Ensure consistent key regardless of order
            return a.compareTo(b) < 0 ?
                    a + "|" + b :
                    b + "|" + a;
        }

    }

    public static class DynamicNameGenerator {
        private static final String[] COMMON_SUFFIXES = {
                "ite", "ium", "ide", "ate", "ine", "ite", "on", "alloy", "comp", "synth"
        };

        private static final String[] METAL_PREFIXES = {
                "ferr", "cupr", "argent", "aur", "stann", "plumb", "titan", "nickel"
        };

        private static final String[] CRYSTAL_PARTS = {
                "cryst", "quartz", "gem", "prism", "diam", "shard", "lith", "fluor"
        };

        private static final String[] ORGANIC_PARTS = {
                "bio", "organ", "cell", "vita", "life", "growth", "culture"
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

        public static String generateName(Object a, Object b) {
            // First try linguistic blending
            var ia = (a instanceof Item i0) ? i0.localizedName : (a instanceof ComboItem c0) ? c0.name : null;
            var ib = (b instanceof Item i1) ? i1.localizedName : (b instanceof ComboItem c1) ? c1.name : null;
            String blended = blendNamesLinguistically(ia, ib);
            if (isGoodBlend(blended)) {
                return blended;
            }

            // Fall back to material-based generation
            MaterialType typeA = classifyMaterial(a);
            MaterialType typeB = classifyMaterial(b);
            String suffix = determineSuffix(a, b);

            if (typeA == MaterialType.METAL && typeB == MaterialType.METAL) {
                return generateMetalName(ia, ib, suffix);
            } else if (typeA == MaterialType.CRYSTAL || typeB == MaterialType.CRYSTAL) {
                return generateCrystalName(ia, ib, suffix);
            } else if (typeA == MaterialType.ORGANIC || typeB == MaterialType.ORGANIC) {
                return generateOrganicName(ia, ib, suffix);
            } else {
                return generateDefaultName(ia, ib, suffix);
            }
        }

        private enum MaterialType { METAL, CRYSTAL, ORGANIC, OTHER }

        private static MaterialType classifyMaterial(Object item) {
            if (item instanceof Item a) {
                if (a.hardness > 4 && a.flammability < 0.3f) {
                    return MaterialType.METAL;
                } else if (a.hardness > 5 && a.radioactivity < 0.2f) {
                    return MaterialType.CRYSTAL;
                } else if (a.flammability > 0.5f || a.name.contains("flesh")) {
                    return MaterialType.ORGANIC;
                }
            }

            if (item instanceof ComboItem a) {
                if (a.hardness > 4 && a.flammability < 0.3f) {
                    return MaterialType.METAL;
                } else if (a.hardness > 5 && a.radioactivity < 0.2f) {
                    return MaterialType.CRYSTAL;
                } else if (a.flammability > 0.5f || a.name.contains("flesh")) {
                    return MaterialType.ORGANIC;
                }
            }
            return MaterialType.OTHER;
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

            var aHard = (a0 instanceof Item item) ? item.hardness : (a0 instanceof ComboItem comboItem) ? comboItem.hardness : 0;
            var bHard = (b0 instanceof Item item) ? item.hardness : (b0 instanceof ComboItem comboItem) ? comboItem.hardness : 0;

            var aflame = (a0 instanceof Item item) ? item.flammability : (a0 instanceof ComboItem comboItem) ? comboItem.flammability : 0;
            var bflame = (b0 instanceof Item item) ? item.flammability : (b0 instanceof ComboItem comboItem) ? comboItem.flammability : 0;

            var aradio = (a0 instanceof Item item) ? item.radioactivity : (a0 instanceof ComboItem comboItem) ? comboItem.radioactivity : 0;
            var bradio = (b0 instanceof Item item) ? item.radioactivity : (b0 instanceof ComboItem comboItem) ? comboItem.radioactivity : 0;

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

    private static String generateCombinedName(Item item1, Item item2) {
        // Simple name combination logic
        return item1.name + "-" + item2.name;
    }

    private static Color blendColorsHSL(Color c1, Color c2) {
        // Convert RGB to HSL for both colors
        float[] hsl1 = rgbToHsl(c1.r, c1.g, c1.b);
        float[] hsl2 = rgbToHsl(c2.r, c2.g, c2.b);

        // Blend in HSL space
        float h = averageAngles(hsl1[0], hsl2[0]);
        float s = (hsl1[1] + hsl2[1]) / 2f;
        float l = (hsl1[2] + hsl2[2]) / 2f;

        // Convert back to RGB
        float[] rgb = hslToRgb(h, s, l);
        return new Color(rgb[0], rgb[1], rgb[2], (c1.a + c2.a) / 2f);
    }

    private static Color blendColorsVibrant(Color c1, Color c2) {
        // Use multiply blend mode for richer results
        float r = Math.min(1f, c1.r * c2.r * 1.2f);
        float g = Math.min(1f, c1.g * c2.g * 1.2f);
        float b = Math.min(1f, c1.b * c2.b * 1.2f);

        // Boost saturation
        float[] hsl = rgbToHsl(r, g, b);
        hsl[1] = Math.min(1f, hsl[1] * 1.3f); // Increase saturation
        float[] rgb = hslToRgb(hsl[0], hsl[1], hsl[2]);

        return new Color(rgb[0], rgb[1], rgb[2], (c1.a + c2.a) / 2f);
    }

    private static float averageAngles(float a1, float a2) {
        // Special handling for hue (circular value)
        float diff = Math.abs(a1 - a2);
        if (diff > 0.5f) {
            return ((a1 + a2 + 1f) / 2f) % 1f;
        }
        return (a1 + a2) / 2f;
    }

    // RGB to HSL conversion
    private static float[] rgbToHsl(float r, float g, float b) {
        float max = Math.max(Math.max(r, g), b);
        float min = Math.min(Math.min(r, g), b);
        float h, s, l = (max + min) / 2f;

        if (max == min) {
            h = s = 0f; // achromatic
        } else {
            float d = max - min;
            s = l > 0.5f ? d / (2f - max - min) : d / (max + min);

            if (max == r) {
                h = (g - b) / d + (g < b ? 6f : 0f);
            } else if (max == g) {
                h = (b - r) / d + 2f;
            } else {
                h = (r - g) / d + 4f;
            }
            h /= 6f;
        }
        return new float[]{h, s, l};
    }

    // HSL to RGB conversion
    private static float[] hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0f) {
            r = g = b = l; // achromatic
        } else {
            float q = l < 0.5f ? l * (1f + s) : l + s - l * s;
            float p = 2f * l - q;
            r = hueToRgb(p, q, h + 1f/3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f/3f);
        }
        return new float[]{r, g, b};
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0f) t += 1f;
        if (t > 1f) t -= 1f;
        if (t < 1f/6f) return p + (q - p) * 6f * t;
        if (t < 1f/2f) return q;
        if (t < 2f/3f) return p + (q - p) * (2f/3f - t) * 6f;
        return p;
    }
}

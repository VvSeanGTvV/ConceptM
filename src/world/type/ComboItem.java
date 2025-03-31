package world.type;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.Log;
import mindustry.gen.Tex;
import mindustry.graphics.*;
import mindustry.type.Item;

import java.util.*;

public class ComboItem {

    public String name;
    public Color color;

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

    public ComboItem(String name, Item item1, Item item2) {
        this.color = blendColorsVibrant(item1.color, item2.color); // Using smart blending
        this.item1 = item1;
        this.item2 = item2;

        // Combine numeric properties (average them)
        this.hardness = (item1.hardness + item2.hardness) / 2;
        this.charge = (item1.charge + item2.charge) / 2;
        this.radioactivity = (item1.radioactivity + item2.radioactivity) / 2;
        this.flammability = (item1.flammability + item2.flammability) / 2;
        this.explosiveness = (item1.explosiveness + item2.explosiveness) / 2;
        this.cost = (item1.cost + item2.cost) / 2;
        this.healthScaling = (item1.healthScaling + item2.healthScaling) / 2;

        // Combine boolean properties (OR operation)
        this.lowPriority = item1.lowPriority || item2.lowPriority;
        this.buildable = item1.buildable || item2.buildable;
        this.name = name;

        createIcons(new MultiPacker());
    }

    public ComboItem(String name, ComboItem item1, Item item2) {
        this.color = blendColorsVibrant(item1.color, item2.color); // Using smart blending
        this.item1c = item1;
        this.item2 = item2;

        // Combine numeric properties (average them)
        this.hardness = (item1.hardness + item2.hardness) / 2;
        this.charge = (item1.charge + item2.charge) / 2;
        this.radioactivity = (item1.radioactivity + item2.radioactivity) / 2;
        this.flammability = (item1.flammability + item2.flammability) / 2;
        this.explosiveness = (item1.explosiveness + item2.explosiveness) / 2;
        this.cost = (item1.cost + item2.cost) / 2;
        this.healthScaling = (item1.healthScaling + item2.healthScaling) / 2;

        // Combine boolean properties (OR operation)
        this.lowPriority = item1.lowPriority || item2.lowPriority;
        this.buildable = item1.buildable || item2.buildable;
        this.name = name;

        createIcons(new MultiPacker());
    }

    public ComboItem(String name, Item item1, ComboItem item2) {
        this.color = blendColorsVibrant(item1.color, item2.color); // Using smart blending
        this.item1 = item1;
        this.item2c = item2;

        // Combine numeric properties (average them)
        this.hardness = (item1.hardness + item2.hardness) / 2;
        this.charge = (item1.charge + item2.charge) / 2;
        this.radioactivity = (item1.radioactivity + item2.radioactivity) / 2;
        this.flammability = (item1.flammability + item2.flammability) / 2;
        this.explosiveness = (item1.explosiveness + item2.explosiveness) / 2;
        this.cost = (item1.cost + item2.cost) / 2;
        this.healthScaling = (item1.healthScaling + item2.healthScaling) / 2;

        // Combine boolean properties (OR operation)
        this.lowPriority = item1.lowPriority || item2.lowPriority;
        this.buildable = item1.buildable || item2.buildable;
        this.name = name;

        createIcons(new MultiPacker());
    }

    public ComboItem(String name, ComboItem item1, ComboItem item2) {
        this.color = blendColorsVibrant(item1.color, item2.color); // Using smart blending
        this.item1c = item1;
        this.item2c = item2;

        // Combine numeric properties (average them)
        this.hardness = (item1.hardness + item2.hardness) / 2;
        this.charge = (item1.charge + item2.charge) / 2;
        this.radioactivity = (item1.radioactivity + item2.radioactivity) / 2;
        this.flammability = (item1.flammability + item2.flammability) / 2;
        this.explosiveness = (item1.explosiveness + item2.explosiveness) / 2;
        this.cost = (item1.cost + item2.cost) / 2;
        this.healthScaling = (item1.healthScaling + item2.healthScaling) / 2;

        // Combine boolean properties (OR operation)
        this.lowPriority = item1.lowPriority || item2.lowPriority;
        this.buildable = item1.buildable || item2.buildable;
        this.name = name;

        createIcons(new MultiPacker());
    }

    public ComboItem(Item item1, Item item2) {
        this(
                nameRegistry.getNameFor(item1, item2),
                item1,
                item2
        );
    }

    public ComboItem(ComboItem item1, Item item2) {
        this(
                nameRegistry.getNameFor(item1, item2),
                item1,
                item2
        );
    }

    public ComboItem(Item item1, ComboItem item2) {
        this(
                nameRegistry.getNameFor(item1, item2),
                item1,
                item2
        );
    }

    public ComboItem(ComboItem item1, ComboItem item2) {
        this(
                nameRegistry.getNameFor(item1, item2),
                item1,
                item2
        );
    }

    public void draw(float x, float y, float size) {
        draw(x, y, size, color);
    }

    public void draw(float x, float y, float size, Color color){
        Draw.color(color);
        if (item1 == null) Draw.rect(item1c.fullIcon, x, y, size, size); else
            Draw.rect(Core.atlas.find(name + "-full"), x, y, size, size);
    }

    public void createIcons(MultiPacker packer) {
        // Get the pixmaps for both items
        var icon1 = (item1 != null) ? item1.fullIcon : (item1c != null) ? item1c.fullIcon : Core.atlas.find("white");
        var icon2 = (item2 != null) ? item2.fullIcon : (item2c != null) ? item2c.fullIcon : Core.atlas.find("white");
        if (!Core.atlas.isFound(Core.atlas.find(name + "-full"))) {
            PixmapRegion pix1 = Core.atlas.getPixmap(icon1);
            PixmapRegion pix2 = Core.atlas.getPixmap(icon2);

            // Determine the output size (use the larger dimensions)
            int width = Math.max(icon1.width, icon2.width);
            int height = Math.max(icon1.height, icon2.height);

            // Create output pixmap
            Pixmap result = new Pixmap(width, height);
            String regionName = name;
            // Blend each pixel
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // Get colors from both pixmaps (with bounds checking)
                    Color color1 = new Color();
                    Color color2 = new Color();

                    if (x < pix1.width && y < pix1.height) {
                        color1.set(pix1.pixmap.get(x, y));
                    }

                    if (x < pix2.width && y < pix2.height) {
                        color2.set(pix2.pixmap.get(x, y));
                    }

                    // Blend the colors
                    Color blended = blendColorsVibrant(color1, color2);

                    // Set the blended pixel
                    result.set(x, y, blended);
                }
            }

            packer.add(MultiPacker.PageType.main, regionName + "-full", result);
            Core.atlas.addRegion(regionName + "-full", new TextureRegion(new Texture(packer.get(regionName + "-full").pixmap)));
        }
    }

    /*@Override
    public void loadIcon() {
        super.loadIcon();

        fullIcon = uiIcon = Core.atlas.find(name + "-full");
    }*/

    // Alternative constructor that generates a name automatically


    public static class NameRegistry {
        private final Map<String, String> generatedNames = new HashMap<>();

        public String getNameFor(Item a, Item b) {
            String key = generateRegistryKey(a.name, b.name);
            return generatedNames.computeIfAbsent(key, k -> DynamicNameGenerator.generateName(a, b));
        }

        public String getNameFor(ComboItem a, Item b) {
            String key = generateRegistryKey(a.name, b.name);
            return generatedNames.computeIfAbsent(key, k -> DynamicNameGenerator.generateNameSingleCombo(a, b));
        }

        public String getNameFor(Item a, ComboItem b) {
            String key = generateRegistryKey(a.name, b.name);
            return generatedNames.computeIfAbsent(key, k -> DynamicNameGenerator.generateNameSingleCombo(a, b));
        }

        public String getNameFor(ComboItem a, ComboItem b) {
            String key = generateRegistryKey(a.name, b.name);
            return generatedNames.computeIfAbsent(key, k -> DynamicNameGenerator.generateNameDualCombo(a, b));
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

        public static String generateName(Item a, Item b) {
            // Determine material category
            MaterialType typeA = classifyMaterial(a);
            MaterialType typeB = classifyMaterial(b);

            // Generate based on combined categories
            if (typeA == MaterialType.METAL && typeB == MaterialType.METAL) {
                return generateMetalName(a.name, b.name);
            } else if (typeA == MaterialType.CRYSTAL || typeB == MaterialType.CRYSTAL) {
                return generateCrystalName(a.name, b.name);
            } else if (typeA == MaterialType.ORGANIC || typeB == MaterialType.ORGANIC) {
                return generateOrganicName(a.name, b.name);
            } else {
                return generateDefaultName(a.name, b.name);
            }
        }

        public static String generateNameSingleCombo(ComboItem a, Item b) {
            // Determine material category
            MaterialType typeA = classifyMaterial(a);
            MaterialType typeB = classifyMaterial(b);

            // Generate based on combined categories
            if (typeA == MaterialType.METAL && typeB == MaterialType.METAL) {
                return generateMetalName(a.name, b.name);
            } else if (typeA == MaterialType.CRYSTAL || typeB == MaterialType.CRYSTAL) {
                return generateCrystalName(a.name, b.name);
            } else if (typeA == MaterialType.ORGANIC || typeB == MaterialType.ORGANIC) {
                return generateOrganicName(a.name, b.name);
            } else {
                return generateDefaultName(a.name, b.name);
            }
        }

        public static String generateNameSingleCombo(Item a, ComboItem b) {
            // Determine material category
            MaterialType typeA = classifyMaterial(a);
            MaterialType typeB = classifyMaterial(b);

            // Generate based on combined categories
            if (typeA == MaterialType.METAL && typeB == MaterialType.METAL) {
                return generateMetalName(a.name, b.name);
            } else if (typeA == MaterialType.CRYSTAL || typeB == MaterialType.CRYSTAL) {
                return generateCrystalName(a.name, b.name);
            } else if (typeA == MaterialType.ORGANIC || typeB == MaterialType.ORGANIC) {
                return generateOrganicName(a.name, b.name);
            } else {
                return generateDefaultName(a.name, b.name);
            }
        }

        public static String generateNameDualCombo(ComboItem a, ComboItem b) {
            // Determine material category
            MaterialType typeA = classifyMaterial(a);
            MaterialType typeB = classifyMaterial(b);

            // Generate based on combined categories
            if (typeA == MaterialType.METAL && typeB == MaterialType.METAL) {
                return generateMetalName(a.name, b.name);
            } else if (typeA == MaterialType.CRYSTAL || typeB == MaterialType.CRYSTAL) {
                return generateCrystalName(a.name, b.name);
            } else if (typeA == MaterialType.ORGANIC || typeB == MaterialType.ORGANIC) {
                return generateOrganicName(a.name, b.name);
            } else {
                return generateDefaultName(a.name, b.name);
            }
        }

        private enum MaterialType { METAL, CRYSTAL, ORGANIC, OTHER }

        private static MaterialType classifyMaterial(Item item) {
            if (item.hardness > 4 && item.flammability < 0.3f) {
                return MaterialType.METAL;
            } else if (item.hardness > 5 && item.radioactivity < 0.2f) {
                return MaterialType.CRYSTAL;
            } else if (item.flammability > 0.5f || item.name.contains("flesh")) {
                return MaterialType.ORGANIC;
            }
            return MaterialType.OTHER;
        }

        private static MaterialType classifyMaterial(ComboItem item) {
            if (item.hardness > 4 && item.flammability < 0.3f) {
                return MaterialType.METAL;
            } else if (item.hardness > 5 && item.radioactivity < 0.2f) {
                return MaterialType.CRYSTAL;
            } else if (item.flammability > 0.5f || item.name.contains("flesh")) {
                return MaterialType.ORGANIC;
            }
            return MaterialType.OTHER;
        }

        private static String generateMetalName(String a, String b) {
            String base = getBaseName(a) + getBaseName(b);
            String suffix = COMMON_SUFFIXES[(a.hashCode() + b.hashCode()) % COMMON_SUFFIXES.length];
            return capitalize(base + suffix);
        }

        private static String generateCrystalName(String a, String b) {
            String part1 = CRYSTAL_PARTS[Math.abs(a.hashCode()) % CRYSTAL_PARTS.length];
            String part2 = CRYSTAL_PARTS[Math.abs(b.hashCode()) % CRYSTAL_PARTS.length];
            return capitalize(part1 + part2 + "ite");
        }

        private static String generateOrganicName(String a, String b) {
            String prefix = ORGANIC_PARTS[Math.abs(a.hashCode()) % ORGANIC_PARTS.length];
            String suffix = ORGANIC_PARTS[Math.abs(b.hashCode()) % ORGANIC_PARTS.length];
            return capitalize(prefix + suffix + "ium");
        }

        private static String generateDefaultName(String a, String b) {
            String partA = a.substring(0, Math.min(3, a.length()));
            String partB = b.substring(0, Math.min(3, b.length()));
            String suffix = COMMON_SUFFIXES[(partA.hashCode() + partB.hashCode()) % COMMON_SUFFIXES.length];
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
            // Find the best overlapping syllable
            int overlap = findBestOverlap(name1, name2);

            if (overlap > 1) {
                // Merge at overlapping point
                return name1.substring(0, name1.length() - overlap) + name2;
            } else {
                // Take first half of first name and second half of second name
                return name1.substring(0, name1.length()/2) +
                        name2.substring(name2.length()/2);
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

        private static String determineSuffix(Item a, Item b) {
            float avgHardness = (a.hardness + b.hardness) / 2f;
            float avgFlammability = (a.flammability + b.flammability) / 2f;
            float avgRadioactivity = (a.radioactivity + b.radioactivity) / 2f;

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

package conceptm.generator;

import conceptm.world.type.*;
import mindustry.content.StatusEffects;
import mindustry.type.*;
import java.util.*;

public class DynamicNameGenerator {
    // Special combinations
    private static final Map<String, Map<String, String>> SPECIAL_COMBINATIONS = new HashMap<>();

    // Phoneme patterns for different material types
    private static final Map<String, String[]> PHONEME_PATTERNS = new HashMap<>();

    static {
        // Initialize special combinations
        Map<String, String> leadCombos = new HashMap<>();
        leadCombos.put("sand", "metaglass");
        SPECIAL_COMBINATIONS.put("lead", leadCombos);

        Map<String, String> sporeCombos = new HashMap<>();
        sporeCombos.put("water", "sporepod");
        SPECIAL_COMBINATIONS.put("spore", sporeCombos);

        Map<String, String> waterCombos = new HashMap<>();
        waterCombos.put("oil", "emulsion");
        SPECIAL_COMBINATIONS.put("water", waterCombos);

        // Phoneme patterns by material type
        PHONEME_PATTERNS.put("metal", new String[]{
                "br", "cr", "dr", "fr", "gr", "kr", "str",
                "tr", "vr", "zr", "thr", "chr", "phr"
        });

        PHONEME_PATTERNS.put("liquid", new String[]{
                "fl", "gl", "pl", "sl", "vl", "zl",
                "shr", "spl", "spr", "thl"
        });

        PHONEME_PATTERNS.put("organic", new String[]{
                "bl", "cl", "dl", "fl", "gl", "kl",
                "pl", "sl", "chl", "phl", "scl"
        });
    }

    public static String generateName(Object a, Object b) {
        // Check special combinations first
        String specialName = checkSpecialCombinations(a, b);
        if (specialName != null) {
            return capitalize(specialName);
        }

        MaterialProfile profileA = new MaterialProfile(a);
        MaterialProfile profileB = new MaterialProfile(b);

        // Generate name based on material types
        if (profileA.isLiquid || profileB.isLiquid) {
            return generateLiquidBasedName(profileA, profileB);
        } else {
            return generateSolidName(profileA, profileB);
        }
    }

    private static String generateLiquidBasedName(MaterialProfile a, MaterialProfile b) {
        // Determine which is liquid
        MaterialProfile liquid = a.isLiquid ? a : b;
        MaterialProfile other = a.isLiquid ? b : a;

        // Generate core name
        String coreName = generateCoreName(liquid, other);

        // Add appropriate suffix
        String suffix = generateSuffix(liquid, other);

        return capitalize(coreName + suffix);
    }

    private static String generateCoreName(MaterialProfile a, MaterialProfile b) {
        // Get phoneme patterns for both materials
        String[] patternsA = getPhonemePatterns(a);
        String[] patternsB = getPhonemePatterns(b);

        // Generate multiple candidates
        List<String> candidates = new ArrayList<>();

        // 1. Pattern blending
        candidates.add(blendPhonemePatterns(
                patternsA[new Random().nextInt(patternsA.length)],
                patternsB[new Random().nextInt(patternsB.length)]
        ));

        // 2. Name fragment blending
        candidates.add(blendNameFragments(
                getMeaningfulFragment(a.baseName),
                getMeaningfulFragment(b.baseName)
        ));

        // 3. Phoneme construction
        candidates.add(constructFromPhonemes(a, b));

        // Select best candidate
        return selectBestCandidate(candidates);
    }

    private static String blendPhonemePatterns(String patternA, String patternB) {
        // Find natural blending point
        int blendPoint = Math.min(patternA.length(), patternB.length()) / 2;
        return patternA.substring(0, blendPoint) + patternB.substring(blendPoint);
    }

    private static String blendNameFragments(String fragmentA, String fragmentB) {
        // Find vowel transitions
        int splitA = findLastVowel(fragmentA);
        int splitB = findFirstVowel(fragmentB);

        if (splitA > 0 && splitB > 0) {
            return fragmentA.substring(0, splitA) + fragmentB.substring(splitB);
        }
        return fragmentA + fragmentB;
    }

    private static String constructFromPhonemes(MaterialProfile a, MaterialProfile b) {
        // Get characteristic phonemes
        String phonemesA = getCharacteristicPhonemes(a);
        String phonemesB = getCharacteristicPhonemes(b);

        // Combine distinct phonemes
        Set<String> combined = new LinkedHashSet<>();
        combined.addAll(Arrays.asList(phonemesA.split(" ")));
        combined.addAll(Arrays.asList(phonemesB.split(" ")));

        // Build new word
        StringBuilder builder = new StringBuilder();
        for (String phoneme : combined) {
            builder.append(phoneme);
        }
        return builder.toString();
    }

    private static String generateSuffix(MaterialProfile liquid, MaterialProfile other) {
        if (other.isLiquid) {
            // Liquid + liquid combination
            return "";
        } else if (other.isOrganic) {
            // Liquid + organic
            return "ose";
        } else {
            // Liquid + solid
            return "ite";
        }
    }

    private static String generateSolidName(MaterialProfile a, MaterialProfile b) {
        String coreName = generateCoreName(a, b);
        String suffix = generateSolidSuffix(a, b);
        return capitalize(coreName + suffix);
    }

    private static String generateSolidSuffix(MaterialProfile a, MaterialProfile b) {
        if (a.isMetal && b.isMetal) {
            return "ite";
        } else if (a.isCrystal || b.isCrystal) {
            return "ium";
        } else if (a.isOrganic || b.isOrganic) {
            return "in";
        }
        return "ide";
    }

    private static String[] getPhonemePatterns(MaterialProfile profile) {
        if (profile.isLiquid) return PHONEME_PATTERNS.get("liquid");
        if (profile.isOrganic) return PHONEME_PATTERNS.get("organic");
        if (profile.isMetal) return PHONEME_PATTERNS.get("metal");
        return PHONEME_PATTERNS.get("metal"); // default
    }

    private static String getMeaningfulFragment(String name) {
        if (name == null || name.isEmpty()) return "";
        name = name.toLowerCase().replaceAll("[^a-z]", "");

        // Remove common suffixes
        String[] suffixes = {"ite", "ium", "ide", "ate", "ine"};
        for (String suffix : suffixes) {
            if (name.endsWith(suffix) && name.length() > suffix.length()) {
                name = name.substring(0, name.length() - suffix.length());
                break;
            }
        }

        // Take meaningful part
        int length = Math.min(5, name.length());
        if (length > 3 && isVowel(name.charAt(length-1))) {
            length--;
        }
        return name.substring(0, length);
    }

    private static String getCharacteristicPhonemes(MaterialProfile profile) {
        // This would analyze the name for characteristic sound patterns
        // Simplified version for demonstration
        String name = profile.baseName.toLowerCase();
        if (name.contains("chr") || name.contains("phr")) return "chr phr";
        if (name.contains("sp")) return "sp spr spl";
        if (name.contains("tr")) return "tr thr str";
        return "br cr dr fr gr";
    }

    private static String selectBestCandidate(List<String> candidates) {
        // Score based on linguistic qualities
        return candidates.stream()
                .max(Comparator.comparingInt(c -> scoreName(c)))
                .orElse("compound");
    }

    private static int scoreName(String name) {
        int score = 0;
        if (name.length() >= 4 && name.length() <= 8) score += 3;
        if (countVowels(name) >= 2) score += 2;
        if (!name.matches(".*(.)\\1{2,}.*")) score += 1; // No triple letters
        return score;
    }

    private static int countVowels(String word) {
        int count = 0;
        for (char c : word.toCharArray()) {
            if (isVowel(c)) count++;
        }
        return count;
    }

    private static boolean isVowel(char c) {
        return "aeiou".indexOf(Character.toLowerCase(c)) != -1;
    }

    private static int findLastVowel(String word) {
        for (int i = word.length()-1; i >= 0; i--) {
            if (isVowel(word.charAt(i))) return i;
        }
        return -1;
    }

    private static int findFirstVowel(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (isVowel(word.charAt(i))) return i;
        }
        return -1;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private static String checkSpecialCombinations(Object a, Object b) {
        String nameA = getBaseName(a).toLowerCase();
        String nameB = getBaseName(b).toLowerCase();

        if (SPECIAL_COMBINATIONS.containsKey(nameA)) {
            String result = SPECIAL_COMBINATIONS.get(nameA).get(nameB);
            if (result != null) return result;
        }
        if (SPECIAL_COMBINATIONS.containsKey(nameB)) {
            String result = SPECIAL_COMBINATIONS.get(nameB).get(nameA);
            if (result != null) return result;
        }
        return null;
    }

    private static String getBaseName(Object obj) {
        if (obj instanceof Item item) return item.localizedName;
        if (obj instanceof CustomItem customItem) return customItem.localizedName;
        if (obj instanceof Liquid liquid) return liquid.localizedName;
        if (obj instanceof CustomLiquid customLiquid) return customLiquid.localizedName;
        return "unknown";
    }

    private static class MaterialProfile {
        public String baseName;
        public boolean isMetal;
        public boolean isCrystal;
        public boolean isOrganic;
        public boolean isLiquid;

        public MaterialProfile(Object material) {
            this.baseName = getBaseName(material);

            if (material instanceof Item item) {
                initItem(item);
            } else if (material instanceof Liquid liquid) {
                initLiquid(liquid);
            } else if (material instanceof CustomItem customItem) {
                initCustomItem(customItem);
            } else if (material instanceof CustomLiquid customLiquid) {
                initCustomLiquid(customLiquid);
            }

            // Ensure liquids aren't classified as metals
            if (this.isLiquid) {
                this.isMetal = false;
            }
        }

        private void initItem(Item item) {
            this.isMetal = item.hardness > 4 && item.flammability < 0.3f;
            this.isCrystal = item.hardness > 5 && item.radioactivity < 0.2f;
            this.isOrganic = item.flammability > 0.5f || item.name.toLowerCase().contains("flesh");
            this.isLiquid = false;
        }

        private void initLiquid(Liquid liquid) {
            this.isLiquid = true;
            this.isOrganic = liquid.effect != StatusEffects.none || liquid.name.toLowerCase().contains("bio");
            this.isCrystal = liquid.temperature < 0.3f && liquid.heatCapacity > 0.4f;
            this.isMetal = false; // Explicitly set to false for liquids
        }

        private void initCustomItem(CustomItem item) {
            this.isMetal = item.hardness > 4 && item.flammability < 0.3f;
            this.isCrystal = item.hardness > 5 && item.radioactivity < 0.2f;
            this.isOrganic = item.flammability > 0.5f || item.name.toLowerCase().contains("flesh");
            this.isLiquid = false;
        }

        private void initCustomLiquid(CustomLiquid liquid) {
            this.isLiquid = true;
            this.isOrganic = liquid.effect != StatusEffects.none || liquid.name.toLowerCase().contains("bio");
            this.isCrystal = liquid.temperature < 0.3f && liquid.heatCapacity > 0.4f;
            this.isMetal = false; // Explicitly set to false for liquids
        }
    }
}
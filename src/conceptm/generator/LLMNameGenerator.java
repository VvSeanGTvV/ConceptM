package conceptm.generator;

import arc.struct.*;
import conceptm.world.type.*;
import mindustry.content.StatusEffects;
import mindustry.type.*;

import java.util.*;

public class LLMNameGenerator {
    // Special combinations database
    private static final Map<String, Map<String, String>> SPECIAL_COMBINATIONS = new HashMap<>();

    // Morpheme database organized by material type
    private static final Map<String, String[]> MORPHEME_DATABASE = new HashMap<>();
    private static final String[] PREFIXES = {
            "hyper", "neo", "ultra", "meta", "proto", "nano", "micro", "macro", "super"
    };
    private static final String[] SUFFIXES = {
            "ite", "ium", "ide", "ate", "ine", "ogen", "alloy", "synth", "form", "matter"
    };

    static {
        // Initialize special combinations
        Map<String, String> leadCombinations = new HashMap<>();
        leadCombinations.put("sand", "metaglass");
        SPECIAL_COMBINATIONS.put("lead", leadCombinations);

        Map<String, String> sporeCombinations = new HashMap<>();
        sporeCombinations.put("water", "sporepod");
        SPECIAL_COMBINATIONS.put("spore", sporeCombinations);

        Map<String, String> waterCombinations = new HashMap<>();
        waterCombinations.put("oil", "emulsion");
        SPECIAL_COMBINATIONS.put("water", waterCombinations);

        // Initialize morpheme database
        MORPHEME_DATABASE.put("metal", new String[]{"ferr", "steel", "chrom", "titan", "vanad", "nickel", "cobalt", "mangan"});
        MORPHEME_DATABASE.put("crystal", new String[]{"quartz", "diam", "sapph", "rub", "emerald", "topaz", "amethyst", "jade"});
        MORPHEME_DATABASE.put("organic", new String[]{"bio", "carbo", "cell", "protein", "lipid", "enzyme", "dna", "rna"});
        MORPHEME_DATABASE.put("liquid", new String[]{"aqua", "hydro", "flu", "liqu", "solv", "solve", "mix"});
    }

    public static String generateName(Object a, Object b) {
        // Check for special combinations first
        String specialName = checkSpecialCombinations(a, b);
        if (specialName != null) {
            return capitalize(specialName);
        }

        MaterialContext ctxA = new MaterialContext(a);
        MaterialContext ctxB = new MaterialContext(b);

        // Handle liquid combinations
        if (ctxA.isLiquid || ctxB.isLiquid) {
            return generateLiquidName(a, b, ctxA, ctxB);
        }

        // Handle solid combinations
        return generateSolidName(a, b, ctxA, ctxB);
    }

    private static String generateLiquidName(Object a, Object b, MaterialContext ctxA, MaterialContext ctxB) {
        String nameA = getBaseName(a);
        String nameB = getBaseName(b);

        // Determine which is liquid and which is other
        String liquidName = ctxA.isLiquid ? nameA : nameB;
        String otherName = ctxA.isLiquid ? nameB : nameA;
        MaterialContext otherCtx = ctxA.isLiquid ? ctxB : ctxA;

        // Special liquid stems and transformations
        Map<String, String> liquidStems = new HashMap<>();
        liquidStems.put("water", "aqua");
        liquidStems.put("oil", "oleo");
        liquidStems.put("cryofluid", "cryo");
        liquidStems.put("slag", "slag");
        liquidStems.put("spore", "sporo");
        liquidStems.put("acid", "acid");
        liquidStems.put("lava", "magma");

        // Special liquid combinations
        Map<String, Map<String, String>> liquidCombinations = new HashMap<>();
        Map<String, String> waterCombos = new HashMap<>();
        waterCombos.put("oil", "emulsion");
        waterCombos.put("slag", "obsidian");
        liquidCombinations.put("water", waterCombos);

        Map<String, String> oilCombos = new HashMap<>();
        oilCombos.put("spore", "biocrude");
        liquidCombinations.put("oil", oilCombos);

        // Check for special combinations first
        String specialName = checkLiquidCombinations(liquidName, otherName, liquidCombinations);
        if (specialName != null) {
            return capitalize(specialName);
        }

        String liquidStem = liquidStems.getOrDefault(liquidName.toLowerCase(),
                getMeaningfulStem(liquidName));

        if (otherCtx.isLiquid) {
            // Two liquids combination
            String otherLiquidStem = liquidStems.getOrDefault(otherName.toLowerCase(),
                    getMeaningfulStem(otherName));

            // Generate dynamic liquid name
            return generateLiquidCompoundName(liquidStem, otherLiquidStem);
        }
        else if (otherCtx.isOrganic) {
            // Organic + liquid combination
            return generateOrganicLiquidName(getMeaningfulStem(otherName), liquidStem);
        }
        else {
            // Solid + liquid combination
            return generateSolidLiquidName(getMeaningfulStem(otherName), liquidStem);
        }
    }

    private static String checkLiquidCombinations(String liquid1, String liquid2,
                                                  Map<String, Map<String, String>> combinations) {
        // Check both possible orders
        if (combinations.containsKey(liquid1)) {
            String result = combinations.get(liquid1).get(liquid2);
            if (result != null) return result;
        }
        if (combinations.containsKey(liquid2)) {
            return combinations.get(liquid2).get(liquid1);
        }
        return null;
    }

    private static String generateLiquidCompoundName(String stem1, String stem2) {
        // Try different blending techniques
        List<String> candidates = new ArrayList<>();

        // 1. Simple combination
        candidates.add(stem1 + stem2);

        // 2. Vowel-blended combination
        candidates.add(blendAtVowelTransition(stem1, stem2));

        // 3. Morpheme-based combination
        String morphemeCombo = findMorphemeCombo(stem1, stem2);
        if (morphemeCombo != null) {
            candidates.add(morphemeCombo);
        }

        // 4. Common liquid suffixes
        String[] liquidSuffixes = {"ium", "ate", "ine", "ide"};
        for (String suffix : liquidSuffixes) {
            candidates.add(stem1.substring(0, Math.min(3, stem1.length())) +
                    stem2.substring(0, Math.min(2, stem2.length())) +
                    suffix);
        }

        // Select best candidate
        return candidates.stream()
                .max(Comparator.comparingInt(LLMNameGenerator::scoreLiquidName))
                .orElse(stem1 + stem2 + "ium");
    }

    private static String generateOrganicLiquidName(String organicStem, String liquidStem) {
        // Special cases for organic+liquid
        if (liquidStem.equals("aqua") && organicStem.equals("sporo")) {
            return "Sporeculture";
        }

        // Try different patterns
        List<String> candidates = new ArrayList<>();
        candidates.add(organicStem + liquidStem);
        candidates.add(liquidStem + organicStem);
        candidates.add(blendAtVowelTransition(organicStem, liquidStem));

        // Add common biological suffixes
        String[] bioSuffixes = {"phage", "zyme", "some", "plasm"};
        for (String suffix : bioSuffixes) {
            candidates.add(organicStem.substring(0, Math.min(3, organicStem.length())) + suffix);
        }

        return candidates.stream()
                .max(Comparator.comparingInt(LLMNameGenerator::scoreLiquidName))
                .orElse(organicStem + liquidStem);
    }

    private static String generateSolidLiquidName(String solidStem, String liquidStem) {
        // Special cases for solid+liquid
        if (liquidStem.equals("aqua") && solidStem.equals("salt")) {
            return "Brine";
        }

        // Try different patterns
        List<String> candidates = new ArrayList<>();
        candidates.add(solidStem + liquidStem);
        candidates.add(liquidStem + solidStem);
        candidates.add(blendAtVowelTransition(solidStem, liquidStem));

        // Add common solution suffixes
        String[] solutionSuffixes = {"ate", "ite", "ide", "ium"};
        for (String suffix : solutionSuffixes) {
            candidates.add(solidStem.substring(0, Math.min(3, solidStem.length())) +
                    liquidStem.substring(0, Math.min(2, liquidStem.length())) +
                    suffix);
        }

        return candidates.stream()
                .max(Comparator.comparingInt(LLMNameGenerator::scoreLiquidName))
                .orElse(solidStem + liquidStem + "ite");
    }

    private static int scoreLiquidName(String name) {
        int score = 0;

        // Ideal length
        if (name.length() >= 4 && name.length() <= 8) score += 3;

        // Vowel-consonant balance
        int vowels = countVowels(name);
        if (vowels >= 2 && vowels <= name.length() - 2) score += 2;

        // Contains known morphemes
        if (containsKnownMorpheme(name)) score += 2;

        // Avoid awkward repetitions
        if (!name.matches(".*(.)\\1{2,}.*")) score += 1;

        return score;
    }

    private static String generateSolidName(Object a, Object b, MaterialContext ctxA, MaterialContext ctxB) {
        String nameA = getBaseName(a);
        String nameB = getBaseName(b);
        MaterialContext combinedCtx = combineContexts(ctxA, ctxB);

        // Try compound naming first
        String compoundName = tryCompoundName(nameA, nameB, combinedCtx);
        if (compoundName != null) {
            return capitalize(compoundName);
        }

        // Then try blending
        String blended = blendWords(getMeaningfulStem(nameA), getMeaningfulStem(nameB));
        if (isGoodName(blended)) {
            return capitalize(blended);
        }

        // Fall back to contextual generation
        return capitalize(generateContextualName(nameA, nameB, combinedCtx));
    }

    private static String blendWords(String a, String b) {
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;

        // Try to blend at natural vowel transitions
        int splitA = findLastVowel(a);
        int splitB = findFirstVowel(b);

        if (splitA > 0 && splitB > 0) {
            return a.substring(0, splitA) + b.substring(splitB);
        }

        // Default to first half + second half
        return a.substring(0, a.length()/2) + b.substring(b.length()/2);
    }

    private static int findLastVowel(String word) {
        for (int i = word.length()-1; i >= 0; i--) {
            if (isVowel(word.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static int findFirstVowel(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (isVowel(word.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static String getBaseName(Object obj) {
        if (obj instanceof Item item) return item.name;
        if (obj instanceof CustomItem customItem) return customItem.name;
        if (obj instanceof Liquid liquid) return liquid.name;
        if (obj instanceof CustomLiquid customLiquid) return customLiquid.name;
        return "unknown";
    }

    private static String getMeaningfulStem(String name) {
        if (name == null || name.isEmpty()) return "";
        name = name.toLowerCase().replaceAll("[^a-z]", "");

        // Remove common suffixes
        for (String suffix : SUFFIXES) {
            if (name.endsWith(suffix) && name.length() > suffix.length()) {
                name = name.substring(0, name.length() - suffix.length());
                break;
            }
        }

        // Take first 3-5 meaningful characters
        int length = Math.min(5, name.length());
        if (length > 3 && isVowel(name.charAt(length-1))) {
            length--; // Prefer ending with consonant
        }
        return name.substring(0, length);
    }

    private static boolean isGoodName(String name) {
        return name != null && name.length() >= 4 && name.length() <= 10;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private static String getLiquidStem(String liquidName) {
        // Special stems for common liquids
        Map<String, String> specialStems = new HashMap<>();
        specialStems.put("water", "aqua");
        specialStems.put("oil", "oleo");
        specialStems.put("slag", "slag");
        specialStems.put("cryofluid", "cryo");

        String lowerName = liquidName.toLowerCase();
        if (specialStems.containsKey(lowerName)) {
            return specialStems.get(lowerName);
        }

        // Default stem extraction
        return getMeaningfulStem(liquidName);
    }

    private static String checkSpecialCombinations(Object a, Object b) {
        String nameA = getBaseName(a).toLowerCase();
        String nameB = getBaseName(b).toLowerCase();

        // Check both combinations (A+B and B+A)
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

    private static String tryCompoundName(String nameA, String nameB, MaterialContext ctx) {
        // For metal + crystal combinations
        if ((ctx.isCrystalline && ctx.hardness > 4) ||
                (nameA.equalsIgnoreCase("lead") && nameB.equalsIgnoreCase("sand"))) {
            return nameA + nameB;
        }

        // For organic + liquid combinations
        if (ctx.isOrganic && ctx.isLiquid) {
            return nameA + "-" + nameB;
        }

        // For two metals
        if (ctx.hardness > 5 && !ctx.isOrganic && !ctx.isCrystalline) {
            return nameA + nameB + "alloy";
        }

        return null;
    }

    private static String advancedBlend(String nameA, String nameB) {
        // Clean names and get stems
        String stemA = getMeaningfulStem(nameA);
        String stemB = getMeaningfulStem(nameB);

        // Try different blending strategies
        List<String> candidates = new ArrayList<>();

        // 1. Simple combination of stems
        candidates.add(stemA + stemB);

        // 2. Vowel-consonant blended version
        candidates.add(blendAtVowelTransition(stemA, stemB));

        // 3. Common morpheme combination
        String morphemeCombo = findMorphemeCombo(stemA, stemB);
        if (morphemeCombo != null) {
            candidates.add(morphemeCombo);
        }

        // Return the best candidate
        return candidates.stream()
                .max(Comparator.comparingInt(c -> scoreName(c, stemA, stemB)))
                .orElse(stemA + stemB);
    }

    private static String findMorphemeCombo(String stemA, String stemB) {
        // Try to find a shared morpheme between the two stems
        for (String[] morphemes : MORPHEME_DATABASE.values()) {
            for (String morpheme : morphemes) {
                if (stemA.contains(morpheme) && stemB.contains(morpheme)) {
                    // Found shared morpheme - use it as bridge
                    return stemA.substring(0, stemA.indexOf(morpheme)) +
                            morpheme +
                            stemB.substring(stemB.indexOf(morpheme) + morpheme.length());
                }
            }
        }

        // Try to find any morpheme in either stem
        String morphemeA = findBestMorpheme(stemA);
        String morphemeB = findBestMorpheme(stemB);

        if (!morphemeA.isEmpty() && !morphemeB.isEmpty()) {
            // Combine using the found morphemes
            return morphemeA + morphemeB;
        }

        // No good morpheme combination found
        return null;
    }

    private static String findBestMorpheme(String word) {
        if (word == null || word.isEmpty()) return "";

        // Check against known morphemes (longest first)
        List<String> allMorphemes = new ArrayList<>();
        for (String[] morphemes : MORPHEME_DATABASE.values()) {
            allMorphemes.addAll(Arrays.asList(morphemes));
        }

        // Sort by length descending to find longest matches first
        allMorphemes.sort((a, b) -> Integer.compare(b.length(), a.length()));

        for (String morpheme : allMorphemes) {
            if (word.contains(morpheme)) {
                return morpheme;
            }
        }

        // Fall back to first 3 letters if no morpheme found
        return word.length() > 3 ? word.substring(0, 3) : word;
    }

    private static String blendAtVowelTransition(String a, String b) {
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;

        // Find last vowel in A
        int lastVowelA = -1;
        for (int i = a.length()-1; i >= 0; i--) {
            if (isVowel(a.charAt(i))) {
                lastVowelA = i;
                break;
            }
        }

        // Find first vowel in B
        int firstVowelB = -1;
        for (int i = 0; i < b.length(); i++) {
            if (isVowel(b.charAt(i))) {
                firstVowelB = i;
                break;
            }
        }

        // Blend at vowel points if found
        if (lastVowelA >= 0 && firstVowelB >= 0) {
            return a.substring(0, lastVowelA) + b.substring(firstVowelB);
        }

        // Default to simple combination
        return a + b;
    }

    private static int scoreName(String name, String stemA, String stemB) {
        int score = 0;

        // Length score (4-8 chars ideal)
        if (name.length() >= 4 && name.length() <= 8) score += 3;
        else if (name.length() > 8) score -= 1;

        // Contains both stems
        if (name.contains(stemA) && name.contains(stemB)) score += 2;

        // Vowel-consonant balance
        int vowels = countVowels(name);
        if (vowels >= 2 && vowels <= name.length() - 2) score += 2;

        // Known morphemes
        if (containsKnownMorpheme(name)) score += 1;

        return score;
    }

    // Contextual analyzer
    public static class MaterialContext {
        public float hardness;
        public float flammability;
        public float radioactivity;
        public float temperature;
        public boolean isGas;
        public boolean isLiquid;
        public boolean isOrganic;
        public boolean isCrystalline;

        public MaterialContext(Object material) {
            if (material instanceof Item item) {
                initItemProperties(item);
            } else if (material instanceof CustomItem customItem) {
                initCustomItemProperties(customItem);
            } else if (material instanceof Liquid liquid) {
                initLiquidProperties(liquid);
            } else if (material instanceof CustomLiquid customLiquid) {
                initCustomLiquidProperties(customLiquid);
            }
        }

        private void initItemProperties(Item item) {
            this.hardness = item.hardness;
            this.flammability = item.flammability;
            this.radioactivity = item.radioactivity;
            this.temperature = 0;
            this.isGas = false;
            this.isLiquid = false;
            this.isOrganic = item.flammability > 0.5f || item.localizedName.toLowerCase().contains("flesh");
            this.isCrystalline = item.hardness > 5 && item.radioactivity < 0.2f;
        }

        private void initCustomItemProperties(CustomItem item) {
            this.hardness = item.hardness;
            this.flammability = item.flammability;
            this.radioactivity = item.radioactivity;
            this.temperature = 0;
            this.isGas = false;
            this.isLiquid = false;
            this.isOrganic = item.flammability > 0.5f || item.localizedName.toLowerCase().contains("flesh");
            this.isCrystalline = item.hardness > 5 && item.radioactivity < 0.2f;
        }

        private void initLiquidProperties(Liquid liquid) {
            this.hardness = 0;
            this.flammability = liquid.flammability;
            this.radioactivity = 0;
            this.temperature = liquid.temperature;
            this.isGas = liquid.gas;
            this.isLiquid = true;
            this.isOrganic = liquid.effect != StatusEffects.none || liquid.localizedName.toLowerCase().contains("bio");
            this.isCrystalline = liquid.temperature < 0.3f && liquid.heatCapacity > 0.4f;
        }

        private void initCustomLiquidProperties(CustomLiquid liquid) {
            this.hardness = 0;
            this.flammability = liquid.flammability;
            this.radioactivity = 0;
            this.temperature = liquid.temperature;
            this.isGas = liquid.gas;
            this.isLiquid = true;
            this.isOrganic = liquid.effect != StatusEffects.none || liquid.localizedName.toLowerCase().contains("bio");
            this.isCrystalline = liquid.temperature < 0.3f && liquid.heatCapacity > 0.4f;
        }
    }

    private static MaterialContext combineContexts(MaterialContext a, MaterialContext b) {
        MaterialContext combined = new MaterialContext(null);
        combined.hardness = (a.hardness + b.hardness) / 2;
        combined.flammability = (a.flammability + b.flammability) / 2;
        combined.radioactivity = (a.radioactivity + b.radioactivity) / 2;
        combined.temperature = (a.temperature + b.temperature) / 2;
        combined.isGas = a.isGas || b.isGas;
        combined.isLiquid = a.isLiquid || b.isLiquid;
        combined.isOrganic = a.isOrganic || b.isOrganic;
        combined.isCrystalline = a.isCrystalline || b.isCrystalline;
        return combined;
    }

    private static String cleanName(String name) {
        if (name == null) return "";
        // Remove common suffixes and prefixes
        String cleaned = name.toLowerCase()
                .replaceAll("(ite|ium|ide|ate|ine|ogen|alloy|synth)$", "")
                .replaceAll("^(hyper|neo|ultra|meta|proto|nano)", "");
        // Remove non-alphabetic characters
        return cleaned.replaceAll("[^a-z]", "");
    }

    private static int findNaturalSplit(String word) {
        if (word.length() < 3) return word.length() / 2;
        // Find transition from consonant to vowel or vice versa
        for (int i = 1; i < word.length(); i++) {
            boolean prevVowel = isVowel(word.charAt(i-1));
            boolean currVowel = isVowel(word.charAt(i));
            if (prevVowel != currVowel) {
                return i;
            }
        }
        return word.length() / 2;
    }

    private static String simpleCombine(String a, String b) {
        int aLen = Math.min(3, a.length());
        int bLen = Math.min(3, b.length());
        return a.substring(0, aLen) + b.substring(0, bLen);
    }

    private static int scoreBlend(String word) {
        if (word == null || word.isEmpty()) return -1;
        int score = 0;

        // Ideal length
        if (word.length() >= 4 && word.length() <= 8) score += 3;

        // Vowel-consonant balance
        int vowels = countVowels(word);
        if (vowels >= 2 && vowels <= word.length() - 2) score += 2;

        // Known morphemes
        if (containsKnownMorpheme(word)) score += 2;

        return score;
    }

    private static int countVowels(String word) {
        int count = 0;
        for (char c : word.toCharArray()) {
            if (isVowel(c)) count++;
        }
        return count;
    }

    private static boolean containsKnownMorpheme(String word) {
        for (String[] morphemes : MORPHEME_DATABASE.values()) {
            for (String morpheme : morphemes) {
                if (word.contains(morpheme)) return true;
            }
        }
        return false;
    }

    private static String generateContextualName(String nameA, String nameB, MaterialContext ctx) {
        String base = selectBaseMorphemes(nameA, nameB, ctx);
        String prefix = selectPrefix(ctx);
        String suffix = selectSuffix(ctx);

        return capitalize(prefix + base + suffix);
    }

    private static String selectBaseMorphemes(String nameA, String nameB, MaterialContext ctx) {
        String category = ctx.isOrganic ? "organic" :
                ctx.isCrystalline ? "crystal" :
                        ctx.isGas ? "gas" :
                                ctx.hardness > 4 ? "metal" : "default";

        String[] morphemes = MORPHEME_DATABASE.getOrDefault(category, new String[0]);

        String morphemeA = findMorphemeInName(nameA, morphemes);
        String morphemeB = findMorphemeInName(nameB, morphemes);

        return morphemeA + morphemeB;
    }

    private static String findMorphemeInName(String name, String[] morphemes) {
        if (name == null || name.isEmpty()) return "";
        String lowerName = name.toLowerCase();
        for (String morpheme : morphemes) {
            if (lowerName.contains(morpheme)) {
                return morpheme;
            }
        }
        return getStem(name);
    }

    private static String getStem(String name) {
        String clean = cleanName(name);
        return clean.length() > 3 ? clean.substring(0, 3) : clean;
    }

    private static String selectPrefix(MaterialContext ctx) {
        if (ctx.radioactivity > 0.7f) return "hyper";
        if (ctx.temperature > 0.8f) return "pyro";
        if (ctx.temperature < 0.2f) return "cryo";
        if (ctx.isOrganic) return "bio";
        return Math.random() > 0.7 ? PREFIXES[(int)(Math.random() * PREFIXES.length)] : "";
    }

    private static String selectSuffix(MaterialContext ctx) {
        if (ctx.radioactivity > 0.7f) return "on";
        if (ctx.flammability > 0.5f) return "ene";
        if (ctx.hardness > 7f) return "ite";
        if (ctx.hardness > 4f) return "ium";
        if (ctx.isLiquid && ctx.hardness > 3f) return "alloy";
        if (ctx.isGas) return "vapor";
        return SUFFIXES[(int)(Math.random() * SUFFIXES.length)];
    }

    private static boolean isVowel(char c) {
        return "aeiou".indexOf(Character.toLowerCase(c)) != -1;
    }
}
package conceptm.graphics;

import arc.graphics.Color;

public class BlendColor {
    public static Color blendColorsHSL(Color c1, Color c2) {
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

    public static Color blendColorsVibrant(Color c1, Color c2) {
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

    public static float averageAngles(float a1, float a2) {
        // Special handling for hue (circular value)
        float diff = Math.abs(a1 - a2);
        if (diff > 0.5f) {
            return ((a1 + a2 + 1f) / 2f) % 1f;
        }
        return (a1 + a2) / 2f;
    }

    // RGB to HSL conversion
    public static float[] rgbToHsl(float r, float g, float b) {
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
    public static float[] hslToRgb(float h, float s, float l) {
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

    public static float hueToRgb(float p, float q, float t) {
        if (t < 0f) t += 1f;
        if (t > 1f) t -= 1f;
        if (t < 1f/6f) return p + (q - p) * 6f * t;
        if (t < 1f/2f) return q;
        if (t < 2f/3f) return p + (q - p) * (2f/3f - t) * 6f;
        return p;
    }
}

package conceptm.world.type;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.ObjectSet;
import arc.util.*;
import conceptm.generator.PuddleCustom;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.gen.Puddle;
import mindustry.graphics.Drawf;
import mindustry.type.*;
import mindustry.world.meta.*;

import java.util.*;

import static conceptm.graphics.BlendColor.blendColorsVibrant;
import static conceptm.world.type.CustomItem.nameRegistry;
import static mindustry.entities.Puddles.maxLiquid;
import static mindustry.type.Liquid.*;

public class CustomLiquid extends CustomUnlockable {

    public Liquid liq1, liq2;
    public CustomLiquid liq1c, liq2c;

    public Item item0;
    public CustomItem item0c;

    protected static final Rand rand = new Rand();

    /** If true, this fluid is treated as a gas (and does not create puddles) */
    public boolean gas = false;
    /** Color used in pipes and on the ground. */
    public Color color;
    /** Color of this liquid in gas form. */
    public Color gasColor = Color.lightGray.cpy();
    /** Color used in bars. */
    public @Nullable Color barColor;
    /** Color used to draw lights. Note that the alpha channel is used to dictate brightness. */
    public Color lightColor = Color.clear.cpy();
    /** 0-1, 0 is completely not flammable, anything above that may catch fire when exposed to heat, 0.5+ is very flammable. */
    public float flammability;
    /** temperature: 0.5 is 'room' temperature, 0 is very cold, 1 is molten hot */
    public float temperature = 0.5f;
    /** how much heat this liquid can store. 0.4=water (decent), anything lower is probably less dense and bad at cooling. */
    public float heatCapacity = 0.5f;
    /** how thick this liquid is. 0.5=water (relatively viscous), 1 would be something like tar (very slow). */
    public float viscosity = 0.5f;
    /** how prone to exploding this liquid is, when heated. 0 = nothing, 1 = nuke */
    public float explosiveness;
    /** whether this fluid reacts in blocks at all (e.g. slag with water) */
    public boolean blockReactive = true;
    /** if false, this liquid cannot be a coolant */
    public boolean coolant = true;
    /** if true, this liquid can move through blocks as a puddle. */
    public boolean moveThroughBlocks = false;
    /** if true, this liquid can be incinerated in the incinerator block. */
    public boolean incinerable = true;
    /** The associated status effect. */
    public StatusEffect effect = StatusEffects.none;
    /** Effect shown in puddles. */
    public Effect particleEffect = Fx.none;
    /** Particle effect rate spacing in ticks. */
    public float particleSpacing = 60f;
    /** Temperature at which this liquid vaporizes. This isn't just boiling. */
    public float boilPoint = 2f;
    /** If true, puddle size is capped. */
    public boolean capPuddles = true;
    /** Effect when this liquid vaporizes. */
    public Effect vaporEffect = Fx.vapor;
    /** Liquids this puddle can stay on, e.g. oil on water. */
    public ObjectSet<CustomLiquid> canStayOn = new ObjectSet<>();

    public CustomLiquid(String name, Object a0, Object b0){

        var aflame = (a0 instanceof Liquid item) ? item.flammability : (a0 instanceof CustomLiquid comboItem) ? comboItem.flammability : 0;
        var bflame = (b0 instanceof Liquid item) ? item.flammability : (b0 instanceof CustomLiquid comboItem) ? comboItem.flammability : 0;
        var atemp = (a0 instanceof Liquid item) ? item.temperature : (a0 instanceof CustomLiquid comboItem) ? comboItem.temperature : 0;
        var btemp = (b0 instanceof Liquid item) ? item.temperature : (b0 instanceof CustomLiquid comboItem) ? comboItem.temperature : 0;
        var aheat = (a0 instanceof Liquid item) ? item.heatCapacity : (a0 instanceof CustomLiquid comboItem) ? comboItem.heatCapacity : 0;
        var bheat = (b0 instanceof Liquid item) ? item.heatCapacity : (b0 instanceof CustomLiquid comboItem) ? comboItem.heatCapacity : 0;
        var aexplode = (a0 instanceof Liquid item) ? item.explosiveness : (a0 instanceof CustomLiquid comboItem) ? comboItem.explosiveness : 0;
        var bexplode = (b0 instanceof Liquid item) ? item.explosiveness : (b0 instanceof CustomLiquid comboItem) ? comboItem.explosiveness : 0;
        var aboil = (a0 instanceof Liquid item) ? item.boilPoint : (a0 instanceof CustomLiquid comboItem) ? comboItem.boilPoint : 1f;
        var bboil = (b0 instanceof Liquid item) ? item.boilPoint : (b0 instanceof CustomLiquid comboItem) ? comboItem.boilPoint : 1f;
        var avis = (a0 instanceof Liquid item) ? item.viscosity : (a0 instanceof CustomLiquid comboItem) ? comboItem.viscosity : 0;
        var bvis = (b0 instanceof Liquid item) ? item.viscosity : (b0 instanceof CustomLiquid comboItem) ? comboItem.viscosity : 0;

        // Combine numeric properties (average them)
        float div = 1.5f;
        this.temperature = (atemp + btemp) / div;
        this.heatCapacity = (aheat + bheat) / div;
        this.flammability = (aflame + bflame) / div;
        this.explosiveness = (aexplode + bexplode) / div;
        this.boilPoint = (aboil + bboil);

        // PREVENT PUDDLE LOOP
        this.viscosity = Math.min(((avis + bvis) / div), 1f);

        liq1 = (a0 instanceof Liquid item) ? item : null;
        liq2 = (b0 instanceof Liquid item) ? item : null;

        liq1c = (a0 instanceof CustomLiquid item) ? item : null;
        liq2c = (b0 instanceof CustomLiquid item) ? item : null;

        /*List<Boolean> boolList = Arrays.asList(
                item1 != null && item1.lowPriority,
                item2 != null && item2.lowPriority,
                item1c != null && item1c.lowPriority,
                item2c != null && item2c.lowPriority
        );
        this.lowPriority = boolList.stream().reduce(false, Boolean::logicalOr);


        */

        List<Boolean> boolList = Arrays.asList(
                liq1 != null && liq1.gas,
                liq1c != null && liq1c.gas
        );
        this.gas = boolList.stream().reduce(false, Boolean::logicalOr);

        this.color = this.barColor = blendColorsVibrant(
                (liq1 != null) ? liq1.color : (liq1c != null) ? liq1c.color : Color.lightGray.cpy(),
                (liq2 != null) ? liq2.color : (liq2c != null) ? liq2c.color : Color.lightGray.cpy()
        );

        String an = (a0 instanceof Liquid ai) ? ai.name : (a0 instanceof CustomLiquid ac) ? ac.name : "";
        String bn = (b0 instanceof Liquid bi) ? bi.name : (b0 instanceof CustomLiquid bc) ? bc.name : "";

        this.localizedName = name;
        this.name = nameRegistry.generateRegistryKeyWithBracket(an, bn, "{", "}");
        createIcons(liq1, liq2, liq1c, liq2c);
        setStats();

        short i0d = (liq1 != null) ? liq1.id : (liq1c != null) ? liq1c.id : (short) 0;
        short i1d = (liq2 != null) ? liq2.id : (liq2c != null) ? liq2c.id : (short) 0;
        this.id = (short) (i0d + i1d);
    }

    public CustomLiquid(Object item){
        float charge = (item instanceof Item item1) ? item1.charge : (item instanceof CustomItem item1c) ? item1c.charge : 0f;
        float cost = (item instanceof Item item1) ? item1.cost : (item instanceof CustomItem item1c) ? item1c.cost : 1f;
        float flammability = (item instanceof Item item1) ? item1.flammability : (item instanceof CustomItem item1c) ? item1c.flammability : 0f;
        float explosiveness = (item instanceof Item item1) ? item1.explosiveness : (item instanceof CustomItem item1c) ? item1c.explosiveness : 0f;
        float radioactivity = (item instanceof Item item1) ? item1.radioactivity : (item instanceof CustomItem item1c) ? item1c.radioactivity : 0f;
        int hardness = (item instanceof Item item1) ? item1.hardness : (item instanceof CustomItem item1c) ? item1c.hardness : 0;

        Color color1 = (item instanceof Item item1) ? item1.color : (item instanceof CustomItem item1c) ? item1c.color : Color.white.cpy();

        this.temperature = 0.9f + (charge * 0.1f);
        this.heatCapacity = Math.min(1f, 0.3f + cost * 0.1f);
        this.flammability = flammability * 0.8f;
        this.explosiveness = explosiveness * 1.2f;
        float boilPoint1 = (this.temperature + (hardness * 0.03f) - (charge * 0.2f));
        this.boilPoint = boilPoint1;

        // PREVENT PUDDLE LOOP
        this.viscosity = 1f - (hardness / 10f);

        // ADJUSTABLE
        if (temperature >= boilPoint) {
            this.explosiveness += 0.2f; // or scale it by (temperature - boilPoint)
        }

        // COLOR ADJUSTER
        Color liquidColor = color1.cpy(); // start with base color

        /*if(charge > 0.5f){
            liquidColor.lerp(Color.blue, (charge - 0.5f) * 2f); // add blue if highly charged
        }

        if(radioactivity > 0.3f){
            liquidColor.lerp(Color.green, (radioactivity)); // add green tint if radioactive
        }

        if(temperature >= 0.8f){
            liquidColor.lerp(Color.orange, (temperature - 0.8f) * 5f); // molten glow
        }

        if (temperature >= boilPoint) {
            liquidColor.lerp(Color.white, (temperature - boilPoint) * 2f);
        }*/

        if(temperature >= boilPoint){
            gas = true;
            viscosity = 0.1f;
            heatCapacity *= 0.5f;
            this.explosiveness += 0.2f;
            this.flammability += 0.1f;
            liquidColor.a = 0.4f; // semi-transparent
        }

        if(gas && temperature < boilPoint){
            gas = false;
            this.temperature = 0.9f + (charge * 0.1f);
            this.heatCapacity = Math.min(1f, 0.3f + cost * 0.1f);
            this.flammability = flammability * 0.8f;
            this.explosiveness = explosiveness * 1.2f;
            this.boilPoint = boilPoint1;
        }

        String localizedName = (item instanceof Item item1) ? item1.localizedName : (item instanceof CustomItem item1c) ? item1c.localizedName : "unknown";
        String name = (item instanceof Item item1) ? item1.name : (item instanceof CustomItem item1c) ? item1c.name : "unknown";
        short id = (item instanceof Item item1) ? item1.id : (item instanceof CustomItem item1c) ? item1c.id : 0;

        if (item instanceof Item item1) item0 = item1;
        if (item instanceof CustomItem item1) item0c = item1;

        this.color = this.barColor = liquidColor;
        this.localizedName = localizedName + " Liquid";
        this.name = "liquid-" + name;
        fullIcon = (gas) ? Core.atlas.find("concept-m" + "-gas-template") : Core.atlas.find("concept-m" + "-liquid-template");
        //createIcons(liq1, liq2, liq1c, liq2c);
        setStats();

        this.id = (short) (id + 10);
    }

    public CustomLiquid(Object item1, Object item2) {
        this(
                nameRegistry.getNameFor(item1, item2, true),
                item1,
                item2
        );
    }

    public boolean willBoil(){
        return Attribute.heat.env() >= boilPoint;
    }

    public void drawPuddle(PuddleCustom puddle){
        float amount = puddle.amount, x = puddle.x, y = puddle.y;
        float f = Mathf.clamp(amount / (maxLiquid / 1.5f));
        float smag = puddle.tile.floor().isLiquid ? 0.8f : 0f, sscl = 25f;

        Draw.color(Tmp.c1.set(color).shiftValue(-0.05f));
        Fill.circle(x + Mathf.sin(Time.time + id * 532, sscl, smag), y + Mathf.sin(Time.time + id * 53, sscl, smag), f * 8f);

        float length = f * 6f;
        rand.setSeed(id);
        for(int i = 0; i < 3; i++){
            Tmp.v1.trns(rand.random(360f), rand.random(length));
            float vx = x + Tmp.v1.x, vy = y + Tmp.v1.y;

            Fill.circle(
                    vx + Mathf.sin(Time.time + i * 532, sscl, smag),
                    vy + Mathf.sin(Time.time + i * 53, sscl, smag),
                    f * 5f);
        }

        Draw.color();

        if(lightColor.a > 0.001f && f > 0){
            Drawf.light(x, y, 30f * f, lightColor, color.a * f * 0.8f);
        }
    }

    public int getAnimationFrame(){
        return (int)(Time.time / (gas ? animationScaleGas : animationScaleLiquid) * animationFrames + id*5) % animationFrames;
    }

    public void createIcons(Liquid item0, Liquid item1, CustomLiquid customItem0, CustomLiquid customItem1) {
        // Get the pixmaps for both items
        var icon1 = (item0 != null) ? item0.fullIcon : (customItem0 != null) ? customItem0.fullIcon : Core.atlas.find("white");
        var icon2 = (item1  != null) ? item1.fullIcon : (customItem1 != null) ? customItem1.fullIcon : Core.atlas.find("white");
        fullIcon = icon1;
    }

    public void setStats(){
        stats.addPercent(Stat.explosiveness, explosiveness);
        stats.addPercent(Stat.flammability, flammability);
        stats.addPercent(Stat.temperature, temperature);
        stats.addPercent(Stat.heatCapacity, heatCapacity);
        stats.addPercent(Stat.viscosity, viscosity);
    }
}

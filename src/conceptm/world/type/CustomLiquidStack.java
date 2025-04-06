package conceptm.world.type;

public class CustomLiquidStack {
    public CustomLiquid liq;
    public float amount = 0;

    public CustomLiquidStack(CustomLiquid liq, float amount){
        this.liq = liq;
        this.amount = amount;
    }

    public CustomLiquidStack set(CustomLiquid liq, float amount){
        this.liq = liq;
        this.amount = amount;
        return this;
    }
}

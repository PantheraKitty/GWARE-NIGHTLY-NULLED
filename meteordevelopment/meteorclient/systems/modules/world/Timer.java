package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Timer extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> multiplier;
   public static final double OFF = 1.0D;
   private double override;

   public Timer() {
      super(Categories.World, "timer", "Changes the speed of everything in your game.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.multiplier = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("multiplier")).description("The timer multiplier amount.")).defaultValue(1.0D).min(0.1D).sliderMin(0.1D).build());
      this.override = 1.0D;
   }

   public double getMultiplier() {
      return this.override != 1.0D ? this.override : (this.isActive() ? (Double)this.multiplier.get() : 1.0D);
   }

   public void setOverride(double override) {
      this.override = override;
   }
}

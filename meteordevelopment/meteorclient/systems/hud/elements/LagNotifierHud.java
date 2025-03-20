package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.TickRate;

public class LagNotifierHud extends HudElement {
   public static final HudElementInfo<LagNotifierHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgScale;
   private final SettingGroup sgBackground;
   private final Setting<Boolean> shadow;
   private final Setting<SettingColor> textColor;
   private final Setting<SettingColor> color1;
   private final Setting<SettingColor> color2;
   private final Setting<SettingColor> color3;
   private final Setting<Integer> border;
   private final Setting<Boolean> customScale;
   private final Setting<Double> scale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;

   public LagNotifierHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgScale = this.settings.createGroup("Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shadow")).description("Text shadow.")).defaultValue(true)).build());
      this.textColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("text-color")).description("A.")).defaultValue(new SettingColor()).build());
      this.color1 = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color-1")).description("First color.")).defaultValue(new SettingColor(255, 255, 5)).build());
      this.color2 = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color-2")).description("Second color.")).defaultValue(new SettingColor(235, 158, 52)).build());
      this.color3 = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color-3")).description("Third color.")).defaultValue(new SettingColor(225, 45, 45)).build());
      this.border = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("border")).description("How much space to add around the element.")).defaultValue(0)).build());
      this.customScale = this.sgScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-scale")).description("Applies custom text scale rather than the global one.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgScale;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("Custom scale.");
      Setting var10003 = this.customScale;
      Objects.requireNonNull(var10003);
      this.scale = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue(1.0D).min(0.5D).sliderRange(0.5D, 3.0D).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var1 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var1.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
   }

   public void setSize(double width, double height) {
      super.setSize(width + (double)((Integer)this.border.get() * 2), height + (double)((Integer)this.border.get() * 2));
   }

   public void render(HudRenderer renderer) {
      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), (Color)this.backgroundColor.get());
      }

      if (this.isInEditor()) {
         this.render(renderer, "4.3", (Color)this.color3.get());
      } else {
         float timeSinceLastTick = TickRate.INSTANCE.getTimeSinceLastTick();
         if (timeSinceLastTick >= 1.0F) {
            Color color;
            if (timeSinceLastTick > 10.0F) {
               color = (Color)this.color3.get();
            } else if (timeSinceLastTick > 3.0F) {
               color = (Color)this.color2.get();
            } else {
               color = (Color)this.color1.get();
            }

            this.render(renderer, String.format("%.1f", timeSinceLastTick), color);
         }

      }
   }

   private void render(HudRenderer renderer, String right, Color rightColor) {
      double x = (double)(this.x + (Integer)this.border.get());
      double y = (double)(this.y + (Integer)this.border.get());
      double x2 = renderer.text("Time since last tick ", x, y, (Color)this.textColor.get(), (Boolean)this.shadow.get(), this.getScale());
      x2 = renderer.text(right, x2, y, rightColor, (Boolean)this.shadow.get(), this.getScale());
      this.setSize(x2 - x, renderer.textHeight((Boolean)this.shadow.get(), this.getScale()));
   }

   private double getScale() {
      return (Boolean)this.customScale.get() ? (Double)this.scale.get() : -1.0D;
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "lag-notifier", "Displays if the server is lagging in ticks.", LagNotifierHud::new);
   }
}

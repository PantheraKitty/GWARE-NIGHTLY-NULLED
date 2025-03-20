package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_3532;

public class CompassHud extends HudElement {
   public static final HudElementInfo<CompassHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgTextScale;
   private final SettingGroup sgBackground;
   private final Setting<CompassHud.Mode> mode;
   private final Setting<Double> scale;
   private final Setting<SettingColor> colorNorth;
   private final Setting<SettingColor> colorOther;
   private final Setting<Boolean> shadow;
   private final Setting<Integer> border;
   private final Setting<Boolean> customTextScale;
   private final Setting<Double> textScale;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;

   public CompassHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgTextScale = this.settings.createGroup("Text Scale");
      this.sgBackground = this.settings.createGroup("Background");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("type")).description("Which type of direction information to show.")).defaultValue(CompassHud.Mode.Axis)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale.")).defaultValue(1.0D).min(1.0D).sliderRange(1.0D, 5.0D).onChanged((aDouble) -> {
         this.calculateSize();
      })).build());
      this.colorNorth = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color-north")).description("Color of north.")).defaultValue(new SettingColor(225, 45, 45)).build());
      this.colorOther = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color-north")).description("Color of other directions.")).defaultValue(new SettingColor()).build());
      this.shadow = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("shadow")).description("Text shadow.")).defaultValue(false)).build());
      this.border = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("border")).description("How much space to add around the element.")).defaultValue(0)).onChanged((integer) -> {
         this.calculateSize();
      })).build());
      this.customTextScale = this.sgTextScale.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-text-scale")).description("Applies custom text scale rather than the global one.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgTextScale;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("text-scale")).description("Custom text scale.");
      Setting var10003 = this.customTextScale;
      Objects.requireNonNull(var10003);
      this.textScale = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue(1.0D).min(0.5D).sliderRange(0.5D, 3.0D).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      var10001 = this.sgBackground;
      ColorSetting.Builder var1 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var1.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.calculateSize();
   }

   public void setSize(double width, double height) {
      super.setSize(width + (double)((Integer)this.border.get() * 2), height + (double)((Integer)this.border.get() * 2));
   }

   private void calculateSize() {
      this.setSize(100.0D * (Double)this.scale.get(), 100.0D * (Double)this.scale.get());
   }

   public void render(HudRenderer renderer) {
      double x = (double)this.x + (double)this.getWidth() / 2.0D;
      double y = (double)this.y + (double)this.getHeight() / 2.0D;
      double pitch = this.isInEditor() ? 120.0D : (double)class_3532.method_15363(MeteorClient.mc.field_1724.method_36455() + 30.0F, -90.0F, 90.0F);
      pitch = Math.toRadians(pitch);
      double yaw = this.isInEditor() ? 180.0D : (double)class_3532.method_15393(MeteorClient.mc.field_1724.method_36454());
      yaw = Math.toRadians(yaw);
      CompassHud.Direction[] var10 = CompassHud.Direction.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         CompassHud.Direction direction = var10[var12];
         String axis = this.mode.get() == CompassHud.Mode.Axis ? direction.getAxis() : direction.name();
         renderer.text(axis, x + this.getX(direction, yaw) - renderer.textWidth(axis, (Boolean)this.shadow.get(), this.getTextScale()) / 2.0D, y + this.getY(direction, yaw, pitch) - renderer.textHeight((Boolean)this.shadow.get(), this.getTextScale()) / 2.0D, direction == CompassHud.Direction.N ? (Color)this.colorNorth.get() : (Color)this.colorOther.get(), (Boolean)this.shadow.get(), this.getTextScale());
      }

      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), (Color)this.backgroundColor.get());
      }

   }

   private double getX(CompassHud.Direction direction, double yaw) {
      return Math.sin(this.getPos(direction, yaw)) * (Double)this.scale.get() * 40.0D;
   }

   private double getY(CompassHud.Direction direction, double yaw, double pitch) {
      return Math.cos(this.getPos(direction, yaw)) * Math.sin(pitch) * (Double)this.scale.get() * 40.0D;
   }

   private double getPos(CompassHud.Direction direction, double yaw) {
      return yaw + (double)direction.ordinal() * 3.141592653589793D / 2.0D;
   }

   private double getTextScale() {
      return (Boolean)this.customTextScale.get() ? (Double)this.textScale.get() : -1.0D;
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "compass", "Displays a compass.", CompassHud::new);
   }

   public static enum Mode {
      Direction,
      Axis;

      // $FF: synthetic method
      private static CompassHud.Mode[] $values() {
         return new CompassHud.Mode[]{Direction, Axis};
      }
   }

   private static enum Direction {
      N("Z-"),
      W("X-"),
      S("Z+"),
      E("X+");

      private final String axis;

      private Direction(String axis) {
         this.axis = axis;
      }

      public String getAxis() {
         return this.axis;
      }

      // $FF: synthetic method
      private static CompassHud.Direction[] $values() {
         return new CompassHud.Direction[]{N, W, S, E};
      }
   }
}

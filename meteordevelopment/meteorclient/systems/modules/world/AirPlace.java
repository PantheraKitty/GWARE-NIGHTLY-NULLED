package meteordevelopment.meteorclient.systems.modules.world;

import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1826;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_2846;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class AirPlace extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRange;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> grimBypass;
   private final Setting<Boolean> customRange;
   private final Setting<Double> range;
   private class_239 hitResult;

   public AirPlace() {
      super(Categories.Player, "air-place", "Places a block where your crosshair is pointing at.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRange = this.settings.createGroup("Range");
      this.render = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders a block overlay where the obsidian will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The color of the sides of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 10)).build());
      this.lineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The color of the lines of the blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 255)).build());
      this.grimBypass = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("grim-bypass")).description("Bypass for GrimAC.")).defaultValue(false)).build());
      this.customRange = this.sgRange.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-range")).description("Use custom range for air place.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgRange;
      DoubleSetting.Builder var10002 = (DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("Custom range to place at.");
      Setting var10003 = this.customRange;
      Objects.requireNonNull(var10003);
      this.range = var10001.add(((DoubleSetting.Builder)var10002.visible(var10003::get)).defaultValue(5.0D).min(0.0D).sliderMax(6.0D).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      double r = (Boolean)this.customRange.get() ? (Double)this.range.get() : this.mc.field_1724.method_55754();
      this.hitResult = this.mc.method_1560().method_5745(r, 0.0F, false);
      class_239 var5 = this.hitResult;
      if (var5 instanceof class_3965) {
         class_3965 blockHitResult = (class_3965)var5;
         if (this.mc.field_1724.method_6047().method_7909() instanceof class_1747 || this.mc.field_1724.method_6047().method_7909() instanceof class_1826) {
            if (this.mc.field_1690.field_1904.method_1434() && BlockUtils.canPlace(blockHitResult.method_17777())) {
               class_1268 hand = class_1268.field_5808;
               if ((Boolean)this.grimBypass.get()) {
                  this.mc.method_1562().method_52787(new class_2846(class_2847.field_12969, new class_2338(0, 0, 0), class_2350.field_11033));
                  hand = class_1268.field_5810;
               }

               BlockUtils.place(blockHitResult.method_17777(), hand, this.mc.field_1724.method_31548().field_7545, false, 0, true, true, false);
               if ((Boolean)this.grimBypass.get()) {
                  this.mc.method_1562().method_52787(new class_2846(class_2847.field_12969, new class_2338(0, 0, 0), class_2350.field_11033));
               }
            }

            return;
         }
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      class_239 var3 = this.hitResult;
      if (var3 instanceof class_3965) {
         class_3965 blockHitResult = (class_3965)var3;
         if (this.mc.field_1687.method_8320(blockHitResult.method_17777()).method_45474() && (this.mc.field_1724.method_6047().method_7909() instanceof class_1747 || this.mc.field_1724.method_6047().method_7909() instanceof class_1826) && (Boolean)this.render.get()) {
            event.renderer.box((class_2338)blockHitResult.method_17777(), (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
            return;
         }
      }

   }
}

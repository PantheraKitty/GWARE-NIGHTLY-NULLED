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
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class ArmorHud extends HudElement {
   public static final HudElementInfo<ArmorHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgDurability;
   private final SettingGroup sgBackground;
   private final Setting<ArmorHud.Orientation> orientation;
   private final Setting<Boolean> flipOrder;
   private final Setting<Double> scale;
   private final Setting<Integer> border;
   private final Setting<ArmorHud.Durability> durability;
   private final Setting<SettingColor> durabilityColor;
   private final Setting<Boolean> durabilityShadow;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;

   public ArmorHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgDurability = this.settings.createGroup("Durability");
      this.sgBackground = this.settings.createGroup("Background");
      this.orientation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("orientation")).description("How to display armor.")).defaultValue(ArmorHud.Orientation.Horizontal)).onChanged((val) -> {
         this.calculateSize();
      })).build());
      this.flipOrder = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("flip-order")).description("Flips the order of armor items.")).defaultValue(true)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale.")).defaultValue(2.0D).onChanged((aDouble) -> {
         this.calculateSize();
      })).min(1.0D).sliderRange(1.0D, 5.0D).build());
      this.border = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("border")).description("How much space to add around the element.")).defaultValue(0)).onChanged((integer) -> {
         this.calculateSize();
      })).build());
      this.durability = this.sgDurability.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("durability")).description("How to display armor durability.")).defaultValue(ArmorHud.Durability.Bar)).onChanged((durability1) -> {
         this.calculateSize();
      })).build());
      this.durabilityColor = this.sgDurability.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("durability-color")).description("Color of the text.")).visible(() -> {
         return this.durability.get() == ArmorHud.Durability.Total || this.durability.get() == ArmorHud.Durability.Percentage;
      })).defaultValue(new SettingColor()).build());
      this.durabilityShadow = this.sgDurability.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("durability-shadow")).description("Text shadow.")).visible(() -> {
         return this.durability.get() == ArmorHud.Durability.Total || this.durability.get() == ArmorHud.Durability.Percentage;
      })).defaultValue(true)).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgBackground;
      ColorSetting.Builder var10002 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      Setting var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.calculateSize();
   }

   public void setSize(double width, double height) {
      super.setSize(width + (double)((Integer)this.border.get() * 2), height + (double)((Integer)this.border.get() * 2));
   }

   private void calculateSize() {
      switch(((ArmorHud.Orientation)this.orientation.get()).ordinal()) {
      case 0:
         this.setSize(16.0D * (Double)this.scale.get() * 4.0D + 8.0D, 16.0D * (Double)this.scale.get());
         break;
      case 1:
         this.setSize(16.0D * (Double)this.scale.get(), 16.0D * (Double)this.scale.get() * 4.0D + 8.0D);
      }

   }

   public void render(HudRenderer renderer) {
      double x = (double)this.x;
      double y = (double)this.y;
      int slot = (Boolean)this.flipOrder.get() ? 3 : 0;

      for(int position = 0; position < 4; ++position) {
         class_1799 itemStack = this.getItem(slot);
         double armorX;
         double armorY;
         if (this.orientation.get() == ArmorHud.Orientation.Vertical) {
            armorX = x;
            armorY = y + (double)(position * 18) * (Double)this.scale.get();
         } else {
            armorX = x + (double)(position * 18) * (Double)this.scale.get();
            armorY = y;
         }

         renderer.item(itemStack, (int)armorX, (int)armorY, ((Double)this.scale.get()).floatValue(), itemStack.method_7963() && this.durability.get() == ArmorHud.Durability.Bar);
         if (itemStack.method_7963() && !this.isInEditor() && this.durability.get() != ArmorHud.Durability.Bar && this.durability.get() != ArmorHud.Durability.None) {
            String var10000;
            switch(((ArmorHud.Durability)this.durability.get()).ordinal()) {
            case 2:
               var10000 = Integer.toString(itemStack.method_7936() - itemStack.method_7919());
               break;
            case 3:
               var10000 = Integer.toString(Math.round((float)(itemStack.method_7936() - itemStack.method_7919()) * 100.0F / (float)itemStack.method_7936()));
               break;
            default:
               var10000 = "err";
            }

            String message = var10000;
            double messageWidth = renderer.textWidth(message);
            if (this.orientation.get() == ArmorHud.Orientation.Vertical) {
               armorX = x + 8.0D * (Double)this.scale.get() - messageWidth / 2.0D;
               armorY = y + (double)(18 * position) * (Double)this.scale.get() + (18.0D * (Double)this.scale.get() - renderer.textHeight());
            } else {
               armorX = x + (double)(18 * position) * (Double)this.scale.get() + 8.0D * (Double)this.scale.get() - messageWidth / 2.0D;
               armorY = y + ((double)this.getHeight() - renderer.textHeight());
            }

            renderer.text(message, armorX, armorY, (Color)this.durabilityColor.get(), (Boolean)this.durabilityShadow.get());
         }

         if ((Boolean)this.flipOrder.get()) {
            --slot;
         } else {
            ++slot;
         }
      }

      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), (Color)this.backgroundColor.get());
      }

   }

   private class_1799 getItem(int i) {
      if (this.isInEditor()) {
         class_1799 var10000;
         switch(i) {
         case 1:
            var10000 = class_1802.field_22029.method_7854();
            break;
         case 2:
            var10000 = class_1802.field_22028.method_7854();
            break;
         case 3:
            var10000 = class_1802.field_22027.method_7854();
            break;
         default:
            var10000 = class_1802.field_22030.method_7854();
         }

         return var10000;
      } else {
         return MeteorClient.mc.field_1724.method_31548().method_7372(i);
      }
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "armor", "Displays your armor.", ArmorHud::new);
   }

   public static enum Orientation {
      Horizontal,
      Vertical;

      // $FF: synthetic method
      private static ArmorHud.Orientation[] $values() {
         return new ArmorHud.Orientation[]{Horizontal, Vertical};
      }
   }

   public static enum Durability {
      None,
      Bar,
      Total,
      Percentage;

      // $FF: synthetic method
      private static ArmorHud.Durability[] $values() {
         return new ArmorHud.Durability[]{None, Bar, Total, Percentage};
      }
   }
}

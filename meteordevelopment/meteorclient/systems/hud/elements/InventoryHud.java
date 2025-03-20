package meteordevelopment.meteorclient.systems.hud.elements;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2960;

public class InventoryHud extends HudElement {
   public static final HudElementInfo<InventoryHud> INFO;
   private static final class_2960 TEXTURE;
   private static final class_2960 TEXTURE_TRANSPARENT;
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> containers;
   private final Setting<Double> scale;
   private final Setting<InventoryHud.Background> background;
   private final Setting<SettingColor> color;
   private final class_1799[] containerItems;

   private InventoryHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.containers = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("containers")).description("Shows the contents of a container when holding them.")).defaultValue(false)).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale.")).defaultValue(2.0D).min(1.0D).sliderRange(1.0D, 5.0D).onChanged((aDouble) -> {
         this.calculateSize();
      })).build());
      this.background = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("background")).description("Background of inventory viewer.")).defaultValue(InventoryHud.Background.Texture)).onChanged((bg) -> {
         this.calculateSize();
      })).build());
      this.color = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color of the background.")).defaultValue(new SettingColor(255, 255, 255)).visible(() -> {
         return this.background.get() != InventoryHud.Background.None;
      })).build());
      this.containerItems = new class_1799[27];
      this.calculateSize();
   }

   public void render(HudRenderer renderer) {
      double x = (double)this.x;
      double y = (double)this.y;
      class_1799 container = this.getContainer();
      boolean hasContainer = (Boolean)this.containers.get() && container != null;
      if (hasContainer) {
         Utils.getItemsInContainerItem(container, this.containerItems);
      }

      Color drawColor = hasContainer ? Utils.getShulkerColor(container) : (Color)this.color.get();
      if (this.background.get() != InventoryHud.Background.None) {
         this.drawBackground(renderer, (int)x, (int)y, drawColor);
      }

      if (MeteorClient.mc.field_1724 != null) {
         renderer.post(() -> {
            for(int row = 0; row < 3; ++row) {
               for(int i = 0; i < 9; ++i) {
                  int index = row * 9 + i;
                  class_1799 stack = hasContainer ? this.containerItems[index] : MeteorClient.mc.field_1724.method_31548().method_5438(index + 9);
                  if (stack != null) {
                     int itemX = this.background.get() == InventoryHud.Background.Texture ? (int)(x + (double)(8 + i * 18) * (Double)this.scale.get()) : (int)(x + (double)(1 + i * 18) * (Double)this.scale.get());
                     int itemY = this.background.get() == InventoryHud.Background.Texture ? (int)(y + (double)(7 + row * 18) * (Double)this.scale.get()) : (int)(y + (double)(1 + row * 18) * (Double)this.scale.get());
                     renderer.item(stack, itemX, itemY, ((Double)this.scale.get()).floatValue(), true);
                  }
               }
            }

         });
      }
   }

   private void calculateSize() {
      this.setSize((double)((InventoryHud.Background)this.background.get()).width * (Double)this.scale.get(), (double)((InventoryHud.Background)this.background.get()).height * (Double)this.scale.get());
   }

   private void drawBackground(HudRenderer renderer, int x, int y, Color color) {
      int w = this.getWidth();
      int h = this.getHeight();
      switch(((InventoryHud.Background)this.background.get()).ordinal()) {
      case 1:
      case 2:
         renderer.texture(this.background.get() == InventoryHud.Background.Texture ? TEXTURE : TEXTURE_TRANSPARENT, (double)x, (double)y, (double)w, (double)h, color);
         break;
      case 3:
         renderer.quad((double)x, (double)y, (double)w, (double)h, color);
      }

   }

   private class_1799 getContainer() {
      if (!this.isInEditor() && MeteorClient.mc.field_1724 != null) {
         class_1799 stack = MeteorClient.mc.field_1724.method_6079();
         if (!Utils.hasItems(stack) && stack.method_7909() != class_1802.field_8466) {
            stack = MeteorClient.mc.field_1724.method_6047();
            return !Utils.hasItems(stack) && stack.method_7909() != class_1802.field_8466 ? null : stack;
         } else {
            return stack;
         }
      } else {
         return null;
      }
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "inventory", "Displays your inventory.", InventoryHud::new);
      TEXTURE = MeteorClient.identifier("textures/container.png");
      TEXTURE_TRANSPARENT = MeteorClient.identifier("textures/container-transparent.png");
   }

   public static enum Background {
      None(162, 54),
      Texture(176, 67),
      Outline(162, 54),
      Flat(162, 54);

      private final int width;
      private final int height;

      private Background(int width, int height) {
         this.width = width;
         this.height = height;
      }

      // $FF: synthetic method
      private static InventoryHud.Background[] $values() {
         return new InventoryHud.Background[]{None, Texture, Outline, Flat};
      }
   }
}

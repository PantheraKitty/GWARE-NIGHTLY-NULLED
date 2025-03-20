package meteordevelopment.meteorclient.systems.hud.elements;

import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.WorldRendererAccessor;
import meteordevelopment.meteorclient.settings.BlockListSetting;
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
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_3532;

public class HoleHud extends HudElement {
   public static final HudElementInfo<HoleHud> INFO;
   private final SettingGroup sgGeneral;
   private final SettingGroup sgBackground;
   public final Setting<List<class_2248>> safe;
   private final Setting<Double> scale;
   private final Setting<Integer> border;
   private final Setting<Boolean> background;
   private final Setting<SettingColor> backgroundColor;
   private final Color BG_COLOR;
   private final Color OL_COLOR;

   public HoleHud() {
      super(INFO);
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgBackground = this.settings.createGroup("Background");
      this.safe = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("safe-blocks")).description("Which blocks to consider safe.")).defaultValue(class_2246.field_10540, class_2246.field_9987, class_2246.field_22423, class_2246.field_22108).build());
      this.scale = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("scale")).description("The scale.")).defaultValue(2.0D).onChanged((aDouble) -> {
         this.calculateSize();
      })).min(1.0D).sliderRange(1.0D, 5.0D).build());
      this.border = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("border")).description("How much space to add around the element.")).defaultValue(0)).onChanged((integer) -> {
         this.calculateSize();
      })).build());
      this.background = this.sgBackground.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("background")).description("Displays background.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgBackground;
      ColorSetting.Builder var10002 = (ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("background-color")).description("Color used for the background.");
      Setting var10003 = this.background;
      Objects.requireNonNull(var10003);
      this.backgroundColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).defaultValue(new SettingColor(25, 25, 25, 50)).build());
      this.BG_COLOR = new Color(255, 25, 25, 100);
      this.OL_COLOR = new Color(255, 25, 25, 255);
      this.calculateSize();
   }

   public void setSize(double width, double height) {
      super.setSize(width + (double)((Integer)this.border.get() * 2), height + (double)((Integer)this.border.get() * 2));
   }

   private void calculateSize() {
      this.setSize(48.0D * (Double)this.scale.get(), 48.0D * (Double)this.scale.get());
   }

   public void render(HudRenderer renderer) {
      renderer.post(() -> {
         double x = (double)(this.x + (Integer)this.border.get());
         double y = (double)(this.y + (Integer)this.border.get());
         this.drawBlock(renderer, this.get(HoleHud.Facing.Left), x, y + 16.0D * (Double)this.scale.get());
         this.drawBlock(renderer, this.get(HoleHud.Facing.Front), x + 16.0D * (Double)this.scale.get(), y);
         this.drawBlock(renderer, this.get(HoleHud.Facing.Right), x + 32.0D * (Double)this.scale.get(), y + 16.0D * (Double)this.scale.get());
         this.drawBlock(renderer, this.get(HoleHud.Facing.Back), x + 16.0D * (Double)this.scale.get(), y + 32.0D * (Double)this.scale.get());
      });
      if ((Boolean)this.background.get()) {
         renderer.quad((double)this.x, (double)this.y, (double)this.getWidth(), (double)this.getHeight(), (Color)this.backgroundColor.get());
      }

   }

   private class_2350 get(HoleHud.Facing dir) {
      return this.isInEditor() ? class_2350.field_11033 : class_2350.method_10150((double)class_3532.method_15393(MeteorClient.mc.field_1724.method_36454() + (float)dir.offset));
   }

   private void drawBlock(HudRenderer renderer, class_2350 dir, double x, double y) {
      class_2248 block = dir == class_2350.field_11033 ? class_2246.field_10540 : MeteorClient.mc.field_1687.method_8320(MeteorClient.mc.field_1724.method_24515().method_10093(dir)).method_26204();
      if (((List)this.safe.get()).contains(block)) {
         renderer.item(block.method_8389().method_7854(), (int)x, (int)y, ((Double)this.scale.get()).floatValue(), false);
         if (dir != class_2350.field_11033) {
            ((WorldRendererAccessor)MeteorClient.mc.field_1769).getBlockBreakingInfos().values().forEach((info) -> {
               if (info.method_13991().equals(MeteorClient.mc.field_1724.method_24515().method_10093(dir))) {
                  this.renderBreaking(renderer, x, y, (double)((float)info.method_13988() / 9.0F));
               }

            });
         }
      }
   }

   private void renderBreaking(HudRenderer renderer, double x, double y, double percent) {
      renderer.quad(x, y, 16.0D * percent * (Double)this.scale.get(), 16.0D * (Double)this.scale.get(), this.BG_COLOR);
      renderer.quad(x, y, 16.0D * (Double)this.scale.get(), 1.0D * (Double)this.scale.get(), this.OL_COLOR);
      renderer.quad(x, y + 15.0D * (Double)this.scale.get(), 16.0D * (Double)this.scale.get(), 1.0D * (Double)this.scale.get(), this.OL_COLOR);
      renderer.quad(x, y, 1.0D * (Double)this.scale.get(), 16.0D * (Double)this.scale.get(), this.OL_COLOR);
      renderer.quad(x + 15.0D * (Double)this.scale.get(), y, 1.0D * (Double)this.scale.get(), 16.0D * (Double)this.scale.get(), this.OL_COLOR);
   }

   static {
      INFO = new HudElementInfo(Hud.GROUP, "hole", "Displays information about the hole you are standing in.", HoleHud::new);
   }

   private static enum Facing {
      Left(-90),
      Right(90),
      Front(0),
      Back(180);

      public final int offset;

      private Facing(int offset) {
         this.offset = offset;
      }

      // $FF: synthetic method
      private static HoleHud.Facing[] $values() {
         return new HoleHud.Facing[]{Left, Right, Front, Back};
      }
   }
}

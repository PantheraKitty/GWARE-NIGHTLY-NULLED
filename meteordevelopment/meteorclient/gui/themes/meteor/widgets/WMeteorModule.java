package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_3532;

public class WMeteorModule extends WPressable implements MeteorWidget {
   private final Module module;
   private double titleWidth;
   private double animationProgress1;
   private double animationProgress2;

   public WMeteorModule(Module module) {
      this.module = module;
      this.tooltip = module.description;
      if (module.isActive()) {
         this.animationProgress1 = 1.0D;
         this.animationProgress2 = 1.0D;
      } else {
         this.animationProgress1 = 0.0D;
         this.animationProgress2 = 0.0D;
      }

   }

   public double pad() {
      return this.theme.scale(4.0D);
   }

   protected void onCalculateSize() {
      double pad = this.pad();
      if (this.titleWidth == 0.0D) {
         this.titleWidth = this.theme.textWidth(this.module.title);
      }

      this.width = pad + this.titleWidth + pad;
      this.height = pad + this.theme.textHeight() + pad;
   }

   protected void onPressed(int button) {
      if (button == 0) {
         this.module.toggle();
      } else if (button == 1) {
         MeteorClient.mc.method_1507(this.theme.moduleScreen(this.module));
      }

   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      double pad = this.pad();
      this.animationProgress1 += delta * 4.0D * (double)(!this.module.isActive() && !this.mouseOver ? -1 : 1);
      this.animationProgress1 = class_3532.method_15350(this.animationProgress1, 0.0D, 1.0D);
      this.animationProgress2 += delta * 6.0D * (double)(this.module.isActive() ? 1 : -1);
      this.animationProgress2 = class_3532.method_15350(this.animationProgress2, 0.0D, 1.0D);
      if (this.animationProgress1 > 0.0D) {
         renderer.quad(this.x, this.y, this.width * this.animationProgress1, this.height, (Color)theme.moduleBackground.get());
      }

      if (this.animationProgress2 > 0.0D) {
         renderer.quad(this.x, this.y + this.height * (1.0D - this.animationProgress2), theme.scale(2.0D), this.height * this.animationProgress2, (Color)theme.accentColor.get());
      }

      double x = this.x + pad;
      double w = this.width - pad * 2.0D;
      if (theme.moduleAlignment.get() == AlignmentX.Center) {
         x += w / 2.0D - this.titleWidth / 2.0D;
      } else if (theme.moduleAlignment.get() == AlignmentX.Right) {
         x += w - this.titleWidth;
      }

      renderer.text(this.module.title, x, this.y + pad, (Color)theme.textColor.get(), false);
   }
}

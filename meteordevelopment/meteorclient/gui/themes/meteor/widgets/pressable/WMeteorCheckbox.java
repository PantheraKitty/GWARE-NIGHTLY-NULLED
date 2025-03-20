package meteordevelopment.meteorclient.gui.themes.meteor.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_3532;

public class WMeteorCheckbox extends WCheckbox implements MeteorWidget {
   private double animProgress;

   public WMeteorCheckbox(boolean checked) {
      super(checked);
      this.animProgress = checked ? 1.0D : 0.0D;
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      this.animProgress += (double)(this.checked ? 1 : -1) * delta * 14.0D;
      this.animProgress = class_3532.method_15350(this.animProgress, 0.0D, 1.0D);
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      if (this.animProgress > 0.0D) {
         double cs = (this.width - theme.scale(2.0D)) / 1.75D * this.animProgress;
         renderer.quad(this.x + (this.width - cs) / 2.0D, this.y + (this.height - cs) / 2.0D, cs, cs, (Color)theme.checkboxColor.get());
      }

   }
}

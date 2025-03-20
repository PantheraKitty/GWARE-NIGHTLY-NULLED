package meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WGonbleWareMinus extends WMinus implements GonbleWareWidget {
   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      double pad = this.pad();
      double s = this.theme.scale(3.0D);
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      renderer.quad(this.x + pad, this.y + this.height / 2.0D - s / 2.0D, this.width - pad * 2.0D, s, (Color)this.theme().minusColor.get());
   }
}

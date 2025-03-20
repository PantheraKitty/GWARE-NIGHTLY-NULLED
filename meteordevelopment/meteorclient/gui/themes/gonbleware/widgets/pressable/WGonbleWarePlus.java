package meteordevelopment.meteorclient.gui.themes.gonbleware.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareGuiTheme;
import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WGonbleWarePlus extends WPlus implements GonbleWareWidget {
   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      GonbleWareGuiTheme theme = this.theme();
      double pad = this.pad();
      double s = theme.scale(3.0D);
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      renderer.quad(this.x + pad, this.y + this.height / 2.0D - s / 2.0D, this.width - pad * 2.0D, s, (Color)theme.plusColor.get());
      renderer.quad(this.x + this.width / 2.0D - s / 2.0D, this.y + pad, s, this.height - pad * 2.0D, (Color)theme.plusColor.get());
   }
}

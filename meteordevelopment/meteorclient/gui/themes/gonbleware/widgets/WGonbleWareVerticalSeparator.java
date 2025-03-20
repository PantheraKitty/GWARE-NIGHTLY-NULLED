package meteordevelopment.meteorclient.gui.themes.gonbleware.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareGuiTheme;
import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareWidget;
import meteordevelopment.meteorclient.gui.widgets.WVerticalSeparator;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WGonbleWareVerticalSeparator extends WVerticalSeparator implements GonbleWareWidget {
   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      GonbleWareGuiTheme theme = this.theme();
      Color colorEdges = (Color)theme.separatorEdges.get();
      Color colorCenter = (Color)theme.separatorCenter.get();
      double s = theme.scale(1.0D);
      double offsetX = (double)Math.round(this.width / 2.0D);
      renderer.quad(this.x + offsetX, this.y, s, this.height / 2.0D, colorEdges, colorEdges, colorCenter, colorCenter);
      renderer.quad(this.x + offsetX, this.y + this.height / 2.0D, s, this.height / 2.0D, colorCenter, colorCenter, colorEdges, colorEdges);
   }
}

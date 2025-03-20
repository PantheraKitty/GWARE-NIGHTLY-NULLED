package meteordevelopment.meteorclient.gui.themes.gonbleware;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.utils.BaseWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public interface GonbleWareWidget extends BaseWidget {
   default GonbleWareGuiTheme theme() {
      return (GonbleWareGuiTheme)this.getTheme();
   }

   default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
      GonbleWareGuiTheme theme = this.theme();
      double s = theme.scale(2.0D);
      renderer.quad(widget.x + s, widget.y + s, widget.width - s * 2.0D, widget.height - s * 2.0D, theme.backgroundColor.get(pressed, mouseOver));
      Color outlineColor = theme.outlineColor.get(pressed, mouseOver);
      renderer.quad(widget.x, widget.y, widget.width, s, outlineColor);
      renderer.quad(widget.x, widget.y + widget.height - s, widget.width, s, outlineColor);
      renderer.quad(widget.x, widget.y + s, s, widget.height - s * 2.0D, outlineColor);
      renderer.quad(widget.x + widget.width - s, widget.y + s, s, widget.height - s * 2.0D, outlineColor);
   }
}

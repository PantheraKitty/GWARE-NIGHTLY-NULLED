package meteordevelopment.meteorclient.gui.themes.meteor.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.WHorizontalSeparator;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorHorizontalSeparator extends WHorizontalSeparator implements MeteorWidget {
   public WMeteorHorizontalSeparator(String text) {
      super(text);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      if (this.text == null) {
         this.renderWithoutText(renderer);
      } else {
         this.renderWithText(renderer);
      }

   }

   private void renderWithoutText(GuiRenderer renderer) {
      MeteorGuiTheme theme = this.theme();
      double s = theme.scale(1.0D);
      double w = this.width / 2.0D;
      renderer.quad(this.x, this.y + s, w, s, (Color)theme.separatorEdges.get(), (Color)theme.separatorCenter.get());
      renderer.quad(this.x + w, this.y + s, w, s, (Color)theme.separatorCenter.get(), (Color)theme.separatorEdges.get());
   }

   private void renderWithText(GuiRenderer renderer) {
      MeteorGuiTheme theme = this.theme();
      double s = theme.scale(2.0D);
      double h = theme.scale(1.0D);
      double textStart = (double)Math.round(this.width / 2.0D - this.textWidth / 2.0D - s);
      double textEnd = s + textStart + this.textWidth + s;
      double offsetY = (double)Math.round(this.height / 2.0D);
      renderer.quad(this.x, this.y + offsetY, textStart, h, (Color)theme.separatorEdges.get(), (Color)theme.separatorCenter.get());
      renderer.text(this.text, this.x + textStart + s, this.y, (Color)theme.separatorText.get(), false);
      renderer.quad(this.x + textEnd, this.y + offsetY, this.width - textEnd, h, (Color)theme.separatorCenter.get(), (Color)theme.separatorEdges.get());
   }
}

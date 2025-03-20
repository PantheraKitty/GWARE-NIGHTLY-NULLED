package meteordevelopment.meteorclient.gui.themes.meteor.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class WMeteorButton extends WButton implements MeteorWidget {
   public WMeteorButton(String text, GuiTexture texture) {
      super(text, texture);
   }

   protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
      MeteorGuiTheme theme = this.theme();
      double pad = this.pad();
      this.renderBackground(renderer, this, this.pressed, this.mouseOver);
      if (this.text != null) {
         renderer.text(this.text, this.x + this.width / 2.0D - this.textWidth / 2.0D, this.y + pad, (Color)theme.textColor.get(), false);
      } else {
         double ts = theme.textHeight();
         renderer.quad(this.x + this.width / 2.0D - ts / 2.0D, this.y + pad, ts, ts, this.texture, (Color)theme.textColor.get());
      }

   }
}

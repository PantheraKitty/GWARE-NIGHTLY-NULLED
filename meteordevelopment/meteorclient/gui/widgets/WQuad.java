package meteordevelopment.meteorclient.gui.widgets;

import meteordevelopment.meteorclient.utils.render.color.Color;

public abstract class WQuad extends WWidget {
   public Color color;

   public WQuad(Color color) {
      this.color = color;
   }

   protected void onCalculateSize() {
      double s = this.theme.scale(32.0D);
      this.width = s;
      this.height = s;
   }
}

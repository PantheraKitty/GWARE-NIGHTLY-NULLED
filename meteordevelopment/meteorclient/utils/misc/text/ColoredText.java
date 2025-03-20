package meteordevelopment.meteorclient.utils.misc.text;

import meteordevelopment.meteorclient.utils.render.color.Color;

public record ColoredText(String text, Color color) {
   public ColoredText(String text, Color color) {
      this.text = text;
      this.color = color;
   }

   public String text() {
      return this.text;
   }

   public Color color() {
      return this.color;
   }
}

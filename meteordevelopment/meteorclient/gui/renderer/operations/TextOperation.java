package meteordevelopment.meteorclient.gui.renderer.operations;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderOperation;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;

public class TextOperation extends GuiRenderOperation<TextOperation> {
   private String text;
   private TextRenderer renderer;
   public boolean title;

   public TextOperation set(String text, TextRenderer renderer, boolean title) {
      this.text = text;
      this.renderer = renderer;
      this.title = title;
      return this;
   }

   protected void onRun() {
      this.renderer.render(this.text, this.x, this.y, this.color);
   }
}

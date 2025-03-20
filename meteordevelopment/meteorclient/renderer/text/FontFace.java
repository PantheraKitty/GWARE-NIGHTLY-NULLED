package meteordevelopment.meteorclient.renderer.text;

import java.io.InputStream;

public abstract class FontFace {
   public final FontInfo info;

   protected FontFace(FontInfo info) {
      this.info = info;
   }

   public abstract InputStream toStream();

   public String toString() {
      return this.info.toString();
   }
}

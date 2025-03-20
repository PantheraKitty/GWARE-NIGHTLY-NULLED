package meteordevelopment.meteorclient.renderer.text;

import java.io.InputStream;
import java.nio.file.Path;
import meteordevelopment.meteorclient.utils.render.FontUtils;

public class SystemFontFace extends FontFace {
   private final Path path;

   public SystemFontFace(FontInfo info, Path path) {
      super(info);
      this.path = path;
   }

   public InputStream toStream() {
      if (!this.path.toFile().exists()) {
         throw new RuntimeException("Tried to load font that no longer exists.");
      } else {
         InputStream in = FontUtils.stream(this.path.toFile());
         if (in == null) {
            throw new RuntimeException("Failed to load font from " + String.valueOf(this.path) + ".");
         } else {
            return in;
         }
      }
   }

   public String toString() {
      String var10000 = super.toString();
      return var10000 + " (" + this.path.toString() + ")";
   }
}

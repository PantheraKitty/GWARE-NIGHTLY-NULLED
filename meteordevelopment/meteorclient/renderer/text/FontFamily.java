package meteordevelopment.meteorclient.renderer.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FontFamily {
   private final String name;
   private final List<FontFace> fonts = new ArrayList();

   public FontFamily(String name) {
      this.name = name;
   }

   public boolean addFont(FontFace font) {
      return this.fonts.add(font);
   }

   public boolean hasType(FontInfo.Type type) {
      return this.get(type) != null;
   }

   public FontFace get(FontInfo.Type type) {
      if (type == null) {
         return null;
      } else {
         Iterator var2 = this.fonts.iterator();

         FontFace font;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            font = (FontFace)var2.next();
         } while(!font.info.type().equals(type));

         return font;
      }
   }

   public String getName() {
      return this.name;
   }
}

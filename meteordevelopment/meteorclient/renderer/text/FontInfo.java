package meteordevelopment.meteorclient.renderer.text;

public record FontInfo(String family, FontInfo.Type type) {
   public FontInfo(String family, FontInfo.Type type) {
      this.family = family;
      this.type = type;
   }

   public String toString() {
      String var10000 = this.family;
      return var10000 + " " + String.valueOf(this.type);
   }

   public boolean equals(FontInfo info) {
      if (this == info) {
         return true;
      } else if (info != null && this.family != null && this.type != null) {
         return this.family.equals(info.family) && this.type == info.type;
      } else {
         return false;
      }
   }

   public String family() {
      return this.family;
   }

   public FontInfo.Type type() {
      return this.type;
   }

   public static enum Type {
      Regular,
      Bold,
      Italic,
      BoldItalic;

      public static FontInfo.Type fromString(String str) {
         byte var2 = -1;
         switch(str.hashCode()) {
         case -2094913968:
            if (str.equals("Italic")) {
               var2 = 1;
            }
            break;
         case -1886647253:
            if (str.equals("Bold Italic")) {
               var2 = 2;
            }
            break;
         case 2076325:
            if (str.equals("Bold")) {
               var2 = 0;
            }
            break;
         case 1152091445:
            if (str.equals("BoldItalic")) {
               var2 = 3;
            }
         }

         FontInfo.Type var10000;
         switch(var2) {
         case 0:
            var10000 = Bold;
            break;
         case 1:
            var10000 = Italic;
            break;
         case 2:
         case 3:
            var10000 = BoldItalic;
            break;
         default:
            var10000 = Regular;
         }

         return var10000;
      }

      public String toString() {
         String var10000;
         switch(this.ordinal()) {
         case 1:
            var10000 = "Bold";
            break;
         case 2:
            var10000 = "Italic";
            break;
         case 3:
            var10000 = "Bold Italic";
            break;
         default:
            var10000 = "Regular";
         }

         return var10000;
      }

      // $FF: synthetic method
      private static FontInfo.Type[] $values() {
         return new FontInfo.Type[]{Regular, Bold, Italic, BoldItalic};
      }
   }
}

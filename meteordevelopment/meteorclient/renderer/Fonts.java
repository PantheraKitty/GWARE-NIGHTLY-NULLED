package meteordevelopment.meteorclient.renderer;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.CustomFontChangedEvent;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.renderer.text.CustomTextRenderer;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.renderer.text.FontFamily;
import meteordevelopment.meteorclient.renderer.text.FontInfo;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.meteorclient.utils.render.FontUtils;

public class Fonts {
   public static final String[] BUILTIN_FONTS = new String[]{"JetBrains Mono", "Comfortaa", "Tw Cen MT", "Pixelation"};
   public static String DEFAULT_FONT_FAMILY;
   public static FontFace DEFAULT_FONT;
   public static final List<FontFamily> FONT_FAMILIES = new ArrayList();
   public static CustomTextRenderer RENDERER;

   private Fonts() {
   }

   @PreInit(
      dependencies = {Shaders.class}
   )
   public static void refresh() {
      FONT_FAMILIES.clear();
      String[] var0 = BUILTIN_FONTS;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         String builtinFont = var0[var2];
         FontUtils.loadBuiltin(FONT_FAMILIES, builtinFont);
      }

      Iterator var4 = FontUtils.getSearchPaths().iterator();

      while(var4.hasNext()) {
         String fontPath = (String)var4.next();
         FontUtils.loadSystem(FONT_FAMILIES, new File(fontPath));
      }

      FONT_FAMILIES.sort(Comparator.comparing(FontFamily::getName));
      MeteorClient.LOG.info("Found {} font families.", FONT_FAMILIES.size());
      DEFAULT_FONT_FAMILY = FontUtils.getBuiltinFontInfo(BUILTIN_FONTS[1]).family();
      DEFAULT_FONT = getFamily(DEFAULT_FONT_FAMILY).get(FontInfo.Type.Regular);
      Config config = Config.get();
      load(config != null ? (FontFace)config.font.get() : DEFAULT_FONT);
   }

   public static void load(FontFace fontFace) {
      if (RENDERER != null) {
         if (RENDERER.fontFace.equals(fontFace)) {
            return;
         }

         RENDERER.destroy();
      }

      try {
         RENDERER = new CustomTextRenderer(fontFace);
         MeteorClient.EVENT_BUS.post((Object)CustomFontChangedEvent.get());
      } catch (Exception var2) {
         if (fontFace.equals(DEFAULT_FONT)) {
            throw new RuntimeException("Failed to load default font: " + String.valueOf(fontFace), var2);
         }

         MeteorClient.LOG.error("Failed to load font: " + String.valueOf(fontFace), var2);
         load(DEFAULT_FONT);
      }

      if (MeteorClient.mc.field_1755 instanceof WidgetScreen && (Boolean)Config.get().customFont.get()) {
         ((WidgetScreen)MeteorClient.mc.field_1755).invalidate();
      }

   }

   public static FontFamily getFamily(String name) {
      Iterator var1 = FONT_FAMILIES.iterator();

      FontFamily fontFamily;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         fontFamily = (FontFamily)var1.next();
      } while(!fontFamily.getName().equalsIgnoreCase(name));

      return fontFamily;
   }
}

package meteordevelopment.meteorclient.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.themes.gonbleware.GonbleWareGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import net.minecraft.class_2487;
import net.minecraft.class_2507;

public class GuiThemes {
   private static final File FOLDER;
   private static final File THEMES_FOLDER;
   private static final File FILE;
   private static final List<GuiTheme> themes;
   private static GuiTheme theme;
   private static boolean hadGonbleWareTheme;

   private GuiThemes() {
   }

   @PreInit
   public static void init() {
      add(new GonbleWareGuiTheme());
      add(new MeteorGuiTheme());
   }

   @PostInit
   public static void postInit() {
      class_2487 tag;
      if (FILE.exists()) {
         try {
            tag = class_2507.method_10633(FILE.toPath());
            if (tag != null) {
               select(tag.method_10558("currentTheme"));
            }
         } catch (IOException var2) {
            var2.printStackTrace();
         }
      }

      if (theme == null) {
         select("GonbleWare");
      }

      if (FILE.exists()) {
         try {
            tag = class_2507.method_10633(FILE.toPath());
            if (tag != null && !tag.method_10577("hadGonbleWareTheme")) {
               select("GonbleWare");
               hadGonbleWareTheme = true;
            }
         } catch (IOException var1) {
            var1.printStackTrace();
         }
      }

   }

   public static void add(GuiTheme theme) {
      Iterator it = themes.iterator();

      while(it.hasNext()) {
         if (((GuiTheme)it.next()).name.equals(theme.name)) {
            it.remove();
            MeteorClient.LOG.error("Theme with the name '{}' has already been added.", theme.name);
            break;
         }
      }

      themes.add(theme);
   }

   public static void select(String name) {
      GuiTheme theme = null;
      Iterator var2 = themes.iterator();

      while(var2.hasNext()) {
         GuiTheme t = (GuiTheme)var2.next();
         if (t.name.equals(name)) {
            theme = t;
            break;
         }
      }

      if (theme != null) {
         saveTheme();
         GuiThemes.theme = theme;

         try {
            File file = new File(THEMES_FOLDER, get().name + ".nbt");
            if (file.exists()) {
               class_2487 tag = class_2507.method_10633(file.toPath());
               if (tag != null) {
                  get().fromTag(tag);
               }
            }
         } catch (IOException var4) {
            var4.printStackTrace();
         }

         saveGlobal();
      }

   }

   public static GuiTheme get() {
      return theme;
   }

   public static String[] getNames() {
      String[] names = new String[themes.size()];

      for(int i = 0; i < themes.size(); ++i) {
         names[i] = ((GuiTheme)themes.get(i)).name;
      }

      return names;
   }

   private static void saveTheme() {
      if (get() != null) {
         try {
            class_2487 tag = get().toTag();
            THEMES_FOLDER.mkdirs();
            class_2507.method_10630(tag, (new File(THEMES_FOLDER, get().name + ".nbt")).toPath());
         } catch (IOException var1) {
            var1.printStackTrace();
         }
      }

   }

   private static void saveGlobal() {
      try {
         class_2487 tag = new class_2487();
         tag.method_10582("currentTheme", get().name);
         tag.method_10556("hadGonbleWareTheme", hadGonbleWareTheme);
         FOLDER.mkdirs();
         class_2507.method_10630(tag, FILE.toPath());
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

   public static void save() {
      saveTheme();
      saveGlobal();
   }

   static {
      FOLDER = new File(MeteorClient.FOLDER, "gui");
      THEMES_FOLDER = new File(FOLDER, "themes");
      FILE = new File(FOLDER, "gui.nbt");
      themes = new ArrayList();
      hadGonbleWareTheme = false;
   }
}

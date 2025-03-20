package meteordevelopment.meteorclient.utils.misc.input;

import java.util.Iterator;
import java.util.Map;
import meteordevelopment.meteorclient.mixin.KeyBindingAccessor;
import net.minecraft.class_304;
import net.minecraft.class_3675.class_307;

public class KeyBinds {
   private static final String CATEGORY = "Meteor Client";
   public static class_304 OPEN_GUI;
   public static class_304 OPEN_COMMANDS;

   private KeyBinds() {
   }

   public static class_304[] apply(class_304[] binds) {
      Map<String, Integer> categories = KeyBindingAccessor.getCategoryOrderMap();
      int highest = 0;
      Iterator var3 = categories.values().iterator();

      while(var3.hasNext()) {
         int i = (Integer)var3.next();
         if (i > highest) {
            highest = i;
         }
      }

      categories.put("Meteor Client", highest + 1);
      class_304[] newBinds = new class_304[binds.length + 2];
      System.arraycopy(binds, 0, newBinds, 0, binds.length);
      newBinds[binds.length] = OPEN_GUI;
      newBinds[binds.length + 1] = OPEN_COMMANDS;
      return newBinds;
   }

   static {
      OPEN_GUI = new class_304("key.meteor-client.open-gui", class_307.field_1668, 344, "Meteor Client");
      OPEN_COMMANDS = new class_304("key.meteor-client.open-commands", class_307.field_1668, 46, "Meteor Client");
   }
}

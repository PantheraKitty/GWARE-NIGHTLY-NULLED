package meteordevelopment.meteorclient.events.render;

import java.util.Iterator;
import net.minecraft.class_2561;
import net.minecraft.class_345;

public class RenderBossBarEvent {
   public static class BossIterator {
      private static final RenderBossBarEvent.BossIterator INSTANCE = new RenderBossBarEvent.BossIterator();
      public Iterator<class_345> iterator;

      public static RenderBossBarEvent.BossIterator get(Iterator<class_345> iterator) {
         INSTANCE.iterator = iterator;
         return INSTANCE;
      }
   }

   public static class BossSpacing {
      private static final RenderBossBarEvent.BossSpacing INSTANCE = new RenderBossBarEvent.BossSpacing();
      public int spacing;

      public static RenderBossBarEvent.BossSpacing get(int spacing) {
         INSTANCE.spacing = spacing;
         return INSTANCE;
      }
   }

   public static class BossText {
      private static final RenderBossBarEvent.BossText INSTANCE = new RenderBossBarEvent.BossText();
      public class_345 bossBar;
      public class_2561 name;

      public static RenderBossBarEvent.BossText get(class_345 bossBar, class_2561 name) {
         INSTANCE.bossBar = bossBar;
         INSTANCE.name = name;
         return INSTANCE;
      }
   }
}

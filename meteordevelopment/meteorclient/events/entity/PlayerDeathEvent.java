package meteordevelopment.meteorclient.events.entity;

import net.minecraft.class_1657;

public class PlayerDeathEvent {
   public static class Death extends PlayerDeathEvent {
      private static final PlayerDeathEvent.Death INSTANCE = new PlayerDeathEvent.Death();
      private class_1657 player;
      private int pops;

      public static PlayerDeathEvent.Death get(class_1657 player, int pop) {
         INSTANCE.player = player;
         INSTANCE.pops = pop;
         return INSTANCE;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public int getPops() {
         return this.pops;
      }
   }

   public static class TotemPop extends PlayerDeathEvent {
      private static final PlayerDeathEvent.TotemPop INSTANCE = new PlayerDeathEvent.TotemPop();
      private class_1657 player;
      private int pops;

      public static PlayerDeathEvent get(class_1657 player, int pop) {
         INSTANCE.player = player;
         INSTANCE.pops = pop;
         return INSTANCE;
      }

      public class_1657 getPlayer() {
         return this.player;
      }

      public int getPops() {
         return this.pops;
      }
   }
}

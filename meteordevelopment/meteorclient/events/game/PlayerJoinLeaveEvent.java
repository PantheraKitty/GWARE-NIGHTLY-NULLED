package meteordevelopment.meteorclient.events.game;

import net.minecraft.class_640;
import net.minecraft.class_2703.class_2705;

public class PlayerJoinLeaveEvent {
   public static class Leave {
      private static final PlayerJoinLeaveEvent.Leave INSTANCE = new PlayerJoinLeaveEvent.Leave();
      private class_640 entry;

      public static PlayerJoinLeaveEvent.Leave get(class_640 entry) {
         INSTANCE.entry = entry;
         return INSTANCE;
      }

      public class_640 getEntry() {
         return this.entry;
      }
   }

   public static class Join {
      private static final PlayerJoinLeaveEvent.Join INSTANCE = new PlayerJoinLeaveEvent.Join();
      private class_2705 entry;

      public static PlayerJoinLeaveEvent.Join get(class_2705 entry) {
         INSTANCE.entry = entry;
         return INSTANCE;
      }

      public class_2705 getEntry() {
         return this.entry;
      }
   }
}

package meteordevelopment.meteorclient.events.packets;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_2535;
import net.minecraft.class_2596;

public class PacketEvent {
   public static class Sent {
      public class_2596<?> packet;
      public class_2535 connection;

      public Sent(class_2596<?> packet, class_2535 connection) {
         this.packet = packet;
         this.connection = connection;
      }
   }

   public static class Send extends Cancellable {
      public class_2596<?> packet;
      public class_2535 connection;

      public Send(class_2596<?> packet, class_2535 connection) {
         this.setCancelled(false);
         this.packet = packet;
         this.connection = connection;
      }
   }

   public static class Receive extends Cancellable {
      public class_2596<?> packet;
      public class_2535 connection;

      public Receive(class_2596<?> packet, class_2535 connection) {
         this.setCancelled(false);
         this.packet = packet;
         this.connection = connection;
      }
   }
}

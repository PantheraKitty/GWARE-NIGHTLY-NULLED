package meteordevelopment.meteorclient.events.entity;

import net.minecraft.class_1297;
import net.minecraft.class_2833;

public class VehicleMoveEvent {
   private static final VehicleMoveEvent INSTANCE = new VehicleMoveEvent();
   public class_1297 entity;
   public class_2833 packet;

   public static VehicleMoveEvent get(class_2833 packet, class_1297 entity) {
      INSTANCE.entity = entity;
      INSTANCE.packet = packet;
      return INSTANCE;
   }
}

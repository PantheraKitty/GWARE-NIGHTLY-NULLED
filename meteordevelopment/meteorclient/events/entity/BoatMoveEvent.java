package meteordevelopment.meteorclient.events.entity;

import net.minecraft.class_1690;

public class BoatMoveEvent {
   private static final BoatMoveEvent INSTANCE = new BoatMoveEvent();
   public class_1690 boat;

   public static BoatMoveEvent get(class_1690 entity) {
      INSTANCE.boat = entity;
      return INSTANCE;
   }
}

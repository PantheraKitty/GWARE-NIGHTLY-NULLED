package meteordevelopment.meteorclient.events.entity;

import net.minecraft.class_1309;
import net.minecraft.class_243;

public class LivingEntityMoveEvent {
   private static final LivingEntityMoveEvent INSTANCE = new LivingEntityMoveEvent();
   public class_1309 entity;
   public class_243 movement;

   public static LivingEntityMoveEvent get(class_1309 entity, class_243 movement) {
      INSTANCE.entity = entity;
      INSTANCE.movement = movement;
      return INSTANCE;
   }
}

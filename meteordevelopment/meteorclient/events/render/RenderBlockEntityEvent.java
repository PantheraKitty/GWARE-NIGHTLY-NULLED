package meteordevelopment.meteorclient.events.render;

import meteordevelopment.meteorclient.events.Cancellable;
import net.minecraft.class_2586;

public class RenderBlockEntityEvent extends Cancellable {
   private static final RenderBlockEntityEvent INSTANCE = new RenderBlockEntityEvent();
   public class_2586 blockEntity;

   public static RenderBlockEntityEvent get(class_2586 blockEntity) {
      INSTANCE.setCancelled(false);
      INSTANCE.blockEntity = blockEntity;
      return INSTANCE;
   }
}

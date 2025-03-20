package meteordevelopment.meteorclient.events.entity.player;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1297;

public class RenderPlayerEvent implements ICancellable {
   public final class_1297 entity;
   public boolean cancelled;

   public RenderPlayerEvent(class_1297 entity) {
      this.entity = entity;
   }

   public class_1297 getEntity() {
      return this.entity;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }
}

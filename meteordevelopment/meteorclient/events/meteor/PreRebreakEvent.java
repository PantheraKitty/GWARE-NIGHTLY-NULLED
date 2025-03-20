package meteordevelopment.meteorclient.events.meteor;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_2338;

public class PreRebreakEvent implements ICancellable {
   private final class_2338 blockPos;

   public PreRebreakEvent(class_2338 blockPos) {
      this.blockPos = blockPos;
   }

   public class_2338 getBlockPos() {
      return this.blockPos;
   }

   public void setCancelled(boolean cancelled) {
   }

   public boolean isCancelled() {
      return false;
   }
}

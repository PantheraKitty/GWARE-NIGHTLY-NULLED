package meteordevelopment.meteorclient.events.world;

import net.minecraft.class_2818;

public record ChunkDataEvent(class_2818 chunk) {
   public ChunkDataEvent(class_2818 chunk) {
      this.chunk = chunk;
   }

   public class_2818 chunk() {
      return this.chunk;
   }
}

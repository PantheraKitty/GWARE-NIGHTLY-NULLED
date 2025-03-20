package meteordevelopment.meteorclient.events.meteor;

import net.minecraft.class_2338;

public class SilentMineFinishedEvent {
   public static class Post {
      private boolean isRebreak;
      private class_2338 blockPos;

      public Post(class_2338 blockPos, boolean isRebreak) {
         this.blockPos = blockPos;
         this.isRebreak = isRebreak;
      }

      public boolean getIsRebreak() {
         return this.isRebreak;
      }

      public class_2338 getBlockPos() {
         return this.blockPos;
      }
   }

   public static class Pre {
      private boolean isRebreak;
      private class_2338 blockPos;

      public Pre(class_2338 blockPos, boolean isRebreak) {
         this.blockPos = blockPos;
         this.isRebreak = isRebreak;
      }

      public boolean getIsRebreak() {
         return this.isRebreak;
      }

      public class_2338 getBlockPos() {
         return this.blockPos;
      }
   }
}

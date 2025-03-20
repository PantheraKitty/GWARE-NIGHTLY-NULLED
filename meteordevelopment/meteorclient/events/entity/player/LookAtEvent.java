package meteordevelopment.meteorclient.events.entity.player;

import net.minecraft.class_243;

public class LookAtEvent {
   private class_243 target;
   private float yaw;
   private float pitch;
   private boolean rotation;
   public float priority = 0.0F;

   public class_243 getTarget() {
      return this.target;
   }

   public boolean getRotation() {
      return this.rotation;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setTarget(class_243 target, float priority) {
      if (priority >= this.priority) {
         this.rotation = false;
         this.priority = priority;
         this.target = target;
      }

   }

   public void setRotation(float yaw, float pitch, float priority) {
      if (priority >= this.priority) {
         this.rotation = true;
         this.priority = priority;
         this.yaw = yaw;
         this.pitch = pitch;
      }

   }
}

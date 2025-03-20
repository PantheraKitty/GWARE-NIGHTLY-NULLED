package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ICapabilityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   targets = {"com.mojang.blaze3d.platform.GlStateManager$CapabilityTracker"}
)
public abstract class CapabilityTrackerMixin implements ICapabilityTracker {
   @Shadow
   private boolean field_5051;

   @Shadow
   public abstract void method_4470(boolean var1);

   public boolean get() {
      return this.field_5051;
   }

   public void set(boolean state) {
      this.method_4470(state);
   }
}

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.ApplyTransformationEvent;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_4587;
import net.minecraft.class_804;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_804.class})
public abstract class TransformationMixin {
   @Inject(
      method = {"apply"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onApply(boolean leftHanded, class_4587 matrices, CallbackInfo info) {
      ApplyTransformationEvent event = (ApplyTransformationEvent)MeteorClient.EVENT_BUS.post((ICancellable)ApplyTransformationEvent.get((class_804)this, leftHanded, matrices));
      if (event.isCancelled()) {
         info.cancel();
      }

   }
}

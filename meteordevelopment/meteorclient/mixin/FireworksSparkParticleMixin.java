package meteordevelopment.meteorclient.mixin;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.class_677.class_680;
import net.minecraft.class_677.class_681;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({class_681.class})
public abstract class FireworksSparkParticleMixin {
   @Inject(
      method = {"addExplosionParticle"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/particle/FireworksSparkParticle$Explosion;setTrail(Z)V"
)},
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private void onAddExplosion(double x, double y, double z, double velocityX, double velocityY, double velocityZ, IntList colors, IntList targetColors, boolean trail, boolean flicker, CallbackInfo info, class_680 explosion) {
      if (explosion == null) {
         info.cancel();
      }

   }
}

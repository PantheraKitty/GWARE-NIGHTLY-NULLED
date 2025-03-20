package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Collisions;
import net.minecraft.class_2784;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_2784.class})
public abstract class WorldBorderMixin {
   @Inject(
      method = {"canCollide"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void canCollide(CallbackInfoReturnable<Boolean> info) {
      if (((Collisions)Modules.get().get(Collisions.class)).ignoreBorder()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"contains(Lnet/minecraft/util/math/BlockPos;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void contains(CallbackInfoReturnable<Boolean> info) {
      if (((Collisions)Modules.get().get(Collisions.class)).ignoreBorder()) {
         info.setReturnValue(true);
      }

   }
}

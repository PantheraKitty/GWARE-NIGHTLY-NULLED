package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_3572;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_3572.class})
public abstract class ChunkSkyLightProviderMixin {
   @Inject(
      at = {@At("HEAD")},
      method = {"method_51531"},
      cancellable = true
   )
   private void recalculateLevel(long blockPos, long l, int lightLevel, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noSkylightUpdates()) {
         ci.cancel();
      }

   }
}

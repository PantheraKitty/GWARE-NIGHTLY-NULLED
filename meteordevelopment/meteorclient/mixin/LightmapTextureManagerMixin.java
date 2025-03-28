package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_765.class})
public abstract class LightmapTextureManagerMixin {
   @ModifyArgs(
      method = {"update"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"
)
   )
   private void update(Args args) {
      if (((Fullbright)Modules.get().get(Fullbright.class)).getGamma() || Modules.get().isActive(Xray.class)) {
         args.set(2, -1);
      }

   }

   @Inject(
      method = {"getDarknessFactor(F)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getDarknessFactor(float tickDelta, CallbackInfoReturnable<Float> info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noDarkness()) {
         info.setReturnValue(0.0F);
      }

   }
}

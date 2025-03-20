package meteordevelopment.meteorclient.mixin.sodium;

import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.class_1087;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockRenderer.class})
public abstract class SodiumBlockRendererMixin {
   @Inject(
      method = {"renderModel"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/caffeinemc/mods/sodium/client/model/color/ColorProviderRegistry;getColorProvider(Lnet/minecraft/block/Block;)Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;",
   shift = Shift.AFTER
)},
      cancellable = true
   )
   private void onRenderModel(class_1087 model, class_2680 state, class_2338 pos, class_2338 origin, CallbackInfo info) {
      int alpha = Xray.getAlpha(state, pos);
      if (alpha == 0) {
         info.cancel();
      }

   }
}

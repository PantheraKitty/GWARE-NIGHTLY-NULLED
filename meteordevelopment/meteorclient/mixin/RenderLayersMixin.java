package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4696;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4696.class})
public abstract class RenderLayersMixin {
   @Inject(
      method = {"getBlockLayer"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onGetBlockLayer(class_2680 state, CallbackInfoReturnable<class_1921> info) {
      if (Modules.get() != null) {
         int alpha = Xray.getAlpha(state, (class_2338)null);
         if (alpha > 0 && alpha < 255) {
            info.setReturnValue(class_1921.method_23583());
         }

      }
   }
}

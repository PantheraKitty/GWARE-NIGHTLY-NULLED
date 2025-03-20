package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_2580;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_822;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_822.class})
public abstract class BeaconBlockEntityRendererMixin {
   @Inject(
      method = {"render(Lnet/minecraft/block/entity/BeaconBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(class_2580 beaconBlockEntity, float f, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, int j, CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBeaconBeams()) {
         info.cancel();
      }

   }
}

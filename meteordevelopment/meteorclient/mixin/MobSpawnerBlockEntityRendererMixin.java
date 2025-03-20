package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_839;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_839.class})
public abstract class MobSpawnerBlockEntityRendererMixin {
   @Inject(
      method = {"render(Lnet/minecraft/block/entity/MobSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRender(CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noMobInSpawner()) {
         ci.cancel();
      }

   }
}

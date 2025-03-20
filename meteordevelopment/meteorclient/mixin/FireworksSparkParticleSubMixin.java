package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_4184;
import net.minecraft.class_4588;
import net.minecraft.class_677.class_678;
import net.minecraft.class_677.class_680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_680.class, class_678.class})
public abstract class FireworksSparkParticleSubMixin {
   @Inject(
      method = {"buildGeometry"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void buildExplosionGeometry(class_4588 vertexConsumer, class_4184 camera, float tickDelta, CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noFireworkExplosions()) {
         info.cancel();
      }

   }
}

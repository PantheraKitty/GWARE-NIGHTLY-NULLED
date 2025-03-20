package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.minecraft.class_1087;
import net.minecraft.class_1920;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_5819;
import net.minecraft.class_778;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_778.class})
public abstract class BlockModelRendererMixin {
   @Unique
   private final ThreadLocal<Integer> alphas = new ThreadLocal();

   @Inject(
      method = {"renderSmooth", "renderFlat"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderSmooth(class_1920 world, class_1087 model, class_2680 state, class_2338 pos, class_4587 matrices, class_4588 vertexConsumer, boolean cull, class_5819 random, long seed, int overlay, CallbackInfo info) {
      int alpha = Xray.getAlpha(state, pos);
      if (alpha == 0) {
         info.cancel();
      } else {
         this.alphas.set(alpha);
      }

   }

   @ModifyConstant(
      method = {"renderQuad"},
      constant = {@Constant(
   floatValue = 1.0F,
   ordinal = 3
)}
   )
   private float renderQuad_modifyAlpha(float original) {
      int alpha = (Integer)this.alphas.get();
      return alpha == -1 ? original : (float)alpha / 255.0F;
   }
}

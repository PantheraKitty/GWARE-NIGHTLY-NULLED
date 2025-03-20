package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.utils.network.Capes;
import net.minecraft.class_1309;
import net.minecraft.class_2960;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_583;
import net.minecraft.class_742;
import net.minecraft.class_979;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_979.class})
public abstract class ElytraFeatureRendererMixin<T extends class_1309, M extends class_583<T>> extends class_3887<T, M> {
   public ElytraFeatureRendererMixin(class_3883<T, M> context) {
      super(context);
   }

   @ModifyExpressionValue(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/SkinTextures;capeTexture()Lnet/minecraft/util/Identifier;"
)}
   )
   private class_2960 modifyCapeTexture(class_2960 original, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
      if (livingEntity instanceof class_742) {
         class_742 playerEntity = (class_742)livingEntity;
         class_2960 id = Capes.get(playerEntity);
         return id == null ? original : id;
      } else {
         return original;
      }
   }
}

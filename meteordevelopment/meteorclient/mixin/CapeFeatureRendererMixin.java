package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.utils.network.Capes;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_972.class})
public abstract class CapeFeatureRendererMixin {
   @ModifyExpressionValue(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/SkinTextures;capeTexture()Lnet/minecraft/util/Identifier;"
)}
   )
   private class_2960 modifyCapeTexture(class_2960 original, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, class_742 abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
      class_2960 id = Capes.get(abstractClientPlayerEntity);
      return id == null ? original : id;
   }
}

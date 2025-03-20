package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_1087;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2504;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4696;
import net.minecraft.class_811;
import net.minecraft.class_8923;
import net.minecraft.class_918;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_918.class})
public abstract class ItemRendererMixin {
   @ModifyArgs(
      method = {"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V"
)
   )
   private void modifyEnchant(Args args, class_1799 stack, class_811 renderMode, boolean leftHanded, class_4587 matrices, class_4597 vertexConsumers, int light, int overlay, class_1087 model) {
      if (((NoRender)Modules.get().get(NoRender.class)).noEnchantGlint()) {
         boolean var10000;
         label35: {
            if (renderMode != class_811.field_4317 && !renderMode.method_29998()) {
               class_1792 var12 = stack.method_7909();
               if (var12 instanceof class_1747) {
                  class_1747 blockItem = (class_1747)var12;
                  if (blockItem.method_7711() instanceof class_8923 || blockItem.method_7711() instanceof class_2504) {
                     var10000 = false;
                     break label35;
                  }
               }
            }

            var10000 = true;
         }

         boolean bl = var10000;
         args.set(5, vertexConsumers.getBuffer(class_4696.method_23678(stack, bl)));
      }
   }
}

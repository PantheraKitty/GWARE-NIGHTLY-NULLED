package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.systems.modules.render.HandView;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1007;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_630;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_1007.class})
public abstract class PlayerEntityRendererMixin {
   @ModifyArgs(
      method = {"renderArm"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
   ordinal = 0
)
   )
   private void modifyRenderLayer(Args args, class_4587 matrices, class_4597 vertexConsumers, int light, class_742 player, class_630 arm, class_630 sleeve) {
      Chams chams = (Chams)Modules.get().get(Chams.class);
      if (chams.isActive() && (Boolean)chams.hand.get()) {
         class_2960 texture = (Boolean)chams.handTexture.get() ? player.method_52814().comp_1626() : Chams.BLANK;
         args.set(1, vertexConsumers.getBuffer(class_1921.method_23580(texture)));
      }

   }

   @Redirect(
      method = {"renderArm"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
   ordinal = 0
)
   )
   private void redirectRenderMain(class_630 modelPart, class_4587 matrices, class_4588 vertices, int light, int overlay) {
      Chams chams = (Chams)Modules.get().get(Chams.class);
      if (chams.isActive() && (Boolean)chams.hand.get()) {
         Color color = (Color)chams.handColor.get();
         modelPart.method_22699(matrices, vertices, light, overlay, color.getPacked());
      } else {
         modelPart.method_22698(matrices, vertices, light, overlay);
      }

   }

   @Redirect(
      method = {"renderArm"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
   ordinal = 1
)
   )
   private void redirectRenderSleeve(class_630 modelPart, class_4587 matrices, class_4588 vertices, int light, int overlay) {
      Chams chams = (Chams)Modules.get().get(Chams.class);
      if (!Modules.get().isActive(HandView.class)) {
         if (chams.isActive() && (Boolean)chams.hand.get()) {
            Color color = (Color)chams.handColor.get();
            modelPart.method_22699(matrices, vertices, light, overlay, color.getPacked());
         } else {
            modelPart.method_22698(matrices, vertices, light, overlay);
         }

      }
   }
}

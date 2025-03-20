package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_1088;
import net.minecraft.class_1921;
import net.minecraft.class_2215;
import net.minecraft.class_2350;
import net.minecraft.class_2546;
import net.minecraft.class_2573;
import net.minecraft.class_2680;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_630;
import net.minecraft.class_7833;
import net.minecraft.class_823;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_823.class})
public abstract class BannerBlockEntityRendererMixin {
   @Final
   @Shadow
   private class_630 field_20811;
   @Final
   @Shadow
   private class_630 field_20812;

   @Inject(
      method = {"render(Lnet/minecraft/block/entity/BannerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void render(class_2573 bannerBlockEntity, float f, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, int j, CallbackInfo ci) {
      if (bannerBlockEntity.method_10997() != null) {
         NoRender.BannerRenderMode renderMode = ((NoRender)Modules.get().get(NoRender.class)).getBannerRenderMode();
         if (renderMode == NoRender.BannerRenderMode.None) {
            ci.cancel();
         } else if (renderMode == NoRender.BannerRenderMode.Pillar) {
            class_2680 blockState = bannerBlockEntity.method_11010();
            if (blockState.method_26204() instanceof class_2215) {
               this.field_20811.field_3665 = true;
               this.field_20812.field_3665 = false;
               this.renderPillar(bannerBlockEntity, matrixStack, vertexConsumerProvider, i, j);
            } else {
               this.field_20811.field_3665 = false;
               this.field_20812.field_3665 = true;
               this.renderCrossbar(bannerBlockEntity, matrixStack, vertexConsumerProvider, i, j);
            }

            ci.cancel();
         }
      }

   }

   @Unique
   private void renderPillar(class_2573 bannerBlockEntity, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, int j) {
      matrixStack.method_22903();
      class_2680 blockState = bannerBlockEntity.method_11010();
      matrixStack.method_22904(0.5D, 0.5D, 0.5D);
      float h = (float)(-(Integer)blockState.method_11654(class_2215.field_9924) * 360) / 16.0F;
      matrixStack.method_22907(class_7833.field_40716.rotationDegrees(h));
      matrixStack.method_22903();
      matrixStack.method_22905(0.6666667F, -0.6666667F, -0.6666667F);
      class_4588 vertexConsumer = class_1088.field_20847.method_24145(vertexConsumerProvider, class_1921::method_23572);
      this.field_20811.method_22698(matrixStack, vertexConsumer, i, j);
      matrixStack.method_22909();
      matrixStack.method_22909();
   }

   @Unique
   private void renderCrossbar(class_2573 bannerBlockEntity, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, int j) {
      matrixStack.method_22903();
      class_2680 blockState = bannerBlockEntity.method_11010();
      matrixStack.method_22904(0.5D, -0.1666666716337204D, 0.5D);
      float h = -((class_2350)blockState.method_11654(class_2546.field_11722)).method_10144();
      matrixStack.method_22907(class_7833.field_40716.rotationDegrees(h));
      matrixStack.method_22904(0.0D, -0.3125D, -0.4375D);
      matrixStack.method_22903();
      matrixStack.method_22905(0.6666667F, -0.6666667F, -0.6666667F);
      class_4588 vertexConsumer = class_1088.field_20847.method_24145(vertexConsumerProvider, class_1921::method_23572);
      this.field_20812.method_22698(matrixStack, vertexConsumer, i, j);
      matrixStack.method_22909();
      matrixStack.method_22909();
   }
}

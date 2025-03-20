package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1511;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_630;
import net.minecraft.class_892;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_892.class})
public abstract class EndCrystalEntityRendererMixin {
   @Mutable
   @Shadow
   @Final
   private static class_1921 field_21736;
   @Shadow
   @Final
   private static class_2960 field_4663;
   @Shadow
   @Final
   public class_630 field_21003;
   @Shadow
   @Final
   public class_630 field_21004;

   @Inject(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("HEAD")}
   )
   private void render(class_1511 endCrystalEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, CallbackInfo ci) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      field_21736 = class_1921.method_23580(module.isActive() && (Boolean)module.crystals.get() && !(Boolean)module.crystalsTexture.get() ? Chams.BLANK : field_4663);
   }

   @ModifyArgs(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V",
   ordinal = 0
)
   )
   private void modifyScale(Args args) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.crystals.get()) {
         args.set(0, 2.0F * ((Double)module.crystalsScale.get()).floatValue());
         args.set(1, 2.0F * ((Double)module.crystalsScale.get()).floatValue());
         args.set(2, 2.0F * ((Double)module.crystalsScale.get()).floatValue());
      }
   }

   @Redirect(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/entity/EndCrystalEntityRenderer;getYOffset(Lnet/minecraft/entity/decoration/EndCrystalEntity;F)F"
)
   )
   private float getYOff(class_1511 crystal, float tickDelta) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.crystals.get()) {
         float f = (float)crystal.field_7034 + tickDelta;
         float g = class_3532.method_15374(f * 0.2F) / 2.0F + 0.5F;
         g = (g * g + g) * 0.4F * ((Double)module.crystalsBounce.get()).floatValue();
         return g - 1.4F;
      } else {
         return class_892.method_23155(crystal, tickDelta);
      }
   }

   @ModifyArgs(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;"
)
   )
   private void modifySpeed(Args args) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.crystals.get()) {
         args.set(0, (Float)args.get(0) * ((Double)module.crystalsRotationSpeed.get()).floatValue());
      }
   }

   @Redirect(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
   ordinal = 3
)
   )
   private void modifyCore(class_630 modelPart, class_4587 matrices, class_4588 vertices, int light, int overlay) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.crystals.get()) {
         if ((Boolean)module.renderCore.get()) {
            Color color = (Color)module.crystalsCoreColor.get();
            this.field_21003.method_22699(matrices, vertices, light, overlay, color.getPacked());
         }

      } else {
         this.field_21003.method_22698(matrices, vertices, light, overlay);
      }
   }

   @Redirect(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
   ordinal = 1
)
   )
   private void modifyFrame1(class_630 modelPart, class_4587 matrices, class_4588 vertices, int light, int overlay) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.crystals.get()) {
         if ((Boolean)module.renderFrame1.get()) {
            Color color = (Color)module.crystalsFrame1Color.get();
            this.field_21004.method_22699(matrices, vertices, light, overlay, color.getPacked());
         }

      } else {
         this.field_21004.method_22698(matrices, vertices, light, overlay);
      }
   }

   @Redirect(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V",
   ordinal = 2
)
   )
   private void modifyFrame2(class_630 modelPart, class_4587 matrices, class_4588 vertices, int light, int overlay) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.crystals.get()) {
         if ((Boolean)module.renderFrame2.get()) {
            Color color = (Color)module.crystalsFrame2Color.get();
            this.field_21004.method_22699(matrices, vertices, light, overlay, color.getPacked());
         }

      } else {
         this.field_21004.method_22698(matrices, vertices, light, overlay);
      }
   }
}

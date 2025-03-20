package meteordevelopment.meteorclient.mixin;

import com.google.common.base.MoreObjects;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.ArmRenderEvent;
import meteordevelopment.meteorclient.events.render.HeldItemRendererEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.HandView;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_759.class})
public abstract class HeldItemRendererMixin {
   @Shadow
   private float field_4043;
   @Shadow
   private float field_4052;
   @Shadow
   private class_1799 field_4047;
   @Shadow
   private class_1799 field_4048;

   @ModifyVariable(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = @At(
   value = "STORE",
   ordinal = 0
),
      index = 6
   )
   private float modifySwing(float swingProgress) {
      HandView module = (HandView)Modules.get().get(HandView.class);
      class_1268 hand = (class_1268)MoreObjects.firstNonNull(MeteorClient.mc.field_1724.field_6266, class_1268.field_5808);
      if (module.isActive()) {
         if (hand == class_1268.field_5810 && !MeteorClient.mc.field_1724.method_6079().method_7960()) {
            return swingProgress + ((Double)module.offSwing.get()).floatValue();
         }

         if (hand == class_1268.field_5808 && !MeteorClient.mc.field_1724.method_6047().method_7960()) {
            return swingProgress + ((Double)module.mainSwing.get()).floatValue();
         }
      }

      return swingProgress;
   }

   @Redirect(
      method = {"updateHeldItems"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"
)
   )
   private boolean redirectSwapping(class_1799 left, class_1799 right) {
      return this.showSwapping(left, right);
   }

   @ModifyArg(
      method = {"updateHeldItems"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F",
   ordinal = 2
),
      index = 0
   )
   private float modifyEquipProgressMainhand(float value) {
      float f = MeteorClient.mc.field_1724.method_7261(1.0F);
      float modified = ((HandView)Modules.get().get(HandView.class)).oldAnimations() ? 1.0F : f * f * f;
      return (this.showSwapping(this.field_4047, MeteorClient.mc.field_1724.method_6047()) ? modified : 0.0F) - this.field_4043;
   }

   @ModifyArg(
      method = {"updateHeldItems"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F",
   ordinal = 3
),
      index = 0
   )
   private float modifyEquipProgressOffhand(float value) {
      return (float)(this.showSwapping(this.field_4048, MeteorClient.mc.field_1724.method_6079()) ? 1 : 0) - this.field_4052;
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
)}
   )
   private void onRenderItem(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
      MeteorClient.EVENT_BUS.post((Object)HeldItemRendererEvent.get(hand, matrices));
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"
)}
   )
   private void onRenderArm(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo ci) {
      MeteorClient.EVENT_BUS.post((Object)ArmRenderEvent.get(hand, matrices));
   }

   @Inject(
      method = {"applyEatOrDrinkTransformation"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/lang/Math;pow(DD)D",
   shift = Shift.BEFORE
)},
      cancellable = true
   )
   private void cancelTransformations(class_4587 matrices, float tickDelta, class_1306 arm, class_1799 stack, class_1657 player, CallbackInfo ci) {
      if (((HandView)Modules.get().get(HandView.class)).disableFoodAnimation()) {
         ci.cancel();
      }

   }

   @Unique
   private boolean showSwapping(class_1799 stack1, class_1799 stack2) {
      return !((HandView)Modules.get().get(HandView.class)).showSwapping() || class_1799.method_7973(stack1, stack2);
   }
}

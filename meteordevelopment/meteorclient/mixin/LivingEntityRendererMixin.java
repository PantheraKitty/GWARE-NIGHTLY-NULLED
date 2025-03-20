package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.managers.RotationManager;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_268;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_583;
import net.minecraft.class_922;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_922.class})
public abstract class LivingEntityRendererMixin<T extends class_1309, M extends class_583<T>> {
   @Unique
   private class_1309 lastEntity;
   @Unique
   private float originalYaw;
   @Unique
   private float originalHeadYaw;
   @Unique
   private float originalBodyYaw;
   @Unique
   private float originalPitch;
   @Unique
   private float originalPrevYaw;
   @Unique
   private float originalPrevHeadYaw;
   @Unique
   private float originalPrevBodyYaw;

   @Shadow
   @Nullable
   protected abstract class_1921 method_24302(T var1, boolean var2, boolean var3, boolean var4);

   @ModifyExpressionValue(
      method = {"hasLabel(Lnet/minecraft/entity/LivingEntity;)Z"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;"
)}
   )
   private class_1297 hasLabelGetCameraEntityProxy(class_1297 cameraEntity) {
      return Modules.get().isActive(Freecam.class) ? null : cameraEntity;
   }

   @ModifyVariable(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      ordinal = 2,
      at = @At(
   value = "STORE",
   ordinal = 0
)
   )
   public float changeYaw(float oldValue, class_1309 entity) {
      return entity.equals(MeteorClient.mc.field_1724) && Rotations.rotationTimer < 10 ? Rotations.serverYaw : oldValue;
   }

   @ModifyVariable(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      ordinal = 3,
      at = @At(
   value = "STORE",
   ordinal = 0
)
   )
   public float changeHeadYaw(float oldValue, class_1309 entity) {
      return entity.equals(MeteorClient.mc.field_1724) && Rotations.rotationTimer < 10 ? Rotations.serverYaw : oldValue;
   }

   @ModifyVariable(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      ordinal = 5,
      at = @At(
   value = "STORE",
   ordinal = 3
)
   )
   public float changePitch(float oldValue, class_1309 entity) {
      return entity.equals(MeteorClient.mc.field_1724) && Rotations.rotationTimer < 10 ? Rotations.serverPitch : oldValue;
   }

   @ModifyExpressionValue(
      method = {"hasLabel(Lnet/minecraft/entity/LivingEntity;)Z"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;getScoreboardTeam()Lnet/minecraft/scoreboard/Team;"
)}
   )
   private class_268 hasLabelClientPlayerEntityGetScoreboardTeamProxy(class_268 team) {
      return MeteorClient.mc.field_1724 == null ? null : team;
   }

   @Inject(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHead(T livingEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noDeadEntities() && livingEntity.method_29504()) {
         ci.cancel();
      }

      Chams chams = (Chams)Modules.get().get(Chams.class);
      if (chams.isActive() && chams.shouldRender(livingEntity)) {
         GL11.glEnable(32823);
         GL11.glPolygonOffset(1.0F, -1100000.0F);
      }

   }

   @Inject(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("TAIL")}
   )
   private void renderTail(T livingEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, CallbackInfo ci) {
      Chams chams = (Chams)Modules.get().get(Chams.class);
      if (chams.isActive() && chams.shouldRender(livingEntity)) {
         GL11.glPolygonOffset(1.0F, 1100000.0F);
         GL11.glDisable(32823);
      }

   }

   @ModifyArgs(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V",
   ordinal = 1
)
   )
   private void modifyScale(Args args, T livingEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.players.get() && livingEntity instanceof class_1657) {
         if (!(Boolean)module.ignoreSelf.get() || livingEntity != MeteorClient.mc.field_1724) {
            args.set(0, -((Double)module.playersScale.get()).floatValue());
            args.set(1, -((Double)module.playersScale.get()).floatValue());
            args.set(2, ((Double)module.playersScale.get()).floatValue());
         }
      }
   }

   @ModifyArgs(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
)
   )
   private void modifyColor(Args args, T livingEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.players.get() && livingEntity instanceof class_1657) {
         if (!(Boolean)module.ignoreSelf.get() || livingEntity != MeteorClient.mc.field_1724) {
            Color color = PlayerUtils.getPlayerColor((class_1657)livingEntity, (Color)module.playersColor.get());
            args.set(4, color.getPacked());
         }
      }
   }

   @Redirect(
      method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getRenderLayer(Lnet/minecraft/entity/LivingEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;"
)
   )
   private class_1921 getRenderLayer(class_922<T, M> livingEntityRenderer, T livingEntity, boolean showBody, boolean translucent, boolean showOutline) {
      Chams module = (Chams)Modules.get().get(Chams.class);
      if (module.isActive() && (Boolean)module.players.get() && livingEntity instanceof class_1657 && !(Boolean)module.playersTexture.get()) {
         return (Boolean)module.ignoreSelf.get() && livingEntity == MeteorClient.mc.field_1724 ? this.method_24302(livingEntity, showBody, translucent, showOutline) : class_1921.method_29379(Chams.BLANK);
      } else {
         return this.method_24302(livingEntity, showBody, translucent, showOutline);
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   public void onRenderPre(T livingEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, CallbackInfo ci) {
      if (MeteorClient.mc.field_1724 != null && livingEntity == MeteorClient.mc.field_1724) {
         this.originalYaw = livingEntity.method_36454();
         this.originalHeadYaw = livingEntity.field_6241;
         this.originalBodyYaw = livingEntity.field_6283;
         this.originalPitch = livingEntity.method_36455();
         this.originalPrevYaw = livingEntity.field_5982;
         this.originalPrevHeadYaw = livingEntity.field_6259;
         this.originalPrevBodyYaw = livingEntity.field_6220;
         livingEntity.method_36456(RotationManager.getRenderYawOffset());
         livingEntity.field_6241 = RotationManager.getRotationYawHead();
         livingEntity.field_6283 = RotationManager.getRenderYawOffset();
         livingEntity.method_36457(RotationManager.getRenderPitch());
         livingEntity.field_5982 = RotationManager.getPrevRenderYawOffset();
         livingEntity.field_6259 = RotationManager.getPrevRotationYawHead();
         livingEntity.field_6220 = RotationManager.getPrevRenderYawOffset();
         livingEntity.field_6004 = RotationManager.getPrevPitch();
      }

      this.lastEntity = livingEntity;
   }

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   public void onRenderPost(T livingEntity, float f, float g, class_4587 matrixStack, class_4597 vertexConsumerProvider, int i, CallbackInfo ci) {
      if (MeteorClient.mc.field_1724 != null && livingEntity == MeteorClient.mc.field_1724) {
         livingEntity.method_36456(this.originalYaw);
         livingEntity.field_6241 = this.originalHeadYaw;
         livingEntity.field_6283 = this.originalBodyYaw;
         livingEntity.method_36457(this.originalPitch);
         livingEntity.field_5982 = this.originalPrevYaw;
         livingEntity.field_6259 = this.originalPrevHeadYaw;
         livingEntity.field_6220 = this.originalPrevBodyYaw;
         livingEntity.field_6004 = this.originalPitch;
      }

   }
}

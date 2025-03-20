package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.render.RenderAfterWorldEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.LiquidInteract;
import meteordevelopment.meteorclient.systems.modules.player.NoMiningTrace;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.render.Zoom;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3966;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import net.minecraft.class_239.class_240;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({class_757.class})
public abstract class GameRendererMixin {
   @Shadow
   @Final
   class_310 field_4015;
   @Shadow
   @Final
   private class_4184 field_18765;
   @Unique
   private Renderer3D renderer;
   @Unique
   private final class_4587 matrices = new class_4587();
   @Unique
   private boolean freecamSet = false;

   @Shadow
   public abstract void method_3190(float var1);

   @Shadow
   public abstract void method_3203();

   @Shadow
   protected abstract void method_3186(class_4587 var1, float var2);

   @Shadow
   protected abstract void method_3198(class_4587 var1, float var2);

   @Inject(
      method = {"renderWorld"},
      at = {@At(
   value = "INVOKE_STRING",
   target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
   args = {"ldc=hand"}
)},
      locals = LocalCapture.CAPTURE_FAILEXCEPTION
   )
   private void onRenderWorld(class_9779 tickCounter, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrix4f2, @Local(ordinal = 1) float tickDelta, @Local class_4587 matrixStack) {
      if (Utils.canUpdate()) {
         this.field_4015.method_16011().method_15396("meteor-client_render");
         if (this.renderer == null) {
            this.renderer = new Renderer3D();
         }

         Render3DEvent event = Render3DEvent.get(matrixStack, this.renderer, tickDelta, this.field_18765.method_19326().field_1352, this.field_18765.method_19326().field_1351, this.field_18765.method_19326().field_1350);
         RenderUtils.updateScreenCenter();
         NametagUtils.onRender(matrix4f2);
         RenderSystem.getModelViewStack().pushMatrix().mul(matrix4f2);
         this.matrices.method_22903();
         this.method_3198(this.matrices, this.field_18765.method_55437());
         if ((Boolean)this.field_4015.field_1690.method_42448().method_41753()) {
            this.method_3186(this.matrices, this.field_18765.method_55437());
         }

         RenderSystem.getModelViewStack().mul(this.matrices.method_23760().method_23761().invert());
         this.matrices.method_22909();
         RenderSystem.applyModelViewMatrix();
         this.renderer.begin();
         MeteorClient.EVENT_BUS.post((Object)event);
         this.renderer.render(matrixStack);
         RenderSystem.getModelViewStack().popMatrix();
         RenderSystem.applyModelViewMatrix();
         this.field_4015.method_16011().method_15407();
      }
   }

   @Inject(
      method = {"renderWorld"},
      at = {@At("TAIL")}
   )
   private void onRenderWorldTail(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post((Object)RenderAfterWorldEvent.get());
   }

   @ModifyReturnValue(
      method = {"findCrosshairTarget"},
      at = {@At("RETURN")}
   )
   private class_239 onUpdateTargetedEntity(class_239 original, @Local class_239 hitResult) {
      NoMiningTrace var10000 = (NoMiningTrace)Modules.get().get(NoMiningTrace.class);
      class_1297 var10001;
      if (original instanceof class_3966) {
         class_3966 ehr = (class_3966)original;
         var10001 = ehr.method_17782();
      } else {
         var10001 = null;
      }

      return var10000.canWork(var10001) && hitResult.method_17783() == class_240.field_1332 ? hitResult : original;
   }

   @Redirect(
      method = {"findCrosshairTarget"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"
)
   )
   private class_239 updateTargetedEntityEntityRayTraceProxy(class_1297 entity, double maxDistance, float tickDelta, boolean includeFluids) {
      if (Modules.get().isActive(LiquidInteract.class)) {
         class_239 result = entity.method_5745(maxDistance, tickDelta, includeFluids);
         return result.method_17783() != class_240.field_1333 ? result : entity.method_5745(maxDistance, tickDelta, true);
      } else {
         return entity.method_5745(maxDistance, tickDelta, includeFluids);
      }
   }

   @Inject(
      method = {"showFloatingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onShowFloatingItem(class_1799 floatingItem, CallbackInfo info) {
      if (floatingItem.method_7909() == class_1802.field_8288 && ((NoRender)Modules.get().get(NoRender.class)).noTotemAnimation()) {
         info.cancel();
      }

   }

   @ModifyExpressionValue(
      method = {"renderWorld"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"
)}
   )
   private float applyCameraTransformationsMathHelperLerpProxy(float original) {
      return ((NoRender)Modules.get().get(NoRender.class)).noNausea() ? 0.0F : original;
   }

   @Inject(
      method = {"renderNausea"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderNausea(class_332 context, float distortionStrength, CallbackInfo ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noNausea()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"updateCrosshairTarget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateTargetedEntityInvoke(float tickDelta, CallbackInfo info) {
      Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
      boolean highwayBuilder = Modules.get().isActive(HighwayBuilder.class);
      if ((freecam.isActive() || highwayBuilder) && this.field_4015.method_1560() != null && !this.freecamSet) {
         info.cancel();
         class_1297 cameraE = this.field_4015.method_1560();
         double x = cameraE.method_23317();
         double y = cameraE.method_23318();
         double z = cameraE.method_23321();
         double prevX = cameraE.field_6014;
         double prevY = cameraE.field_6036;
         double prevZ = cameraE.field_5969;
         float yaw = cameraE.method_36454();
         float pitch = cameraE.method_36455();
         float prevYaw = cameraE.field_5982;
         float prevPitch = cameraE.field_6004;
         if (highwayBuilder) {
            cameraE.method_36456(this.field_18765.method_19330());
            cameraE.method_36457(this.field_18765.method_19329());
         } else {
            ((IVec3d)cameraE.method_19538()).set(freecam.pos.x, freecam.pos.y - (double)cameraE.method_18381(cameraE.method_18376()), freecam.pos.z);
            cameraE.field_6014 = freecam.prevPos.x;
            cameraE.field_6036 = freecam.prevPos.y - (double)cameraE.method_18381(cameraE.method_18376());
            cameraE.field_5969 = freecam.prevPos.z;
            cameraE.method_36456(freecam.yaw);
            cameraE.method_36457(freecam.pitch);
            cameraE.field_5982 = freecam.prevYaw;
            cameraE.field_6004 = freecam.prevPitch;
         }

         this.freecamSet = true;
         this.method_3190(tickDelta);
         this.freecamSet = false;
         ((IVec3d)cameraE.method_19538()).set(x, y, z);
         cameraE.field_6014 = prevX;
         cameraE.field_6036 = prevY;
         cameraE.field_5969 = prevZ;
         cameraE.method_36456(yaw);
         cameraE.method_36457(pitch);
         cameraE.field_5982 = prevYaw;
         cameraE.field_6004 = prevPitch;
      }

   }

   @Inject(
      method = {"renderHand"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHand(class_4184 camera, float tickDelta, Matrix4f matrix4f, CallbackInfo ci) {
      if (!((Freecam)Modules.get().get(Freecam.class)).renderHands() || !((Zoom)Modules.get().get(Zoom.class)).renderHands()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void onRender(class_9779 tickCounter, boolean tick, CallbackInfo ci) {
      float tickDelta = tickCounter.method_60637(tick);
      MeteorClient.onRender(tickDelta);
   }
}

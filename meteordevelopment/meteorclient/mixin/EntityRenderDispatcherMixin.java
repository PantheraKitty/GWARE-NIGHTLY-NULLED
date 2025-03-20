package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IBox;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Hitboxes;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShaders;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4538;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({class_898.class})
public abstract class EntityRenderDispatcherMixin {
   @Shadow
   public class_4184 field_4686;

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <E extends class_1297> void render(E entity, double x, double y, double z, float yaw, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, int light, CallbackInfo info) {
      if (entity instanceof FakePlayerEntity) {
         FakePlayerEntity player = (FakePlayerEntity)entity;
         if (player.hideWhenInsideCamera) {
            int cX = class_3532.method_15357(this.field_4686.method_19326().field_1352);
            int cY = class_3532.method_15357(this.field_4686.method_19326().field_1351);
            int cZ = class_3532.method_15357(this.field_4686.method_19326().field_1350);
            if (cX == entity.method_31477() && cZ == entity.method_31479() && (cY == entity.method_31478() || cY == entity.method_31478() + 1)) {
               info.cancel();
            }
         }
      }

   }

   @Inject(
      method = {"renderHitbox"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/math/Box;FFFF)V",
   ordinal = 0
)},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private static void onRenderHitbox(class_4587 matrices, class_4588 vertices, class_1297 entity, float tickDelta, float red, float green, float blue, CallbackInfo ci, class_238 box) {
      double v = ((Hitboxes)Modules.get().get(Hitboxes.class)).getEntityValue(entity);
      if (v != 0.0D) {
         ((IBox)box).expand(v);
      }

   }

   @Inject(
      method = {"renderShadow"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onRenderShadow(class_4587 matrices, class_4597 vertexConsumers, class_1297 entity, float opacity, float tickDelta, class_4538 world, float radius, CallbackInfo info) {
      if (PostProcessShaders.rendering) {
         info.cancel();
      }

      if (((NoRender)Modules.get().get(NoRender.class)).noDeadEntities() && entity instanceof class_1309 && ((class_1309)entity).method_29504()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"getSquaredDistanceToCamera(Lnet/minecraft/entity/Entity;)D"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetSquaredDistanceToCameraEntity(class_1297 entity, CallbackInfoReturnable<Double> info) {
      if (this.field_4686 == null) {
         info.setReturnValue(0.0D);
      }

   }

   @Inject(
      method = {"getSquaredDistanceToCamera(DDD)D"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetSquaredDistanceToCameraXYZ(double x, double y, double z, CallbackInfoReturnable<Double> info) {
      if (this.field_4686 == null) {
         info.setReturnValue(0.0D);
      }

   }
}

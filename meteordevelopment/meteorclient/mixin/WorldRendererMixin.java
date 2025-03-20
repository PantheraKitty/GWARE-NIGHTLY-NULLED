package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BlockSelection;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.postprocess.EntityShader;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShaders;
import net.minecraft.class_1297;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_276;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4618;
import net.minecraft.class_757;
import net.minecraft.class_761;
import net.minecraft.class_765;
import net.minecraft.class_9779;
import net.minecraft.class_293.class_5596;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_761.class})
public abstract class WorldRendererMixin {
   @Shadow
   private class_276 field_4101;
   @Unique
   private ESP esp;

   @Shadow
   protected abstract void method_22977(class_1297 var1, double var2, double var4, double var6, float var8, class_4587 var9, class_4597 var10);

   @Inject(
      method = {"checkEmpty"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onCheckEmpty(class_4587 matrixStack, CallbackInfo info) {
      info.cancel();
   }

   @Inject(
      method = {"drawBlockOutline"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDrawHighlightedBlockOutline(class_4587 matrixStack, class_4588 vertexConsumer, class_1297 entity, double d, double e, double f, class_2338 blockPos, class_2680 blockState, CallbackInfo info) {
      if (Modules.get().isActive(BlockSelection.class)) {
         info.cancel();
      }

   }

   @ModifyArg(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V"
),
      index = 3
   )
   private boolean renderSetupTerrainModifyArg(boolean spectator) {
      return Modules.get().isActive(Freecam.class) || spectator;
   }

   @Inject(
      method = {"renderWeather"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderWeather(class_765 manager, float f, double d, double e, double g, CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noWeather()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hasBlindnessOrDarkness(class_4184 camera, CallbackInfoReturnable<Boolean> info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBlindness() || ((NoRender)Modules.get().get(NoRender.class)).noDarkness()) {
         info.setReturnValue((Object)null);
      }

   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void onRenderHead(class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, class_757 gameRenderer, class_765 lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
      PostProcessShaders.beginRender();
   }

   @Inject(
      method = {"renderEntity"},
      at = {@At("HEAD")}
   )
   private void renderEntity(class_1297 entity, double cameraX, double cameraY, double cameraZ, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, CallbackInfo info) {
      this.draw(entity, cameraX, cameraY, cameraZ, tickDelta, vertexConsumers, matrices, PostProcessShaders.CHAMS, Color.WHITE);
      this.draw(entity, cameraX, cameraY, cameraZ, tickDelta, vertexConsumers, matrices, PostProcessShaders.ENTITY_OUTLINE, ((ESP)Modules.get().get(ESP.class)).getSideColor(entity));
   }

   @Unique
   private void draw(class_1297 entity, double cameraX, double cameraY, double cameraZ, float tickDelta, class_4597 vertexConsumers, class_4587 matrices, EntityShader shader, Color color) {
      if (shader.shouldDraw(entity) && !PostProcessShaders.isCustom(vertexConsumers) && color != null) {
         class_276 prevBuffer = this.field_4101;
         this.field_4101 = shader.framebuffer;
         PostProcessShaders.rendering = true;
         shader.vertexConsumerProvider.method_23286(color.r, color.g, color.b, color.a);
         this.method_22977(entity, cameraX, cameraY, cameraZ, tickDelta, matrices, shader.vertexConsumerProvider);
         PostProcessShaders.rendering = false;
         this.field_4101 = prevBuffer;
      }

   }

   @Inject(
      method = {"render"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;draw()V"
)}
   )
   private void onRender(class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, class_757 gameRenderer, class_765 lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
      PostProcessShaders.endRender();
   }

   @ModifyExpressionValue(
      method = {"render"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/MinecraftClient;hasOutline(Lnet/minecraft/entity/Entity;)Z"
)}
   )
   private boolean shouldMobGlow(boolean original, @Local class_1297 entity) {
      if (this.getESP().isGlow() && !this.getESP().shouldSkip(entity)) {
         return this.getESP().getSideColor(entity) != null || original;
      } else {
         return original;
      }
   }

   @WrapOperation(
      method = {"render"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/OutlineVertexConsumerProvider;setColor(IIII)V"
)}
   )
   private void setGlowColor(class_4618 instance, int red, int green, int blue, int alpha, Operation<Void> original, @Local LocalRef<class_1297> entity) {
      if (this.getESP().isGlow() && !this.getESP().shouldSkip((class_1297)entity.get())) {
         Color color = this.getESP().getSideColor((class_1297)entity.get());
         if (color == null) {
            original.call(new Object[]{instance, red, green, blue, alpha});
         } else {
            instance.method_23286(color.r, color.g, color.b, color.a);
         }
      } else {
         original.call(new Object[]{instance, red, green, blue, alpha});
      }

   }

   @Inject(
      method = {"onResized"},
      at = {@At("HEAD")}
   )
   private void onResized(int width, int height, CallbackInfo info) {
      PostProcessShaders.onResized(width, height);
   }

   @Inject(
      method = {"renderEndSky"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/BufferRenderer;drawWithGlobalProgram(Lnet/minecraft/client/render/BuiltBuffer;)V"
)}
   )
   private void onRenderEndSkyDraw(class_4587 matrices, CallbackInfo info) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive() && (Boolean)ambience.endSky.get() && (Boolean)ambience.customSkyColor.get()) {
         Color customEndSkyColor = ambience.skyColor();
         class_289 tessellator = class_289.method_1348();
         class_287 bufferBuilder = tessellator.method_60827(class_5596.field_27382, class_290.field_1575);
         Matrix4f matrix4f = matrices.method_23760().method_23761();
         bufferBuilder.method_22918(matrix4f, -100.0F, -100.0F, -100.0F).method_22913(0.0F, 0.0F).method_1336(customEndSkyColor.r, customEndSkyColor.g, customEndSkyColor.b, 255);
         bufferBuilder.method_22918(matrix4f, -100.0F, -100.0F, 100.0F).method_22913(0.0F, 16.0F).method_1336(customEndSkyColor.r, customEndSkyColor.g, customEndSkyColor.b, 255);
         bufferBuilder.method_22918(matrix4f, 100.0F, -100.0F, 100.0F).method_22913(16.0F, 16.0F).method_1336(customEndSkyColor.r, customEndSkyColor.g, customEndSkyColor.b, 255);
         bufferBuilder.method_22918(matrix4f, 100.0F, -100.0F, -100.0F).method_22913(16.0F, 0.0F).method_1336(customEndSkyColor.r, customEndSkyColor.g, customEndSkyColor.b, 255);
      }

   }

   @ModifyVariable(
      method = {"getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I"},
      at = @At("STORE"),
      ordinal = 0
   )
   private static int getLightmapCoordinatesModifySkyLight(int sky) {
      return Math.max(((Fullbright)Modules.get().get(Fullbright.class)).getLuminance(class_1944.field_9284), sky);
   }

   @ModifyVariable(
      method = {"getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I"},
      at = @At("STORE"),
      ordinal = 1
   )
   private static int getLightmapCoordinatesModifyBlockLight(int sky) {
      return Math.max(((Fullbright)Modules.get().get(Fullbright.class)).getLuminance(class_1944.field_9282), sky);
   }

   @Unique
   private ESP getESP() {
      if (this.esp == null) {
         this.esp = (ESP)Modules.get().get(ESP.class);
      }

      return this.esp;
   }
}

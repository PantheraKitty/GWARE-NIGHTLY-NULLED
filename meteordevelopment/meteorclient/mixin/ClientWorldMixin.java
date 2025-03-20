package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.class_1297;
import net.minecraft.class_2246;
import net.minecraft.class_243;
import net.minecraft.class_5294;
import net.minecraft.class_638;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_5294.class_5295;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_638.class})
public abstract class ClientWorldMixin {
   @Unique
   private final class_5294 endSky = new class_5295();
   @Unique
   private final class_5294 customSky = new Ambience.Custom();

   @Shadow
   @Nullable
   public abstract class_1297 method_8469(int var1);

   @Inject(
      method = {"addEntity"},
      at = {@At("TAIL")}
   )
   private void onAddEntity(class_1297 entity, CallbackInfo info) {
      if (entity != null) {
         MeteorClient.EVENT_BUS.post((Object)EntityAddedEvent.get(entity));
      }

   }

   @Inject(
      method = {"removeEntity"},
      at = {@At("HEAD")}
   )
   private void onRemoveEntity(int entityId, class_5529 removalReason, CallbackInfo info) {
      if (this.method_8469(entityId) != null) {
         MeteorClient.EVENT_BUS.post((Object)EntityRemovedEvent.get(this.method_8469(entityId)));
      }

   }

   @Inject(
      method = {"getDimensionEffects"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetSkyProperties(CallbackInfoReturnable<class_5294> info) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive() && (Boolean)ambience.endSky.get()) {
         info.setReturnValue((Boolean)ambience.customSkyColor.get() ? this.customSky : this.endSky);
      }

   }

   @Inject(
      method = {"getSkyColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetSkyColor(class_243 cameraPos, float tickDelta, CallbackInfoReturnable<class_243> info) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive() && (Boolean)ambience.customSkyColor.get()) {
         info.setReturnValue(ambience.skyColor().getVec3d());
      }

   }

   @Inject(
      method = {"getCloudsColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetCloudsColor(float tickDelta, CallbackInfoReturnable<class_243> info) {
      Ambience ambience = (Ambience)Modules.get().get(Ambience.class);
      if (ambience.isActive() && (Boolean)ambience.customCloudColor.get()) {
         info.setReturnValue(((SettingColor)ambience.cloudColor.get()).getVec3d());
      }

   }

   @ModifyArgs(
      method = {"doRandomBlockDisplayTicks"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/world/ClientWorld;randomBlockDisplayTick(IIIILnet/minecraft/util/math/random/Random;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos$Mutable;)V"
)
   )
   private void doRandomBlockDisplayTicks(Args args) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBarrierInvis()) {
         args.set(5, class_2246.field_10499);
      }

   }
}

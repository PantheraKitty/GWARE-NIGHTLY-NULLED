package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.LivingEntityMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.JumpVelocityMultiplierEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.UpdatePlayerVelocity;
import meteordevelopment.meteorclient.mixininterface.ICamera;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.Hitboxes;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraFakeFly;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.Jesus;
import meteordevelopment.meteorclient.systems.modules.movement.NoFall;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.VanillaFakeFly;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.modules.render.FreeLook;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShaders;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1313;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_4050;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_1297.class})
public abstract class EntityMixin {
   @ModifyExpressionValue(
      method = {"updateMovementInFluid"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/fluid/FluidState;getVelocity(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/Vec3d;"
)}
   )
   private class_243 updateMovementInFluidFluidStateGetVelocity(class_243 vec) {
      if (this != MeteorClient.mc.field_1724) {
         return vec;
      } else {
         Velocity velocity = (Velocity)Modules.get().get(Velocity.class);
         if (velocity.isActive() && (Boolean)velocity.liquids.get()) {
            vec = vec.method_18805(velocity.getHorizontal(velocity.liquidsHorizontal), velocity.getVertical(velocity.liquidsVertical), velocity.getHorizontal(velocity.liquidsHorizontal));
         }

         return vec;
      }
   }

   @Inject(
      method = {"isTouchingWater"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isTouchingWater(CallbackInfoReturnable<Boolean> info) {
      if (this == MeteorClient.mc.field_1724) {
         if (((Flight)Modules.get().get(Flight.class)).isActive()) {
            info.setReturnValue(false);
         }

         if (((NoSlow)Modules.get().get(NoSlow.class)).fluidDrag()) {
            info.setReturnValue(false);
         }

      }
   }

   @Inject(
      method = {"isInLava"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isInLava(CallbackInfoReturnable<Boolean> info) {
      if (this == MeteorClient.mc.field_1724) {
         if (((Flight)Modules.get().get(Flight.class)).isActive()) {
            info.setReturnValue(false);
         }

         if (((NoSlow)Modules.get().get(NoSlow.class)).fluidDrag()) {
            info.setReturnValue(false);
         }

      }
   }

   @Inject(
      method = {"onBubbleColumnSurfaceCollision"},
      at = {@At("HEAD")}
   )
   private void onBubbleColumnSurfaceCollision(CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724) {
         Jesus jesus = (Jesus)Modules.get().get(Jesus.class);
         if (jesus.isActive()) {
            jesus.isInBubbleColumn = true;
         }

      }
   }

   @Inject(
      method = {"onBubbleColumnCollision"},
      at = {@At("HEAD")}
   )
   private void onBubbleColumnCollision(CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724) {
         Jesus jesus = (Jesus)Modules.get().get(Jesus.class);
         if (jesus.isActive()) {
            jesus.isInBubbleColumn = true;
         }

      }
   }

   @ModifyExpressionValue(
      method = {"updateSwimming"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;isSubmergedInWater()Z"
)}
   )
   private boolean isSubmergedInWater(boolean submerged) {
      if (this != MeteorClient.mc.field_1724) {
         return submerged;
      } else if (((NoSlow)Modules.get().get(NoSlow.class)).fluidDrag()) {
         return false;
      } else {
         return ((Flight)Modules.get().get(Flight.class)).isActive() ? false : submerged;
      }
   }

   @ModifyArgs(
      method = {"pushAwayFrom(Lnet/minecraft/entity/Entity;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"
)
   )
   private void onPushAwayFrom(Args args, class_1297 entity) {
      Velocity velocity = (Velocity)Modules.get().get(Velocity.class);
      if (this == MeteorClient.mc.field_1724 && velocity.isActive() && (Boolean)velocity.entityPush.get()) {
         double multiplier = (Double)velocity.entityPushAmount.get();
         args.set(0, (Double)args.get(0) * multiplier);
         args.set(2, (Double)args.get(2) * multiplier);
      } else if (entity instanceof FakePlayerEntity) {
         FakePlayerEntity player = (FakePlayerEntity)entity;
         if (player.doNotPush) {
            args.set(0, 0.0D);
            args.set(2, 0.0D);
         }
      }

   }

   @ModifyReturnValue(
      method = {"getJumpVelocityMultiplier"},
      at = {@At("RETURN")}
   )
   private float onGetJumpVelocityMultiplier(float original) {
      if (this == MeteorClient.mc.field_1724) {
         JumpVelocityMultiplierEvent event = (JumpVelocityMultiplierEvent)MeteorClient.EVENT_BUS.post((Object)JumpVelocityMultiplierEvent.get());
         return original * event.multiplier;
      } else {
         return original;
      }
   }

   @Inject(
      method = {"move"},
      at = {@At("HEAD")}
   )
   private void onMove(class_1313 type, class_243 movement, CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724) {
         MeteorClient.EVENT_BUS.post((Object)PlayerMoveEvent.get(type, movement));
      } else if (this instanceof class_1309) {
         MeteorClient.EVENT_BUS.post((Object)LivingEntityMoveEvent.get((class_1309)this, movement));
      }

   }

   @Inject(
      method = {"getTeamColorValue"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetTeamColorValue(CallbackInfoReturnable<Integer> info) {
      if (PostProcessShaders.rendering) {
         Color color = ((ESP)Modules.get().get(ESP.class)).getSideColor((class_1297)this);
         if (color != null) {
            info.setReturnValue(color.getPacked());
         }
      }

   }

   @Redirect(
      method = {"getVelocityMultiplier"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"
)
   )
   private class_2248 getVelocityMultiplierGetBlockProxy(class_2680 blockState) {
      if (this != MeteorClient.mc.field_1724) {
         return blockState.method_26204();
      } else if (blockState.method_26204() == class_2246.field_10114 && ((NoSlow)Modules.get().get(NoSlow.class)).soulSand()) {
         return class_2246.field_10340;
      } else {
         return blockState.method_26204() == class_2246.field_21211 && ((NoSlow)Modules.get().get(NoSlow.class)).honeyBlock() ? class_2246.field_10340 : blockState.method_26204();
      }
   }

   @ModifyReturnValue(
      method = {"isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z"},
      at = {@At("RETURN")}
   )
   private boolean isInvisibleToCanceller(boolean original) {
      if (!Utils.canUpdate()) {
         return original;
      } else {
         ESP esp = (ESP)Modules.get().get(ESP.class);
         return !((NoRender)Modules.get().get(NoRender.class)).noInvisibility() && (!esp.isActive() || esp.shouldSkip((class_1297)this)) ? original : false;
      }
   }

   @Inject(
      method = {"isGlowing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isGlowing(CallbackInfoReturnable<Boolean> info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noGlowing()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"getTargetingMargin"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetTargetingMargin(CallbackInfoReturnable<Float> info) {
      double v = ((Hitboxes)Modules.get().get(Hitboxes.class)).getEntityValue((class_1297)this);
      if (v != 0.0D) {
         info.setReturnValue((float)v);
      }

   }

   @Inject(
      method = {"isInvisibleTo"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsInvisibleTo(class_1657 player, CallbackInfoReturnable<Boolean> info) {
      if (player == null) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"getPose"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getPoseHook(CallbackInfoReturnable<class_4050> info) {
      if (this == MeteorClient.mc.field_1724) {
         VanillaFakeFly vanillaFakeFly = (VanillaFakeFly)Modules.get().get(VanillaFakeFly.class);
         if (vanillaFakeFly.isFlying()) {
            info.setReturnValue(class_4050.field_18076);
         } else if (((ElytraFly)Modules.get().get(ElytraFly.class)).canPacketEfly()) {
            info.setReturnValue(class_4050.field_18077);
         }
      }
   }

   @ModifyReturnValue(
      method = {"getPose"},
      at = {@At("RETURN")}
   )
   private class_4050 modifyGetPose(class_4050 original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         ElytraFakeFly fakeFly = (ElytraFakeFly)Modules.get().get(ElytraFakeFly.class);
         if (original == class_4050.field_18077 && fakeFly.isFlying()) {
            return class_4050.field_18076;
         } else {
            return original == class_4050.field_18081 && !MeteorClient.mc.field_1724.method_5715() ? class_4050.field_18076 : original;
         }
      }
   }

   @ModifyReturnValue(
      method = {"bypassesLandingEffects"},
      at = {@At("RETURN")}
   )
   private boolean cancelBounce(boolean original) {
      return ((NoFall)Modules.get().get(NoFall.class)).cancelBounce() || original;
   }

   @Inject(
      method = {"changeLookDirection"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
      if (this == MeteorClient.mc.field_1724) {
         Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
         FreeLook freeLook = (FreeLook)Modules.get().get(FreeLook.class);
         if (freecam.isActive()) {
            freecam.changeLookDirection(cursorDeltaX * 0.15D, cursorDeltaY * 0.15D);
            ci.cancel();
         } else if (Modules.get().isActive(HighwayBuilder.class)) {
            class_4184 camera = MeteorClient.mc.field_1773.method_19418();
            ((ICamera)camera).setRot((double)camera.method_19330() + cursorDeltaX * 0.15D, (double)camera.method_19329() + cursorDeltaY * 0.15D);
            ci.cancel();
         } else if (freeLook.cameraMode()) {
            freeLook.cameraYaw += (float)(cursorDeltaX / (double)((Double)freeLook.sensitivity.get()).floatValue());
            freeLook.cameraPitch += (float)(cursorDeltaY / (double)((Double)freeLook.sensitivity.get()).floatValue());
            if (Math.abs(freeLook.cameraPitch) > 90.0F) {
               freeLook.cameraPitch = freeLook.cameraPitch > 0.0F ? 90.0F : -90.0F;
            }

            ci.cancel();
         }

      }
   }

   @Inject(
      method = {"updateVelocity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void updateVelocityHook(float speed, class_243 movementInput, CallbackInfo ci) {
      if (this == MeteorClient.mc.field_1724) {
         UpdatePlayerVelocity event = new UpdatePlayerVelocity(movementInput, speed, MeteorClient.mc.field_1724.method_36454(), PlayerUtils.movementInputToVelocity(movementInput, speed, MeteorClient.mc.field_1724.method_36454()));
         MeteorClient.EVENT_BUS.post((ICancellable)event);
         if (event.isCancelled()) {
            ci.cancel();
            MeteorClient.mc.field_1724.method_18799(MeteorClient.mc.field_1724.method_18798().method_1019(event.getVelocity()));
         }
      }

   }
}

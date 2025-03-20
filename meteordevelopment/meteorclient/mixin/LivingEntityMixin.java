package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.entity.player.CanWalkOnFluidEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraFakeFly;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.movement.VanillaFakeFly;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightModes;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Bounce;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Slide;
import meteordevelopment.meteorclient.systems.modules.player.OffhandCrash;
import meteordevelopment.meteorclient.systems.modules.player.PotionSpoof;
import meteordevelopment.meteorclient.systems.modules.render.HandView;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1268;
import net.minecraft.class_1282;
import net.minecraft.class_1291;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_3610;
import net.minecraft.class_6880;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1309.class})
public abstract class LivingEntityMixin extends class_1297 {
   @Unique
   private boolean previousElytra = false;

   public LivingEntityMixin(class_1299<?> type, class_1937 world) {
      super(type, world);
   }

   @Inject(
      method = {"damage"},
      at = {@At("HEAD")}
   )
   private void onDamageHead(class_1282 source, float amount, CallbackInfoReturnable<Boolean> info) {
      if (Utils.canUpdate() && this.method_37908().field_9236) {
         MeteorClient.EVENT_BUS.post((Object)DamageEvent.get((class_1309)this, source));
      }

   }

   @ModifyReturnValue(
      method = {"canWalkOnFluid"},
      at = {@At("RETURN")}
   )
   private boolean onCanWalkOnFluid(boolean original, class_3610 fluidState) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         CanWalkOnFluidEvent event = (CanWalkOnFluidEvent)MeteorClient.EVENT_BUS.post((Object)CanWalkOnFluidEvent.get(fluidState));
         return event.walkOnFluid;
      }
   }

   @Inject(
      method = {"spawnItemParticles"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void spawnItemParticles(class_1799 stack, int count, CallbackInfo info) {
      NoRender noRender = (NoRender)Modules.get().get(NoRender.class);
      if (noRender.noEatParticles() && stack.method_57353().method_57832(class_9334.field_50075)) {
         info.cancel();
      }

   }

   @Inject(
      method = {"onEquipStack"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onEquipStack(class_1304 slot, class_1799 oldStack, class_1799 newStack, CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724 && ((OffhandCrash)Modules.get().get(OffhandCrash.class)).isAntiCrash()) {
         info.cancel();
      }

   }

   @ModifyArg(
      method = {"swingHand(Lnet/minecraft/util/Hand;)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;swingHand(Lnet/minecraft/util/Hand;Z)V"
)
   )
   private class_1268 setHand(class_1268 hand) {
      HandView handView = (HandView)Modules.get().get(HandView.class);
      if (this == MeteorClient.mc.field_1724 && handView.isActive()) {
         if (handView.swingMode.get() == HandView.SwingMode.None) {
            return hand;
         } else {
            return handView.swingMode.get() == HandView.SwingMode.Offhand ? class_1268.field_5810 : class_1268.field_5808;
         }
      } else {
         return hand;
      }
   }

   @ModifyConstant(
      method = {"getHandSwingDuration"},
      constant = {@Constant(
   intValue = 6
)}
   )
   private int getHandSwingDuration(int constant) {
      if (this != MeteorClient.mc.field_1724) {
         return constant;
      } else {
         return ((HandView)Modules.get().get(HandView.class)).isActive() && MeteorClient.mc.field_1690.method_31044().method_31034() ? (Integer)((HandView)Modules.get().get(HandView.class)).swingSpeed.get() : constant;
      }
   }

   @ModifyReturnValue(
      method = {"isFallFlying"},
      at = {@At("RETURN")}
   )
   private boolean isFallFlyingHook(boolean original) {
      return this == MeteorClient.mc.field_1724 && ((ElytraFly)Modules.get().get(ElytraFly.class)).canPacketEfly() ? true : original;
   }

   @Inject(
      method = {"isFallFlying"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void recastOnLand(CallbackInfoReturnable<Boolean> cir) {
      boolean elytra = (Boolean)cir.getReturnValue();
      ElytraFly elytraFly = (ElytraFly)Modules.get().get(ElytraFly.class);
      ElytraFakeFly fakeFly = (ElytraFakeFly)Modules.get().get(ElytraFakeFly.class);
      VanillaFakeFly vanillaFakeFly = (VanillaFakeFly)Modules.get().get(VanillaFakeFly.class);
      if (this == MeteorClient.mc.field_1724 && fakeFly.isFlying()) {
         cir.setReturnValue(false);
      } else if (this == MeteorClient.mc.field_1724 && vanillaFakeFly.isFlying()) {
         if (vanillaFakeFly.isBounce()) {
            cir.setReturnValue(false);
         } else {
            cir.setReturnValue(true);
         }

      } else {
         if (this.previousElytra && !elytra && elytraFly.isActive() && (elytraFly.flightMode.get() == ElytraFlightModes.Bounce || elytraFly.flightMode.get() == ElytraFlightModes.Slide)) {
            if (elytraFly.flightMode.get() == ElytraFlightModes.Bounce) {
               cir.setReturnValue(Bounce.recastElytra(MeteorClient.mc.field_1724));
            } else if (elytraFly.flightMode.get() == ElytraFlightModes.Slide) {
               cir.setReturnValue(Slide.recastElytra(MeteorClient.mc.field_1724));
            }
         }

         this.previousElytra = elytra;
      }
   }

   @ModifyExpressionValue(
      method = {"travel"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;isFallFlying()Z"
)}
   )
   private boolean overrideTravelIsFallFlying(boolean original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         ElytraFakeFly fakeFly = (ElytraFakeFly)Modules.get().get(ElytraFakeFly.class);
         VanillaFakeFly vanillaFakeFly = (VanillaFakeFly)Modules.get().get(VanillaFakeFly.class);
         if (fakeFly.isFlying()) {
            return true;
         } else {
            return vanillaFakeFly.isFlying() ? true : original;
         }
      }
   }

   @ModifyExpressionValue(
      method = {"isInSwimmingPose"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;isFallFlying()Z"
)}
   )
   private boolean overrideIsInSwimmingPosIsFallFlying(boolean original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         ElytraFakeFly fakeFly = (ElytraFakeFly)Modules.get().get(ElytraFakeFly.class);
         return fakeFly.isFlying() ? true : original;
      }
   }

   @ModifyReturnValue(
      method = {"hasStatusEffect"},
      at = {@At("RETURN")}
   )
   private boolean hasStatusEffect(boolean original, class_6880<class_1291> effect) {
      return ((PotionSpoof)Modules.get().get(PotionSpoof.class)).shouldBlock((class_1291)effect.comp_349()) ? false : original;
   }

   @ModifyExpressionValue(
      method = {"jump"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"
)}
   )
   private float modifyGetYaw(float original) {
      if (this != MeteorClient.mc.field_1724) {
         return original;
      } else {
         Sprint s = (Sprint)Modules.get().get(Sprint.class);
         if (s.rageSprint() && (Boolean)s.jumpFix.get()) {
            float forward = Math.signum(MeteorClient.mc.field_1724.field_3913.field_3905);
            float strafe = 90.0F * Math.signum(MeteorClient.mc.field_1724.field_3913.field_3907);
            if (forward != 0.0F) {
               strafe *= forward * 0.5F;
            }

            original -= strafe;
            if (forward < 0.0F) {
               original -= 180.0F;
            }

            return original;
         } else {
            return original;
         }
      }
   }

   @ModifyExpressionValue(
      method = {"jump"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;isSprinting()Z"
)}
   )
   private boolean modifyIsSprinting(boolean original) {
      if (this == MeteorClient.mc.field_1724 && ((Sprint)Modules.get().get(Sprint.class)).rageSprint()) {
         return original && (Math.abs(MeteorClient.mc.field_1724.field_3913.field_3905) > 1.0E-5F || Math.abs(MeteorClient.mc.field_1724.field_3913.field_3907) > 1.0E-5F);
      } else {
         return original;
      }
   }

   @Inject(
      method = {"takeKnockback(DDD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void disableKnockback(double strength, double x, double z, CallbackInfo info) {
      if (this == MeteorClient.mc.field_1724 && (Boolean)((Velocity)Modules.get().get(Velocity.class)).livingEntityKnockback.get()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"isClimbing"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void overrideIsClimbing(CallbackInfoReturnable<Boolean> info) {
      if (this == MeteorClient.mc.field_1724 && ((NoSlow)Modules.get().get(NoSlow.class)).climbing()) {
         info.cancel();
      }

   }
}

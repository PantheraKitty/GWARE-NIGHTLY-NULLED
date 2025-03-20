package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DamageEvent;
import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.entity.VehicleMoveEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.systems.managers.RotationManager;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.GrimDisabler;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Scaffold;
import meteordevelopment.meteorclient.systems.modules.movement.Sneak;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.player.Portals;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1104;
import net.minecraft.class_1282;
import net.minecraft.class_243;
import net.minecraft.class_2833;
import net.minecraft.class_2848;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_742;
import net.minecraft.class_744;
import net.minecraft.class_746;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2828.class_2831;
import net.minecraft.class_2828.class_5911;
import net.minecraft.class_2848.class_2849;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_746.class})
public abstract class ClientPlayerEntityMixin extends class_742 {
   @Shadow
   public class_744 field_3913;
   @Shadow
   @Final
   public class_634 field_3944;
   @Shadow
   @Final
   private List<class_1104> field_3933;
   @Shadow
   private boolean field_3927;
   @Shadow
   private double field_3926;
   @Shadow
   private double field_3940;
   @Shadow
   private double field_3924;
   @Shadow
   private float field_3941;
   @Shadow
   private float field_3925;
   @Shadow
   private boolean field_3920;
   @Shadow
   private boolean field_3936;
   @Shadow
   private int field_3923;

   public ClientPlayerEntityMixin(class_638 world, GameProfile profile) {
      super(world, profile);
   }

   @Inject(
      method = {"dropSelectedItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> info) {
      if (((DropItemsEvent)MeteorClient.EVENT_BUS.post((ICancellable)DropItemsEvent.get(this.method_6047()))).isCancelled()) {
         info.setReturnValue(false);
      }

   }

   @Redirect(
      method = {"tickNausea"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"
)
   )
   private class_437 updateNauseaGetCurrentScreenProxy(class_310 client) {
      return Modules.get().isActive(Portals.class) ? null : client.field_1755;
   }

   @ModifyExpressionValue(
      method = {"tickMovement"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
)}
   )
   private boolean redirectUsingItem(boolean isUsingItem) {
      return ((NoSlow)Modules.get().get(NoSlow.class)).items() ? false : isUsingItem;
   }

   @Inject(
      method = {"isSneaking"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onIsSneaking(CallbackInfoReturnable<Boolean> info) {
      if (((Scaffold)Modules.get().get(Scaffold.class)).scaffolding()) {
         info.setReturnValue(false);
      }

      if (((Flight)Modules.get().get(Flight.class)).noSneak()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"shouldSlowDown"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onShouldSlowDown(CallbackInfoReturnable<Boolean> info) {
      if (((NoSlow)Modules.get().get(NoSlow.class)).sneaking()) {
         info.setReturnValue(this.method_20448());
      }

      if (this.method_20448() && ((NoSlow)Modules.get().get(NoSlow.class)).crawling()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPushOutOfBlocks(double x, double d, CallbackInfo info) {
      Velocity velocity = (Velocity)Modules.get().get(Velocity.class);
      if (velocity.isActive() && (Boolean)velocity.blocks.get()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"damage"},
      at = {@At("HEAD")}
   )
   private void onDamage(class_1282 source, float amount, CallbackInfoReturnable<Boolean> info) {
      if (Utils.canUpdate() && this.method_37908().field_9236 && this.method_33190()) {
         MeteorClient.EVENT_BUS.post((Object)DamageEvent.get(this, source));
      }

   }

   @ModifyExpressionValue(
      method = {"canSprint"},
      at = {@At(
   value = "CONSTANT",
   args = {"floatValue=6.0f"}
)}
   )
   private float onHunger(float constant) {
      return ((NoSlow)Modules.get().get(NoSlow.class)).hunger() ? -1.0F : constant;
   }

   @ModifyExpressionValue(
      method = {"sendMovementPackets"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSneaking()Z"
)}
   )
   private boolean isSneaking(boolean sneaking) {
      return ((Sneak)Modules.get().get(Sneak.class)).doPacket() || ((NoSlow)Modules.get().get(NoSlow.class)).airStrict() || sneaking;
   }

   @Inject(
      method = {"tickMovement"},
      at = {@At("HEAD")}
   )
   private void preTickMovement(CallbackInfo ci) {
      MeteorClient.EVENT_BUS.post((Object)PlayerTickMovementEvent.get());
   }

   @ModifyExpressionValue(
      method = {"canStartSprinting"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z"
)}
   )
   private boolean modifyIsWalking(boolean original) {
      if (!((Sprint)Modules.get().get(Sprint.class)).rageSprint()) {
         return original;
      } else {
         float forwards = Math.abs(this.field_3913.field_3907);
         float sideways = Math.abs(this.field_3913.field_3905);
         return this.method_5869() ? forwards > 1.0E-5F || sideways > 1.0E-5F : (double)forwards > 0.8D || (double)sideways > 0.8D;
      }
   }

   @ModifyExpressionValue(
      method = {"tickMovement"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z"
)}
   )
   private boolean modifyMovement(boolean original) {
      if (!((Sprint)Modules.get().get(Sprint.class)).rageSprint()) {
         return original;
      } else {
         return Math.abs(this.field_3913.field_3907) > 1.0E-5F || Math.abs(this.field_3913.field_3905) > 1.0E-5F;
      }
   }

   @WrapWithCondition(
      method = {"tickMovement"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V",
   ordinal = 3
)}
   )
   private boolean wrapSetSprinting(class_746 instance, boolean b) {
      return !((Sprint)Modules.get().get(Sprint.class)).rageSprint();
   }

   @Shadow
   private void method_46742() {
   }

   @Shadow
   private void method_3136() {
   }

   @Shadow
   protected boolean method_3134() {
      return false;
   }

   @Shadow
   public abstract float method_5695(float var1);

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void sendMovementPacketsOverwrite(CallbackInfo ci) {
      ci.cancel();
      this.method_46742();
      if (this.method_5715() != this.field_3936) {
         class_2849 mode = this.method_5715() ? class_2849.field_12979 : class_2849.field_12984;
         this.field_3944.method_52787(new class_2848(this, mode));
         this.field_3936 = this.method_5715();
      }

      if (this.method_3134()) {
         SendMovementPacketsEvent.Pre updateEvent = new SendMovementPacketsEvent.Pre();
         MeteorClient.EVENT_BUS.post((Object)updateEvent);
         double d = this.method_23317() - this.field_3926;
         double e = this.method_23318() - this.field_3940;
         double f = this.method_23321() - this.field_3924;
         float yaw = this.method_36454();
         float pitch = this.method_36455();
         SendMovementPacketsEvent.Rotation movementPacketsEvent = new SendMovementPacketsEvent.Rotation(yaw, pitch);
         MeteorClient.EVENT_BUS.post((Object)movementPacketsEvent);
         yaw = movementPacketsEvent.yaw;
         pitch = movementPacketsEvent.pitch;
         MeteorClient.ROTATION.rotationYaw = yaw;
         MeteorClient.ROTATION.rotationPitch = pitch;
         double deltaYaw = (double)(yaw - MeteorClient.ROTATION.lastYaw);
         double deltaPitch = (double)(pitch - MeteorClient.ROTATION.lastPitch);
         ++this.field_3923;
         boolean positionChanged = class_3532.method_41190(d, e, f) > class_3532.method_33723(2.0E-4D) || this.field_3923 >= 20;
         boolean rotationChanged = deltaYaw != 0.0D || deltaPitch != 0.0D;
         float sendYaw = yaw;
         boolean forceFull = movementPacketsEvent.forceFull;
         if (rotationChanged && movementPacketsEvent.forceFullOnRotate) {
            forceFull = true;
         }

         if (((GrimDisabler)Modules.get().get(GrimDisabler.class)).shouldSetYawOverflowRotation()) {
            sendYaw = encodeDegrees(yaw, 100000);
            RotationManager.sendDisablerPacket = true;
            RotationManager.lastActualYaw = yaw;
         }

         if (this.method_5765()) {
            class_243 vec3d = this.method_18798();
            this.field_3944.method_52787(new class_2830(vec3d.field_1352, -999.0D, vec3d.field_1350, sendYaw, pitch, this.method_24828()));
            positionChanged = false;
         } else if (!forceFull && (!positionChanged || !rotationChanged)) {
            if (positionChanged) {
               this.field_3944.method_52787(new class_2829(this.method_23317(), this.method_23318(), this.method_23321(), this.method_24828()));
            } else if (rotationChanged) {
               this.field_3944.method_52787(new class_2831(sendYaw, pitch, this.method_24828()));
            } else if (this.field_3920 != this.method_24828()) {
               this.field_3944.method_52787(new class_5911(this.method_24828()));
            }
         } else {
            this.field_3944.method_52787(new class_2830(this.method_23317(), this.method_23318(), this.method_23321(), sendYaw, pitch, this.method_24828()));
         }

         if (positionChanged) {
            this.field_3926 = this.method_23317();
            this.field_3940 = this.method_23318();
            this.field_3924 = this.method_23321();
            this.field_3923 = 0;
         }

         if (rotationChanged) {
            this.field_3941 = yaw;
            this.field_3925 = pitch;
         }

         this.field_3920 = this.method_24828();
         this.field_3927 = (Boolean)MeteorClient.mc.field_1690.method_42423().method_41753();
      }

      MeteorClient.EVENT_BUS.post((Object)(new SendMovementPacketsEvent.Post()));
   }

   @Inject(
      method = {"tick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   ordinal = 2
)},
      cancellable = true
   )
   private void beforeSendVehicleMovePacket(CallbackInfo ci) {
      VehicleMoveEvent event = (VehicleMoveEvent)MeteorClient.EVENT_BUS.post((Object)VehicleMoveEvent.get(new class_2833(MeteorClient.mc.field_1724.method_5668()), MeteorClient.mc.field_1724.method_5668()));
      if (event.packet != null) {
         MeteorClient.mc.method_1562().method_52787(event.packet);
      }

      ci.cancel();
   }

   private static float encodeDegrees(float degrees, int multiplier) {
      return degrees + (float)multiplier * 360.0F;
   }
}

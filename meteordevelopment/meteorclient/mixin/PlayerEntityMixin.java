package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.entity.player.ClipAtLedgeEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerJumpEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerTravelEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.SoundBlocker;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Scaffold;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.player.Reach;
import meteordevelopment.meteorclient.systems.modules.player.SpeedMine;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1656;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_3965;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1657.class})
public abstract class PlayerEntityMixin extends class_1309 {
   @Shadow
   public abstract class_1656 method_31549();

   protected PlayerEntityMixin(class_1299<? extends class_1309> entityType, class_1937 world) {
      super(entityType, world);
   }

   @Inject(
      method = {"clipAtLedge"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void clipAtLedge(CallbackInfoReturnable<Boolean> info) {
      if (this.method_37908().field_9236) {
         ClipAtLedgeEvent event = (ClipAtLedgeEvent)MeteorClient.EVENT_BUS.post((Object)ClipAtLedgeEvent.get());
         if (event.isSet()) {
            info.setReturnValue(event.isClip());
         }

      }
   }

   @Inject(
      method = {"dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDropItem(class_1799 stack, boolean bl, boolean bl2, CallbackInfoReturnable<class_1542> info) {
      if (this.method_37908().field_9236 && !stack.method_7960() && ((DropItemsEvent)MeteorClient.EVENT_BUS.post((ICancellable)DropItemsEvent.get(stack))).isCancelled()) {
         info.cancel();
      }

   }

   @ModifyReturnValue(
      method = {"getBlockBreakingSpeed"},
      at = {@At("RETURN")}
   )
   public float onGetBlockBreakingSpeed(float breakSpeed, class_2680 block) {
      if (!this.method_37908().field_9236) {
         return breakSpeed;
      } else {
         SpeedMine speedMine = (SpeedMine)Modules.get().get(SpeedMine.class);
         if (speedMine.isActive() && speedMine.mode.get() == SpeedMine.Mode.Normal && speedMine.filter(block.method_26204())) {
            float breakSpeedMod = (float)((double)breakSpeed * (Double)speedMine.modifier.get());
            class_239 var6 = MeteorClient.mc.field_1765;
            if (var6 instanceof class_3965) {
               class_3965 bhr = (class_3965)var6;
               class_2338 pos = bhr.method_17777();
               return !((Double)speedMine.modifier.get() < 1.0D) && BlockUtils.canInstaBreak(pos, breakSpeed) != BlockUtils.canInstaBreak(pos, breakSpeedMod) ? 0.9F / BlockUtils.calcBlockBreakingDelta2(pos, 1.0F) : breakSpeedMod;
            } else {
               return breakSpeed;
            }
         } else {
            return breakSpeed;
         }
      }
   }

   @Inject(
      method = {"jump"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void dontJump(CallbackInfo info) {
      if (this.method_37908().field_9236) {
         Anchor module = (Anchor)Modules.get().get(Anchor.class);
         if (module.isActive() && module.cancelJump) {
            info.cancel();
         } else if (((Scaffold)Modules.get().get(Scaffold.class)).towering()) {
            info.cancel();
         }

      }
   }

   @ModifyReturnValue(
      method = {"getMovementSpeed"},
      at = {@At("RETURN")}
   )
   private float onGetMovementSpeed(float original) {
      if (!this.method_37908().field_9236) {
         return original;
      } else if (!((NoSlow)Modules.get().get(NoSlow.class)).slowness()) {
         return original;
      } else {
         float walkSpeed = this.method_31549().method_7253();
         if (original < walkSpeed) {
            return this.method_5624() ? (float)((double)walkSpeed * 1.300000011920929D) : walkSpeed;
         } else {
            return original;
         }
      }
   }

   @Inject(
      method = {"getOffGroundSpeed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetOffGroundSpeed(CallbackInfoReturnable<Float> info) {
      if (this.method_37908().field_9236) {
         float speed = ((Flight)Modules.get().get(Flight.class)).getOffGroundSpeed();
         if (speed != -1.0F) {
            info.setReturnValue(speed);
         }

      }
   }

   @WrapWithCondition(
      method = {"attack"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
)}
   )
   private boolean keepSprint$setVelocity(class_1657 instance, class_243 vec3d) {
      return ((Sprint)Modules.get().get(Sprint.class)).stopSprinting();
   }

   @WrapWithCondition(
      method = {"attack"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"
)}
   )
   private boolean keepSprint$setSprinting(class_1657 instance, boolean b) {
      return ((Sprint)Modules.get().get(Sprint.class)).stopSprinting();
   }

   @ModifyReturnValue(
      method = {"getBlockInteractionRange"},
      at = {@At("RETURN")}
   )
   private double modifyBlockInteractionRange(double original) {
      return Math.max(0.0D, original + ((Reach)Modules.get().get(Reach.class)).blockReach());
   }

   @ModifyReturnValue(
      method = {"getEntityInteractionRange"},
      at = {@At("RETURN")}
   )
   private double modifyEntityInteractionRange(double original) {
      return Math.max(0.0D, original + ((Reach)Modules.get().get(Reach.class)).entityReach());
   }

   @Inject(
      method = {"jump"},
      at = {@At("HEAD")}
   )
   private void onJumpPre(CallbackInfo ci) {
      MeteorClient.EVENT_BUS.post((Object)(new PlayerJumpEvent.Pre()));
   }

   @Inject(
      method = {"jump"},
      at = {@At("RETURN")}
   )
   private void onJumpPost(CallbackInfo ci) {
      MeteorClient.EVENT_BUS.post((Object)(new PlayerJumpEvent.Post()));
   }

   @Inject(
      method = {"travel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onTravelPre(class_243 movementInput, CallbackInfo ci) {
      class_1657 player = (class_1657)this;
      if (player == MeteorClient.mc.field_1724) {
         PlayerTravelEvent.Pre event = new PlayerTravelEvent.Pre();
         MeteorClient.EVENT_BUS.post((ICancellable)event);
         if (event.isCancelled()) {
            ci.cancel();
            PlayerTravelEvent.Post forcedPostEvent = new PlayerTravelEvent.Post();
            MeteorClient.EVENT_BUS.post((ICancellable)forcedPostEvent);
         }

      }
   }

   @Inject(
      method = {"travel"},
      at = {@At("RETURN")}
   )
   private void onTravelPost(class_243 movementInput, CallbackInfo ci) {
      class_1657 player = (class_1657)this;
      if (player == MeteorClient.mc.field_1724) {
         PlayerTravelEvent.Post event = new PlayerTravelEvent.Post();
         MeteorClient.EVENT_BUS.post((ICancellable)event);
      }
   }

   @Redirect(
      method = {"attack"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"
)
   )
   private void poseNotCollide(class_1937 instance, class_1657 except, double x, double y, double z, class_3414 sound, class_3419 category, float volume, float pitch) {
      SoundBlocker soundBlocker = (SoundBlocker)Modules.get().get(SoundBlocker.class);
      if (soundBlocker.isActive()) {
         instance.method_43128(except, x, y, z, sound, category, (float)((double)volume * soundBlocker.getCrystalHitVolume()), pitch);
      } else {
         instance.method_43128(except, x, y, z, sound, category, volume, pitch);
      }
   }
}

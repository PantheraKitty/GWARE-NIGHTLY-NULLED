package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.BlockBreakingCooldownEvent;
import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractEntityEvent;
import meteordevelopment.meteorclient.events.entity.player.InteractItemEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.systems.modules.player.BreakDelay;
import meteordevelopment.meteorclient.systems.modules.player.SpeedMine;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_3965;
import net.minecraft.class_634;
import net.minecraft.class_636;
import net.minecraft.class_746;
import net.minecraft.class_2846.class_2847;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_636.class})
public abstract class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager {
   @Shadow
   private int field_3716;
   @Shadow
   @Final
   private class_634 field_3720;

   @Shadow
   protected abstract void method_2911();

   @Shadow
   public abstract void method_2906(int var1, int var2, int var3, class_1713 var4, class_1657 var5);

   @Shadow
   public abstract boolean method_2899(class_2338 var1);

   @Inject(
      method = {"clickSlot"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onClickSlot(int syncId, int slotId, int button, class_1713 actionType, class_1657 player, CallbackInfo info) {
      if (actionType == class_1713.field_7795 && slotId >= 0 && slotId < player.field_7512.field_7761.size()) {
         if (((DropItemsEvent)MeteorClient.EVENT_BUS.post((ICancellable)DropItemsEvent.get(((class_1735)player.field_7512.field_7761.get(slotId)).method_7677()))).isCancelled()) {
            info.cancel();
         }
      } else if (slotId == -999 && ((DropItemsEvent)MeteorClient.EVENT_BUS.post((ICancellable)DropItemsEvent.get(player.field_7512.method_34255()))).isCancelled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"clickSlot"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onClickArmorSlot(int syncId, int slotId, int button, class_1713 actionType, class_1657 player, CallbackInfo ci) {
      if (((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).armorStorage()) {
         class_1703 screenHandler = player.field_7512;
         if (screenHandler instanceof class_1723 && slotId >= 5 && slotId <= 8) {
            int armorSlot = 8 - slotId + 36;
            if (actionType == class_1713.field_7790 && !screenHandler.method_34255().method_7960()) {
               this.method_2906(syncId, 17, armorSlot, class_1713.field_7791, player);
               this.method_2906(syncId, 17, button, class_1713.field_7790, player);
               this.method_2906(syncId, 17, armorSlot, class_1713.field_7791, player);
               ci.cancel();
            } else if (actionType == class_1713.field_7791) {
               if (button >= 10) {
                  this.method_2906(syncId, 45, armorSlot, class_1713.field_7791, player);
                  ci.cancel();
               } else {
                  this.method_2906(syncId, 36 + button, armorSlot, class_1713.field_7791, player);
                  ci.cancel();
               }
            }
         }

      }
   }

   @Inject(
      method = {"attackBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAttackBlock(class_2338 blockPos, class_2350 direction, CallbackInfoReturnable<Boolean> info) {
      if (((StartBreakingBlockEvent)MeteorClient.EVENT_BUS.post((ICancellable)StartBreakingBlockEvent.get(blockPos, direction))).isCancelled()) {
         info.cancel();
      } else {
         SpeedMine sm = (SpeedMine)Modules.get().get(SpeedMine.class);
         class_2680 state = MeteorClient.mc.field_1687.method_8320(blockPos);
         if (!sm.instamine() || !sm.filter(state.method_26204())) {
            return;
         }

         if (state.method_26165(MeteorClient.mc.field_1724, MeteorClient.mc.field_1687, blockPos) > 0.5F) {
            this.method_2899(blockPos);
            this.field_3720.method_52787(new class_2846(class_2847.field_12968, blockPos, direction));
            this.field_3720.method_52787(new class_2846(class_2847.field_12973, blockPos, direction));
            info.setReturnValue(true);
         }
      }

   }

   @Inject(
      method = {"interactBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void interactBlock(class_746 player, class_1268 hand, class_3965 hitResult, CallbackInfoReturnable<class_1269> cir) {
      if (((InteractBlockEvent)MeteorClient.EVENT_BUS.post((ICancellable)InteractBlockEvent.get(player.method_6047().method_7960() ? class_1268.field_5810 : hand, hitResult))).isCancelled()) {
         cir.setReturnValue(class_1269.field_5814);
      }

   }

   @Inject(
      method = {"attackEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAttackEntity(class_1657 player, class_1297 target, CallbackInfo info) {
      if (((AttackEntityEvent)MeteorClient.EVENT_BUS.post((ICancellable)AttackEntityEvent.get(target))).isCancelled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"interactEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onInteractEntity(class_1657 player, class_1297 entity, class_1268 hand, CallbackInfoReturnable<class_1269> info) {
      if (((InteractEntityEvent)MeteorClient.EVENT_BUS.post((ICancellable)InteractEntityEvent.get(entity, hand))).isCancelled()) {
         info.setReturnValue(class_1269.field_5814);
      }

   }

   @Inject(
      method = {"dropCreativeStack"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDropCreativeStack(class_1799 stack, CallbackInfo info) {
      if (((DropItemsEvent)MeteorClient.EVENT_BUS.post((ICancellable)DropItemsEvent.get(stack))).isCancelled()) {
         info.cancel();
      }

   }

   @Redirect(
      method = {"updateBlockBreakingProgress"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I",
   opcode = 181,
   ordinal = 1
)
   )
   private void creativeBreakDelayChange(class_636 interactionManager, int value) {
      BlockBreakingCooldownEvent event = (BlockBreakingCooldownEvent)MeteorClient.EVENT_BUS.post((Object)BlockBreakingCooldownEvent.get(value));
      this.field_3716 = event.cooldown;
   }

   @Redirect(
      method = {"updateBlockBreakingProgress"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I",
   opcode = 181,
   ordinal = 2
)
   )
   private void survivalBreakDelayChange(class_636 interactionManager, int value) {
      BlockBreakingCooldownEvent event = (BlockBreakingCooldownEvent)MeteorClient.EVENT_BUS.post((Object)BlockBreakingCooldownEvent.get(value));
      this.field_3716 = event.cooldown;
   }

   @Redirect(
      method = {"attackBlock"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I",
   opcode = 181
)
   )
   private void creativeBreakDelayChange2(class_636 interactionManager, int value) {
      BlockBreakingCooldownEvent event = (BlockBreakingCooldownEvent)MeteorClient.EVENT_BUS.post((Object)BlockBreakingCooldownEvent.get(value));
      this.field_3716 = event.cooldown;
   }

   @Redirect(
      method = {"method_41930"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;calcBlockBreakingDelta(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F"
)
   )
   private float deltaChange(class_2680 blockState, class_1657 player, class_1922 world, class_2338 pos) {
      float delta = blockState.method_26165(player, world, pos);
      if (((BreakDelay)Modules.get().get(BreakDelay.class)).preventInstaBreak() && delta >= 1.0F) {
         BlockBreakingCooldownEvent event = (BlockBreakingCooldownEvent)MeteorClient.EVENT_BUS.post((Object)BlockBreakingCooldownEvent.get(this.field_3716));
         this.field_3716 = event.cooldown;
         return 0.0F;
      } else {
         return delta;
      }
   }

   @Inject(
      method = {"breakBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onBreakBlock(class_2338 blockPos, CallbackInfoReturnable<Boolean> info) {
      if (((BreakBlockEvent)MeteorClient.EVENT_BUS.post((ICancellable)BreakBlockEvent.get(blockPos))).isCancelled()) {
         info.setReturnValue(false);
      }

   }

   @Inject(
      method = {"interactItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onInteractItem(class_1657 player, class_1268 hand, CallbackInfoReturnable<class_1269> info) {
      InteractItemEvent event = (InteractItemEvent)MeteorClient.EVENT_BUS.post((Object)InteractItemEvent.get(hand));
      if (event.toReturn != null) {
         info.setReturnValue(event.toReturn);
      }

   }

   @Inject(
      method = {"cancelBlockBreaking"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onCancelBlockBreaking(CallbackInfo info) {
      if (BlockUtils.breaking) {
         info.cancel();
      }

   }

   public void meteor$syncSelected() {
      this.method_2911();
   }
}

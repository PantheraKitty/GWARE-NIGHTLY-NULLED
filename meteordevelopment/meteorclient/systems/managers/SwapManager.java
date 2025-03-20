package meteordevelopment.meteorclient.systems.managers;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import meteordevelopment.meteorclient.systems.config.AntiCheatConfig;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_433;

public class SwapManager {
   private final AntiCheatConfig antiCheatConfig = AntiCheatConfig.get();
   private final Object swapLock = new Object();
   private SwapManager.SwapState multiTickSwapState = new SwapManager.SwapState(this);
   private SwapManager.SwapState instantSwapState = new SwapManager.SwapState(this);

   public SwapManager() {
      MeteorClient.EVENT_BUS.subscribe((Object)this);
   }

   public boolean beginSwap(class_1792 item, boolean instant) {
      FindItemResult result = InvUtils.findInHotbar(item);
      if (this.getItemSwapMode() == SwapManager.SwapMode.None && !result.isMainHand()) {
         return false;
      } else if (this.getItemSwapMode() == SwapManager.SwapMode.SilentHotbar && !result.found()) {
         return false;
      } else {
         if (!result.found()) {
            result = InvUtils.find(item);
         }

         return !result.found() ? false : this.beginSwap(result, instant);
      }
   }

   public boolean beginSwap(FindItemResult result, boolean instant) {
      if (!result.found()) {
         return false;
      } else if (this.getItemSwapMode() == SwapManager.SwapMode.None && !result.isMainHand()) {
         return false;
      } else if (!instant && MeteorClient.mc.field_1724.method_6115() && MeteorClient.mc.field_1724.method_6058() == class_1268.field_5808) {
         return false;
      } else {
         synchronized(this.swapLock) {
            if (this.instantSwapState.isSwapped) {
               return false;
            }

            if (this.multiTickSwapState.isSwapped && !instant) {
               return false;
            }

            this.getSwapState(instant).isSwapped = true;
         }

         SwapManager.SwapState swapState = this.getSwapState(instant);
         switch(this.getItemSwapMode().ordinal()) {
         case 0:
         default:
            break;
         case 1:
            boolean shouldSilentSwap = !result.isHotbar() || this.multiTickSwapState.isSwapped && instant || MeteorClient.mc.field_1724.method_6115() && MeteorClient.mc.field_1724.method_6058() == class_1268.field_5808;
            if (shouldSilentSwap) {
               if ((Boolean)this.antiCheatConfig.swapAntiScreenClose.get() && MeteorClient.mc.field_1755 instanceof class_433) {
                  this.getSwapState(instant).isSwapped = false;
                  return false;
               }

               swapState.silentSwapInventorySlot = result.slot();
               swapState.silentSwapSelectedSlot = MeteorClient.mc.field_1724.method_31548().field_7545;
               swapState.didSilentSwap = true;
               InvUtils.quickSwap().fromId(MeteorClient.mc.field_1724.method_31548().field_7545).to(result.slot());
            } else {
               swapState.hotbarSelectedSlot = MeteorClient.mc.field_1724.method_31548().field_7545;
               swapState.hotbarItemSlot = result.slot();
               swapState.didSilentSwap = false;
               MeteorClient.mc.field_1724.method_31548().field_7545 = result.slot();
               ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).meteor$syncSelected();
            }
            break;
         case 2:
            swapState.hotbarSelectedSlot = MeteorClient.mc.field_1724.method_31548().field_7545;
            swapState.hotbarItemSlot = result.slot();
            swapState.didSilentSwap = false;
            MeteorClient.mc.field_1724.method_31548().field_7545 = result.slot();
            ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).meteor$syncSelected();
            break;
         case 3:
            if ((Boolean)this.antiCheatConfig.swapAntiScreenClose.get() && MeteorClient.mc.field_1755 instanceof class_433) {
               this.getSwapState(instant).isSwapped = false;
               return false;
            }

            swapState.silentSwapInventorySlot = result.slot();
            swapState.silentSwapSelectedSlot = MeteorClient.mc.field_1724.method_31548().field_7545;
            swapState.didSilentSwap = true;
            InvUtils.quickSwap().fromId(MeteorClient.mc.field_1724.method_31548().field_7545).to(result.slot());
         }

         return true;
      }
   }

   public boolean canSwap(class_1792 item) {
      return this.getSlot(item).found();
   }

   public FindItemResult getSlot(class_1792 item) {
      FindItemResult result = InvUtils.findInHotbar(item);
      if (this.getItemSwapMode() == SwapManager.SwapMode.None && !result.isMainHand()) {
         return new FindItemResult(-1, 0);
      } else if (this.getItemSwapMode() == SwapManager.SwapMode.SilentHotbar && !result.found()) {
         return new FindItemResult(-1, 0);
      } else {
         if (!result.found()) {
            result = InvUtils.find(item);
         }

         return !result.found() ? new FindItemResult(-1, 0) : result;
      }
   }

   public void endSwap(boolean instantSwap) {
      synchronized(this.swapLock) {
         if (instantSwap && !this.getSwapState(instantSwap).isSwapped) {
            return;
         }
      }

      SwapManager.SwapState swapState = this.getSwapState(instantSwap);
      if (swapState.didSilentSwap) {
         InvUtils.quickSwap().fromId(swapState.silentSwapSelectedSlot).to(swapState.silentSwapInventorySlot);
      } else {
         MeteorClient.mc.field_1724.method_31548().field_7545 = swapState.hotbarSelectedSlot;
         ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).meteor$syncSelected();
      }

      swapState.isSwapped = false;
   }

   public SwapManager.SwapMode getItemSwapMode() {
      return (SwapManager.SwapMode)this.antiCheatConfig.swapMode.get();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (MeteorClient.mc.field_1687 != null && MeteorClient.mc.field_1724 != null) {
         if (this.multiTickSwapState.isSwapped) {
            if (this.multiTickSwapState.didSilentSwap) {
               if (this.multiTickSwapState.silentSwapSelectedSlot != MeteorClient.mc.field_1724.method_31548().field_7545) {
                  MeteorClient.mc.field_1724.method_31548().field_7545 = this.multiTickSwapState.silentSwapSelectedSlot;
                  ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).meteor$syncSelected();
               }
            } else if (this.multiTickSwapState.hotbarItemSlot != MeteorClient.mc.field_1724.method_31548().field_7545) {
               MeteorClient.mc.field_1724.method_31548().field_7545 = this.multiTickSwapState.hotbarItemSlot;
               ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).meteor$syncSelected();
            }
         }

      }
   }

   private SwapManager.SwapState getSwapState(boolean instantSwap) {
      return instantSwap ? this.instantSwapState : this.multiTickSwapState;
   }

   private class SwapState {
      public boolean isSwapped = false;
      public boolean didSilentSwap = false;
      public int hotbarSelectedSlot = 0;
      public int hotbarItemSlot = 0;
      public int silentSwapSelectedSlot = 0;
      public int silentSwapInventorySlot = 0;

      private SwapState(final SwapManager param1) {
      }
   }

   public static enum SwapMode {
      None,
      Auto,
      SilentHotbar,
      SilentSwap;

      // $FF: synthetic method
      private static SwapManager.SwapMode[] $values() {
         return new SwapManager.SwapMode[]{None, Auto, SilentHotbar, SilentSwap};
      }
   }
}

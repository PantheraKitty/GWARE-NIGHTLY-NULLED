package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.SlotUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1810;

public class HotbarLock extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> checkDelay;
   private final Setting<Integer> pickaxeSlot;
   private final class_1799[] hotbarSnapshot;
   private int checkTimer;
   private boolean wasInventoryOpen;
   private boolean isEating;

   public HotbarLock() {
      super(Categories.Player, "hotbar-lock", "Locks your hotbar to prevent changes.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.checkDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("check-delay")).description("The delay in ticks to check for hotbar changes.")).defaultValue(5)).min(1).sliderRange(1, 20).build());
      this.pickaxeSlot = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("pickaxe-slot")).description("The slot to lock your pickaxe to (1-9).")).defaultValue(1)).min(1).max(9).sliderRange(1, 9).onChanged(this::onPickaxeSlotChanged)).build());
      this.hotbarSnapshot = new class_1799[9];
      this.checkTimer = 0;
      this.wasInventoryOpen = false;
      this.isEating = false;

      for(int i = 0; i < this.hotbarSnapshot.length; ++i) {
         this.hotbarSnapshot[i] = new class_1799(class_1802.field_8162);
      }

   }

   public void onActivate() {
      this.saveHotbar();
      this.checkTimer = 0;
      this.wasInventoryOpen = false;
      this.isEating = false;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1724 != null) {
         if (this.mc.field_1724.method_6115()) {
            this.isEating = true;
         } else {
            this.isEating = false;
            if (!this.isEating) {
               boolean isInventoryOpen = this.mc.field_1755 != null;
               if (this.wasInventoryOpen && !isInventoryOpen) {
                  this.saveHotbar();
               }

               this.wasInventoryOpen = isInventoryOpen;
               if (!isInventoryOpen) {
                  if (this.checkTimer <= 0) {
                     this.checkTimer = (Integer)this.checkDelay.get();
                     this.checkHotbar();
                  } else {
                     --this.checkTimer;
                  }

               }
            }
         }
      }
   }

   private void saveHotbar() {
      for(int i = 0; i < 9; ++i) {
         this.hotbarSnapshot[i] = this.mc.field_1724.method_31548().method_5438(i).method_7972();
      }

   }

   private void checkHotbar() {
      class_1799 pickaxeStack = this.mc.field_1724.method_31548().method_5438((Integer)this.pickaxeSlot.get() - 1);
      boolean isPickaxeInSlot = pickaxeStack.method_7909() instanceof class_1810;
      int i;
      class_1799 currentStack;
      if (!isPickaxeInSlot) {
         for(i = 9; i < 36; ++i) {
            currentStack = this.mc.field_1724.method_31548().method_5438(i);
            if (currentStack.method_7909() instanceof class_1810) {
               InvUtils.move().from(i).toHotbar((Integer)this.pickaxeSlot.get() - 1);
               break;
            }
         }
      }

      for(i = 0; i < 9; ++i) {
         currentStack = this.mc.field_1724.method_31548().method_5438(i);
         if (!class_1799.method_31577(currentStack, this.hotbarSnapshot[i]) && currentStack.method_7909() != class_1802.field_8288 && (i < 36 || i > 39)) {
            InvUtils.move().from(SlotUtils.indexToId(i)).to(SlotUtils.indexToId(i));
            this.mc.field_1724.method_31548().method_5447(i, this.hotbarSnapshot[i].method_7972());
         }
      }

   }

   private void onPickaxeSlotChanged(int newSlot) {
      if (this.mc.field_1724 != null) {
         for(int i = 0; i < 36; ++i) {
            class_1799 stack = this.mc.field_1724.method_31548().method_5438(i);
            if (stack.method_7909() instanceof class_1810) {
               InvUtils.move().from(i).toHotbar(newSlot - 1);
               break;
            }
         }

      }
   }
}

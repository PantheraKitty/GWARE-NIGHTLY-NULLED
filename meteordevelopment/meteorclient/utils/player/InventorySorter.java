package meteordevelopment.meteorclient.utils.player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.mixininterface.ISlot;
import meteordevelopment.meteorclient.utils.render.PeekScreen;
import net.minecraft.class_1277;
import net.minecraft.class_1661;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_3545;
import net.minecraft.class_465;
import net.minecraft.class_476;
import net.minecraft.class_481;
import net.minecraft.class_495;
import net.minecraft.class_7923;

public class InventorySorter {
   private final class_465<?> screen;
   private final InventorySorter.InvPart originInvPart;
   private boolean invalid;
   private List<InventorySorter.Action> actions;
   private int timer;
   private int currentActionI;

   public InventorySorter(class_465<?> screen, class_1735 originSlot) {
      this.screen = screen;
      this.originInvPart = this.getInvPart(originSlot);
      if (this.originInvPart != InventorySorter.InvPart.Invalid && this.originInvPart != InventorySorter.InvPart.Hotbar && !(screen instanceof PeekScreen)) {
         this.actions = new ArrayList();
         this.generateActions();
      } else {
         this.invalid = true;
      }
   }

   public boolean tick(int delay) {
      if (this.invalid) {
         return true;
      } else if (this.currentActionI >= this.actions.size()) {
         return true;
      } else if (this.timer >= delay) {
         this.timer = 0;
         InventorySorter.Action action = (InventorySorter.Action)this.actions.get(this.currentActionI);
         InvUtils.move().fromId(action.from).toId(action.to);
         ++this.currentActionI;
         return false;
      } else {
         ++this.timer;
         return false;
      }
   }

   private void generateActions() {
      List<InventorySorter.MySlot> slots = new ArrayList();
      Iterator var2 = this.screen.method_17577().field_7761.iterator();

      while(var2.hasNext()) {
         class_1735 slot = (class_1735)var2.next();
         if (this.getInvPart(slot) == this.originInvPart) {
            slots.add(new InventorySorter.MySlot(((ISlot)slot).getId(), slot.method_7677()));
         }
      }

      slots.sort(Comparator.comparingInt((value) -> {
         return value.id;
      }));
      this.generateStackingActions(slots);
      this.generateSortingActions(slots);
   }

   private void generateStackingActions(List<InventorySorter.MySlot> slots) {
      InventorySorter.SlotMap slotMap = new InventorySorter.SlotMap();
      Iterator var3 = slots.iterator();

      while(var3.hasNext()) {
         InventorySorter.MySlot slot = (InventorySorter.MySlot)var3.next();
         if (!slot.itemStack.method_7960() && slot.itemStack.method_7946() && slot.itemStack.method_7947() < slot.itemStack.method_7914()) {
            slotMap.get(slot.itemStack).add(slot);
         }
      }

      var3 = slotMap.map.iterator();

      while(var3.hasNext()) {
         class_3545<class_1799, List<InventorySorter.MySlot>> entry = (class_3545)var3.next();
         List<InventorySorter.MySlot> slotsToStack = (List)entry.method_15441();
         InventorySorter.MySlot slotToStackTo = null;

         for(int i = 0; i < slotsToStack.size(); ++i) {
            InventorySorter.MySlot slot = (InventorySorter.MySlot)slotsToStack.get(i);
            if (slotToStackTo == null) {
               slotToStackTo = slot;
            } else {
               this.actions.add(new InventorySorter.Action(slot.id, slotToStackTo.id));
               if (slotToStackTo.itemStack.method_7947() + slot.itemStack.method_7947() <= slotToStackTo.itemStack.method_7914()) {
                  slotToStackTo.itemStack = new class_1799(slotToStackTo.itemStack.method_7909(), slotToStackTo.itemStack.method_7947() + slot.itemStack.method_7947());
                  slot.itemStack = class_1799.field_8037;
                  if (slotToStackTo.itemStack.method_7947() >= slotToStackTo.itemStack.method_7914()) {
                     slotToStackTo = null;
                  }
               } else {
                  int needed = slotToStackTo.itemStack.method_7914() - slotToStackTo.itemStack.method_7947();
                  slotToStackTo.itemStack = new class_1799(slotToStackTo.itemStack.method_7909(), slotToStackTo.itemStack.method_7914());
                  slot.itemStack = new class_1799(slot.itemStack.method_7909(), slot.itemStack.method_7947() - needed);
                  slotToStackTo = null;
                  --i;
               }
            }
         }
      }

   }

   private void generateSortingActions(List<InventorySorter.MySlot> slots) {
      for(int i = 0; i < slots.size(); ++i) {
         InventorySorter.MySlot bestSlot = null;

         for(int j = i; j < slots.size(); ++j) {
            InventorySorter.MySlot slot = (InventorySorter.MySlot)slots.get(j);
            if (bestSlot == null) {
               bestSlot = slot;
            } else if (this.isSlotBetter(bestSlot, slot)) {
               bestSlot = slot;
            }
         }

         if (!bestSlot.itemStack.method_7960()) {
            InventorySorter.MySlot toSlot = (InventorySorter.MySlot)slots.get(i);
            int from = bestSlot.id;
            int to = toSlot.id;
            if (from != to) {
               class_1799 temp = bestSlot.itemStack;
               bestSlot.itemStack = toSlot.itemStack;
               toSlot.itemStack = temp;
               this.actions.add(new InventorySorter.Action(from, to));
            }
         }
      }

   }

   private boolean isSlotBetter(InventorySorter.MySlot best, InventorySorter.MySlot slot) {
      class_1799 bestI = best.itemStack;
      class_1799 slotI = slot.itemStack;
      if (bestI.method_7960() && !slotI.method_7960()) {
         return true;
      } else if (!bestI.method_7960() && slotI.method_7960()) {
         return false;
      } else {
         int c = class_7923.field_41178.method_10221(bestI.method_7909()).method_12833(class_7923.field_41178.method_10221(slotI.method_7909()));
         if (c == 0) {
            return slotI.method_7947() > bestI.method_7947();
         } else {
            return c > 0;
         }
      }
   }

   private InventorySorter.InvPart getInvPart(class_1735 slot) {
      int i = ((ISlot)slot).getIndex();
      if (!(slot.field_7871 instanceof class_1661) || this.screen instanceof class_481 && ((ISlot)slot).getId() <= 8) {
         if ((this.screen instanceof class_476 || this.screen instanceof class_495) && slot.field_7871 instanceof class_1277) {
            return InventorySorter.InvPart.Main;
         }
      } else {
         if (SlotUtils.isHotbar(i)) {
            return InventorySorter.InvPart.Hotbar;
         }

         if (SlotUtils.isMain(i)) {
            return InventorySorter.InvPart.Player;
         }
      }

      return InventorySorter.InvPart.Invalid;
   }

   private static enum InvPart {
      Hotbar,
      Player,
      Main,
      Invalid;

      // $FF: synthetic method
      private static InventorySorter.InvPart[] $values() {
         return new InventorySorter.InvPart[]{Hotbar, Player, Main, Invalid};
      }
   }

   private static record Action(int from, int to) {
      private Action(int from, int to) {
         this.from = from;
         this.to = to;
      }

      public int from() {
         return this.from;
      }

      public int to() {
         return this.to;
      }
   }

   private static class MySlot {
      public final int id;
      public class_1799 itemStack;

      public MySlot(int id, class_1799 itemStack) {
         this.id = id;
         this.itemStack = itemStack;
      }
   }

   private static class SlotMap {
      private final List<class_3545<class_1799, List<InventorySorter.MySlot>>> map = new ArrayList();

      public List<InventorySorter.MySlot> get(class_1799 itemStack) {
         Iterator var2 = this.map.iterator();

         class_3545 entry;
         do {
            if (!var2.hasNext()) {
               List<InventorySorter.MySlot> list = new ArrayList();
               this.map.add(new class_3545(itemStack, list));
               return list;
            }

            entry = (class_3545)var2.next();
         } while(!class_1799.method_7984(itemStack, (class_1799)entry.method_15442()));

         return (List)entry.method_15441();
      }
   }
}

package meteordevelopment.meteorclient.utils.player;

import java.util.function.Predicate;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2680;

public class InvUtils {
   private static final InvUtils.Action ACTION = new InvUtils.Action();
   public static int previousSlot = -1;

   private InvUtils() {
   }

   public static boolean testInMainHand(Predicate<class_1799> predicate) {
      return predicate.test(MeteorClient.mc.field_1724.method_6047());
   }

   public static boolean testInMainHand(class_1792... items) {
      return testInMainHand((itemStack) -> {
         class_1792[] var2 = items;
         int var3 = items.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_1792 item = var2[var4];
            if (itemStack.method_31574(item)) {
               return true;
            }
         }

         return false;
      });
   }

   public static boolean testInOffHand(Predicate<class_1799> predicate) {
      return predicate.test(MeteorClient.mc.field_1724.method_6079());
   }

   public static boolean testInOffHand(class_1792... items) {
      return testInOffHand((itemStack) -> {
         class_1792[] var2 = items;
         int var3 = items.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_1792 item = var2[var4];
            if (itemStack.method_31574(item)) {
               return true;
            }
         }

         return false;
      });
   }

   public static boolean testInHands(Predicate<class_1799> predicate) {
      return testInMainHand(predicate) || testInOffHand(predicate);
   }

   public static boolean testInHands(class_1792... items) {
      return testInMainHand(items) || testInOffHand(items);
   }

   public static boolean testInHotbar(Predicate<class_1799> predicate) {
      if (testInHands(predicate)) {
         return true;
      } else {
         for(int i = 0; i < 8; ++i) {
            class_1799 stack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
            if (predicate.test(stack)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean testInHotbar(class_1792... items) {
      return testInHotbar((itemStack) -> {
         class_1792[] var2 = items;
         int var3 = items.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_1792 item = var2[var4];
            if (itemStack.method_31574(item)) {
               return true;
            }
         }

         return false;
      });
   }

   public static FindItemResult findEmpty() {
      return find(class_1799::method_7960);
   }

   public static FindItemResult findInHotbar(class_1792... items) {
      return findInHotbar((itemStack) -> {
         class_1792[] var2 = items;
         int var3 = items.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_1792 item = var2[var4];
            if (itemStack.method_7909() == item) {
               return true;
            }
         }

         return false;
      });
   }

   public static FindItemResult findInHotbar(Predicate<class_1799> isGood) {
      if (testInOffHand(isGood)) {
         return new FindItemResult(45, MeteorClient.mc.field_1724.method_6079().method_7947());
      } else {
         return testInMainHand(isGood) ? new FindItemResult(MeteorClient.mc.field_1724.method_31548().field_7545, MeteorClient.mc.field_1724.method_6047().method_7947()) : find(isGood, 0, 8);
      }
   }

   public static FindItemResult find(class_1792... items) {
      return find((itemStack) -> {
         class_1792[] var2 = items;
         int var3 = items.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_1792 item = var2[var4];
            if (itemStack.method_7909() == item) {
               return true;
            }
         }

         return false;
      });
   }

   public static FindItemResult find(Predicate<class_1799> isGood) {
      return MeteorClient.mc.field_1724 == null ? new FindItemResult(0, 0) : find(isGood, 0, MeteorClient.mc.field_1724.method_31548().method_5439());
   }

   public static FindItemResult find(Predicate<class_1799> isGood, int start, int end) {
      if (MeteorClient.mc.field_1724 == null) {
         return new FindItemResult(0, 0);
      } else {
         int slot = -1;
         int count = 0;

         for(int i = start; i <= end; ++i) {
            class_1799 stack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
            if (isGood.test(stack)) {
               if (slot == -1) {
                  slot = i;
               }

               count += stack.method_7947();
            }
         }

         return new FindItemResult(slot, count);
      }
   }

   public static FindItemResult findFastestToolHotbar(class_2680 state) {
      float bestScore = 1.0F;
      int slot = -1;

      for(int i = 0; i < 9; ++i) {
         class_1799 stack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
         if (stack.method_7951(state)) {
            float score = stack.method_7924(state);
            if (score > bestScore) {
               bestScore = score;
               slot = i;
            }
         }
      }

      return new FindItemResult(slot, 1);
   }

   public static FindItemResult findFastestTool(class_2680 state) {
      float bestScore = 1.0F;
      int slot = -1;

      for(int i = 0; i < MeteorClient.mc.field_1724.method_31548().method_5439(); ++i) {
         class_1799 stack = MeteorClient.mc.field_1724.method_31548().method_5438(i);
         if (stack.method_7951(state)) {
            float score = stack.method_7924(state);
            if (score > bestScore) {
               bestScore = score;
               slot = i;
            }
         }
      }

      return new FindItemResult(slot, 1);
   }

   public static boolean swap(int slot, boolean swapBack) {
      if (slot == 45) {
         return true;
      } else if (slot >= 0 && slot <= 8) {
         if (swapBack && previousSlot == -1) {
            previousSlot = MeteorClient.mc.field_1724.method_31548().field_7545;
         } else if (!swapBack) {
            previousSlot = -1;
         }

         MeteorClient.mc.field_1724.method_31548().field_7545 = slot;
         ((IClientPlayerInteractionManager)MeteorClient.mc.field_1761).meteor$syncSelected();
         return true;
      } else {
         return false;
      }
   }

   public static boolean swapBack() {
      if (previousSlot == -1) {
         return false;
      } else {
         boolean return_ = swap(previousSlot, false);
         previousSlot = -1;
         return return_;
      }
   }

   public static InvUtils.Action move() {
      ACTION.type = class_1713.field_7790;
      ACTION.two = true;
      return ACTION;
   }

   public static InvUtils.Action click() {
      ACTION.type = class_1713.field_7790;
      return ACTION;
   }

   public static InvUtils.Action quickSwap() {
      ACTION.type = class_1713.field_7791;
      return ACTION;
   }

   public static InvUtils.Action shiftClick() {
      ACTION.type = class_1713.field_7794;
      return ACTION;
   }

   public static InvUtils.Action drop() {
      ACTION.type = class_1713.field_7795;
      ACTION.data = 1;
      return ACTION;
   }

   public static void dropHand() {
      if (!MeteorClient.mc.field_1724.field_7512.method_34255().method_7960()) {
         MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7512.field_7763, -999, 0, class_1713.field_7790, MeteorClient.mc.field_1724);
      }

   }

   public static class Action {
      private class_1713 type = null;
      private boolean two = false;
      private int from = -1;
      private int to = -1;
      private int data = 0;
      private boolean isRecursive = false;

      private Action() {
      }

      public InvUtils.Action fromId(int id) {
         this.from = id;
         return this;
      }

      public InvUtils.Action from(int index) {
         return this.fromId(SlotUtils.indexToId(index));
      }

      public InvUtils.Action fromHotbar(int i) {
         return this.from(0 + i);
      }

      public InvUtils.Action fromOffhand() {
         return this.from(45);
      }

      public InvUtils.Action fromMain(int i) {
         return this.from(9 + i);
      }

      public InvUtils.Action fromArmor(int i) {
         return this.from(36 + (3 - i));
      }

      public void toId(int id) {
         this.to = id;
         this.run();
      }

      public void to(int index) {
         this.toId(SlotUtils.indexToId(index));
      }

      public void toHotbar(int i) {
         this.to(0 + i);
      }

      public void toOffhand() {
         this.to(45);
      }

      public void toMain(int i) {
         this.to(9 + i);
      }

      public void toArmor(int i) {
         this.to(36 + (3 - i));
      }

      public void slotId(int id) {
         this.from = this.to = id;
         this.run();
      }

      public void slot(int index) {
         this.slotId(SlotUtils.indexToId(index));
      }

      public void slotHotbar(int i) {
         this.slot(0 + i);
      }

      public void slotOffhand() {
         this.slot(45);
      }

      public void slotMain(int i) {
         this.slot(9 + i);
      }

      public void slotArmor(int i) {
         this.slot(36 + (3 - i));
      }

      private void run() {
         boolean hadEmptyCursor = MeteorClient.mc.field_1724.field_7512.method_34255().method_7960();
         if (this.type == class_1713.field_7791) {
            MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7498.field_7763, this.to, this.from, this.type, MeteorClient.mc.field_1724);
         } else {
            if (this.type != null && this.from != -1 && this.to != -1) {
               this.click(this.from);
               if (this.two) {
                  this.click(this.to);
               }
            }

            class_1713 preType = this.type;
            boolean preTwo = this.two;
            int preFrom = this.from;
            int preTo = this.to;
            this.type = null;
            this.two = false;
            this.from = -1;
            this.to = -1;
            this.data = 0;
            if (!this.isRecursive && hadEmptyCursor && preType == class_1713.field_7790 && preTwo && preFrom != -1 && preTo != -1 && !MeteorClient.mc.field_1724.field_7512.method_34255().method_7960()) {
               this.isRecursive = true;
               InvUtils.click().slotId(preFrom);
               this.isRecursive = false;
            }

         }
      }

      private void click(int id) {
         MeteorClient.mc.field_1761.method_2906(MeteorClient.mc.field_1724.field_7512.field_7763, id, this.data, this.type, MeteorClient.mc.field_1724);
      }
   }
}

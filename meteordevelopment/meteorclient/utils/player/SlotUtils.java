package meteordevelopment.meteorclient.utils.player;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.mixin.CreativeInventoryScreenAccessor;
import meteordevelopment.meteorclient.mixin.HorseScreenHandlerAccessor;
import net.minecraft.class_1492;
import net.minecraft.class_1496;
import net.minecraft.class_1498;
import net.minecraft.class_1501;
import net.minecraft.class_1506;
import net.minecraft.class_1507;
import net.minecraft.class_1703;
import net.minecraft.class_1704;
import net.minecraft.class_1706;
import net.minecraft.class_1707;
import net.minecraft.class_1708;
import net.minecraft.class_1714;
import net.minecraft.class_1716;
import net.minecraft.class_1718;
import net.minecraft.class_1722;
import net.minecraft.class_1723;
import net.minecraft.class_1724;
import net.minecraft.class_1726;
import net.minecraft.class_1728;
import net.minecraft.class_1733;
import net.minecraft.class_3705;
import net.minecraft.class_3706;
import net.minecraft.class_3803;
import net.minecraft.class_3858;
import net.minecraft.class_3910;
import net.minecraft.class_3916;
import net.minecraft.class_3971;
import net.minecraft.class_481;
import net.minecraft.class_7706;
import net.minecraft.class_7923;
import net.minecraft.class_481.class_483;

public class SlotUtils {
   public static final int HOTBAR_START = 0;
   public static final int HOTBAR_END = 8;
   public static final int OFFHAND = 45;
   public static final int MAIN_START = 9;
   public static final int MAIN_END = 35;
   public static final int ARMOR_START = 36;
   public static final int ARMOR_END = 39;

   private SlotUtils() {
   }

   public static int indexToId(int i) {
      if (MeteorClient.mc.field_1724 == null) {
         return -1;
      } else {
         class_1703 handler = MeteorClient.mc.field_1724.field_7512;
         if (handler instanceof class_1723) {
            return survivalInventory(i);
         } else if (handler instanceof class_483) {
            return creativeInventory(i);
         } else if (handler instanceof class_1707) {
            class_1707 genericContainerScreenHandler = (class_1707)handler;
            return genericContainer(i, genericContainerScreenHandler.method_17388());
         } else if (handler instanceof class_1714) {
            return craftingTable(i);
         } else if (handler instanceof class_3858) {
            return furnace(i);
         } else if (handler instanceof class_3705) {
            return furnace(i);
         } else if (handler instanceof class_3706) {
            return furnace(i);
         } else if (handler instanceof class_1716) {
            return generic3x3(i);
         } else if (handler instanceof class_1718) {
            return enchantmentTable(i);
         } else if (handler instanceof class_1708) {
            return brewingStand(i);
         } else if (handler instanceof class_1728) {
            return villager(i);
         } else if (handler instanceof class_1704) {
            return beacon(i);
         } else if (handler instanceof class_1706) {
            return anvil(i);
         } else if (handler instanceof class_1722) {
            return hopper(i);
         } else if (handler instanceof class_1733) {
            return genericContainer(i, 3);
         } else if (handler instanceof class_1724) {
            return horse(handler, i);
         } else if (handler instanceof class_3910) {
            return cartographyTable(i);
         } else if (handler instanceof class_3803) {
            return grindstone(i);
         } else if (handler instanceof class_3916) {
            return lectern();
         } else if (handler instanceof class_1726) {
            return loom(i);
         } else {
            return handler instanceof class_3971 ? stonecutter(i) : -1;
         }
      }
   }

   private static int survivalInventory(int i) {
      if (isHotbar(i)) {
         return 36 + i;
      } else {
         return isArmor(i) ? 5 + (i - 36) : i;
      }
   }

   private static int creativeInventory(int i) {
      return MeteorClient.mc.field_1755 instanceof class_481 && CreativeInventoryScreenAccessor.getSelectedTab() == class_7923.field_44687.method_29107(class_7706.field_40206) ? survivalInventory(i) : -1;
   }

   private static int genericContainer(int i, int rows) {
      if (isHotbar(i)) {
         return (rows + 3) * 9 + i;
      } else {
         return isMain(i) ? rows * 9 + (i - 9) : -1;
      }
   }

   private static int craftingTable(int i) {
      if (isHotbar(i)) {
         return 37 + i;
      } else {
         return isMain(i) ? i + 1 : -1;
      }
   }

   private static int furnace(int i) {
      if (isHotbar(i)) {
         return 30 + i;
      } else {
         return isMain(i) ? 3 + (i - 9) : -1;
      }
   }

   private static int generic3x3(int i) {
      if (isHotbar(i)) {
         return 36 + i;
      } else {
         return isMain(i) ? i : -1;
      }
   }

   private static int enchantmentTable(int i) {
      if (isHotbar(i)) {
         return 29 + i;
      } else {
         return isMain(i) ? 2 + (i - 9) : -1;
      }
   }

   private static int brewingStand(int i) {
      if (isHotbar(i)) {
         return 32 + i;
      } else {
         return isMain(i) ? 5 + (i - 9) : -1;
      }
   }

   private static int villager(int i) {
      if (isHotbar(i)) {
         return 30 + i;
      } else {
         return isMain(i) ? 3 + (i - 9) : -1;
      }
   }

   private static int beacon(int i) {
      if (isHotbar(i)) {
         return 28 + i;
      } else {
         return isMain(i) ? 1 + (i - 9) : -1;
      }
   }

   private static int anvil(int i) {
      if (isHotbar(i)) {
         return 30 + i;
      } else {
         return isMain(i) ? 3 + (i - 9) : -1;
      }
   }

   private static int hopper(int i) {
      if (isHotbar(i)) {
         return 32 + i;
      } else {
         return isMain(i) ? 5 + (i - 9) : -1;
      }
   }

   private static int horse(class_1703 handler, int i) {
      class_1496 entity = ((HorseScreenHandlerAccessor)handler).getEntity();
      if (entity instanceof class_1501) {
         class_1501 llamaEntity = (class_1501)entity;
         int strength = llamaEntity.method_6803();
         if (isHotbar(i)) {
            return 2 + 3 * strength + 28 + i;
         }

         if (isMain(i)) {
            return 2 + 3 * strength + 1 + (i - 9);
         }
      } else if (!(entity instanceof class_1498) && !(entity instanceof class_1506) && !(entity instanceof class_1507)) {
         if (entity instanceof class_1492) {
            class_1492 abstractDonkeyEntity = (class_1492)entity;
            boolean chest = abstractDonkeyEntity.method_6703();
            if (isHotbar(i)) {
               return (chest ? 44 : 29) + i;
            }

            if (isMain(i)) {
               return (chest ? 17 : 2) + (i - 9);
            }
         }
      } else {
         if (isHotbar(i)) {
            return 29 + i;
         }

         if (isMain(i)) {
            return 2 + (i - 9);
         }
      }

      return -1;
   }

   private static int cartographyTable(int i) {
      if (isHotbar(i)) {
         return 30 + i;
      } else {
         return isMain(i) ? 3 + (i - 9) : -1;
      }
   }

   private static int grindstone(int i) {
      if (isHotbar(i)) {
         return 30 + i;
      } else {
         return isMain(i) ? 3 + (i - 9) : -1;
      }
   }

   private static int lectern() {
      return -1;
   }

   private static int loom(int i) {
      if (isHotbar(i)) {
         return 31 + i;
      } else {
         return isMain(i) ? 4 + (i - 9) : -1;
      }
   }

   private static int stonecutter(int i) {
      if (isHotbar(i)) {
         return 29 + i;
      } else {
         return isMain(i) ? 2 + (i - 9) : -1;
      }
   }

   public static boolean isHotbar(int i) {
      return i >= 0 && i <= 8;
   }

   public static boolean isMain(int i) {
      return i >= 9 && i <= 35;
   }

   public static boolean isArmor(int i) {
      return i >= 36 && i <= 39;
   }
}

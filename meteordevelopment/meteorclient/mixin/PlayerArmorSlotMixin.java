package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import net.minecraft.class_1263;
import net.minecraft.class_1657;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   targets = {"net/minecraft/screen/PlayerScreenHandler$1"}
)
public abstract class PlayerArmorSlotMixin extends class_1735 {
   public PlayerArmorSlotMixin(class_1263 inventory, int index, int x, int y) {
      super(inventory, index, x, y);
   }

   public int method_7675() {
      return ((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).armorStorage() ? 64 : super.method_7675();
   }

   public boolean method_7680(class_1799 stack) {
      return ((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).armorStorage() ? true : super.method_7680(stack);
   }

   public boolean method_7674(class_1657 playerEntity) {
      return ((InventoryTweaks)Modules.get().get(InventoryTweaks.class)).armorStorage() ? true : super.method_7674(playerEntity);
   }
}

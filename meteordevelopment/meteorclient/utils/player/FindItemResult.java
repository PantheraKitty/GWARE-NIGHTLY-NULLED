package meteordevelopment.meteorclient.utils.player;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.class_1268;

public record FindItemResult(int slot, int count) {
   public FindItemResult(int slot, int count) {
      this.slot = slot;
      this.count = count;
   }

   public boolean found() {
      return this.slot != -1;
   }

   public class_1268 getHand() {
      if (this.slot == 45) {
         return class_1268.field_5810;
      } else {
         return this.slot == MeteorClient.mc.field_1724.method_31548().field_7545 ? class_1268.field_5808 : null;
      }
   }

   public boolean isMainHand() {
      return this.getHand() == class_1268.field_5808;
   }

   public boolean isOffhand() {
      return this.getHand() == class_1268.field_5810;
   }

   public boolean isHotbar() {
      return this.slot >= 0 && this.slot <= 8;
   }

   public boolean isMain() {
      return this.slot >= 9 && this.slot <= 35;
   }

   public boolean isArmor() {
      return this.slot >= 36 && this.slot <= 39;
   }

   public int slot() {
      return this.slot;
   }

   public int count() {
      return this.count;
   }
}

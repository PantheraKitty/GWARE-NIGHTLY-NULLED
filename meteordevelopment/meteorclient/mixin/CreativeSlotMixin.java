package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ISlot;
import net.minecraft.class_1735;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(
   targets = {"net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen$CreativeSlot"}
)
public abstract class CreativeSlotMixin implements ISlot {
   @Shadow
   @Final
   class_1735 field_2898;

   public int getId() {
      return this.field_2898.field_7874;
   }

   public int getIndex() {
      return this.field_2898.method_34266();
   }
}

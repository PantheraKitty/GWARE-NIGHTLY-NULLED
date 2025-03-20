package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IItemEntity;
import net.minecraft.class_1542;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_1542.class})
public abstract class ItemEntityMixin implements IItemEntity {
   @Unique
   private class_243 rotation = new class_243(0.0D, 0.0D, 0.0D);

   public class_243 getRotation() {
      return this.rotation;
   }

   public void setRotation(class_243 rotation) {
      this.rotation = rotation;
   }
}

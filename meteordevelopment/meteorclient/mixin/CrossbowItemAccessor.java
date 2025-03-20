package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1764;
import net.minecraft.class_9278;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_1764.class})
public interface CrossbowItemAccessor {
   @Invoker("getSpeed")
   static float getSpeed(class_9278 itemStack) {
      return 0.0F;
   }
}

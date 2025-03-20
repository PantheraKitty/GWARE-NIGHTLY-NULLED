package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1761;
import net.minecraft.class_481;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_481.class})
public interface CreativeInventoryScreenAccessor {
   @Accessor("selectedTab")
   static class_1761 getSelectedTab() {
      return null;
   }
}

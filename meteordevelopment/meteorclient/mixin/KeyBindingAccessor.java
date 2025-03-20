package meteordevelopment.meteorclient.mixin;

import java.util.Map;
import net.minecraft.class_304;
import net.minecraft.class_3675.class_306;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_304.class})
public interface KeyBindingAccessor {
   @Accessor("CATEGORY_ORDER_MAP")
   static Map<String, Integer> getCategoryOrderMap() {
      return null;
   }

   @Accessor("boundKey")
   class_306 getKey();

   @Accessor("timesPressed")
   int meteor$getTimesPressed();

   @Accessor("timesPressed")
   void meteor$setTimesPressed(int var1);
}

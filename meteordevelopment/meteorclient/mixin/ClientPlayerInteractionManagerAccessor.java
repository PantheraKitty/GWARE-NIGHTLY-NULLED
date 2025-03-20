package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2338;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_636.class})
public interface ClientPlayerInteractionManagerAccessor {
   @Accessor("currentBreakingProgress")
   float getBreakingProgress();

   @Accessor("currentBreakingProgress")
   void setCurrentBreakingProgress(float var1);

   @Accessor("currentBreakingPos")
   class_2338 getCurrentBreakingBlockPos();
}

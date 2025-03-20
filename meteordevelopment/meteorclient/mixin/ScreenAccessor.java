package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_364;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_437.class})
public interface ScreenAccessor {
   @Invoker("addDrawableChild")
   <T extends class_364> T invokeAddDrawableChild(T var1);
}

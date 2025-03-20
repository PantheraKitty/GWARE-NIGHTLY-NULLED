package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_22;
import net.minecraft.class_330;
import net.minecraft.class_9209;
import net.minecraft.class_330.class_331;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_330.class})
public interface MapRendererAccessor {
   @Invoker("getMapTexture")
   class_331 invokeGetMapTexture(class_9209 var1, class_22 var2);
}

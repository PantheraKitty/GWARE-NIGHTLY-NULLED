package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_5577;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_1937.class})
public interface WorldAccessor {
   @Invoker("getEntityLookup")
   class_5577<class_1297> getEntityLookup();
}

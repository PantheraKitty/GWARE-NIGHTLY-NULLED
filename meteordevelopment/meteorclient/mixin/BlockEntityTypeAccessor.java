package meteordevelopment.meteorclient.mixin;

import java.util.Set;
import net.minecraft.class_2248;
import net.minecraft.class_2591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2591.class})
public interface BlockEntityTypeAccessor {
   @Accessor
   Set<class_2248> getBlocks();
}

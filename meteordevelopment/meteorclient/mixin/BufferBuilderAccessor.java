package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_287;
import net.minecraft.class_293;
import net.minecraft.class_9799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_287.class})
public interface BufferBuilderAccessor {
   @Accessor("allocator")
   class_9799 meteor$getAllocator();

   @Accessor("format")
   class_293 getVertexFormat();
}

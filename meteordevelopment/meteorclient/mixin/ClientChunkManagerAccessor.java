package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_631;
import net.minecraft.class_631.class_3681;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_631.class})
public interface ClientChunkManagerAccessor {
   @Accessor("chunks")
   class_3681 getChunks();
}

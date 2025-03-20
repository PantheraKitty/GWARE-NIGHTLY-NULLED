package meteordevelopment.meteorclient.mixin;

import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.class_2818;
import net.minecraft.class_631.class_3681;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_3681.class})
public interface ClientChunkMapAccessor {
   @Accessor("chunks")
   AtomicReferenceArray<class_2818> getChunks();
}

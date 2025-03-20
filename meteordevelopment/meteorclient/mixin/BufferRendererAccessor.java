package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_286;
import net.minecraft.class_291;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_286.class})
public interface BufferRendererAccessor {
   @Accessor("currentVertexBuffer")
   static void setCurrentVertexBuffer(class_291 vertexBuffer) {
   }
}

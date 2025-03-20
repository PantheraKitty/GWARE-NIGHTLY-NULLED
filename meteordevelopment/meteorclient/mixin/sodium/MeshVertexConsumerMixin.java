package meteordevelopment.meteorclient.mixin.sodium;

import meteordevelopment.meteorclient.utils.render.MeshVertexConsumerProvider;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.class_293;
import net.minecraft.class_296;
import net.minecraft.class_4588;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
   value = {MeshVertexConsumerProvider.MeshVertexConsumer.class},
   remap = false
)
public abstract class MeshVertexConsumerMixin implements class_4588, VertexBufferWriter {
   public void push(MemoryStack stack, long ptr, int count, class_293 format) {
      int positionOffset = format.method_60835(class_296.field_52107);
      if (positionOffset != -1) {
         for(int i = 0; i < count; ++i) {
            long positionPtr = ptr + (long)format.method_1362() * (long)i + (long)positionOffset;
            float x = MemoryUtil.memGetFloat(positionPtr);
            float y = MemoryUtil.memGetFloat(positionPtr + 4L);
            float z = MemoryUtil.memGetFloat(positionPtr + 8L);
            this.method_22912(x, y, z);
         }

      }
   }
}

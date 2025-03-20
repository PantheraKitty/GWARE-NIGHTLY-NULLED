package meteordevelopment.meteorclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem.class_5590;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_5590.class})
public interface ShapeIndexBufferAccessor {
   @Accessor("id")
   int getId();
}

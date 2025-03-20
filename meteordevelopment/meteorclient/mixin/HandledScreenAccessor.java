package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1735;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_465.class})
public interface HandledScreenAccessor {
   @Accessor("focusedSlot")
   class_1735 getFocusedSlot();
}

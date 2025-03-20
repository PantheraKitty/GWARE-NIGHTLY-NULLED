package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1792;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1799.class})
public interface ItemStackAccessor {
   @Mutable
   @Accessor("item")
   void setItem(class_1792 var1);
}

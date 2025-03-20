package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2960.class})
public interface IdentifierAccessor {
   @Mutable
   @Accessor
   void setPath(String var1);
}

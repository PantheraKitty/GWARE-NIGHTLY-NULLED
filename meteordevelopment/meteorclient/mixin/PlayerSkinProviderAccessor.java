package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1071;
import net.minecraft.class_1071.class_8687;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1071.class})
public interface PlayerSkinProviderAccessor {
   @Accessor("skinCache")
   class_8687 getSkinCache();
}

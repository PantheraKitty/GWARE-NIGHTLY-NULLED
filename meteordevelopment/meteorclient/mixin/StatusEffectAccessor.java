package meteordevelopment.meteorclient.mixin;

import java.util.Map;
import net.minecraft.class_1291;
import net.minecraft.class_1320;
import net.minecraft.class_6880;
import net.minecraft.class_1291.class_8634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1291.class})
public interface StatusEffectAccessor {
   @Accessor
   Map<class_6880<class_1320>, class_8634> getAttributeModifiers();
}

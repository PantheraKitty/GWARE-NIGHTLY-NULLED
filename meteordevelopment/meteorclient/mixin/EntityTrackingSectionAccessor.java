package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_3509;
import net.minecraft.class_5572;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_5572.class})
public interface EntityTrackingSectionAccessor {
   @Accessor("collection")
   <T> class_3509<T> getCollection();
}

package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1299;
import net.minecraft.class_1785;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1785.class})
public interface EntityBucketItemAccessor {
   @Accessor("entityType")
   class_1299<?> getEntityType();
}

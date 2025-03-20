package meteordevelopment.meteorclient.mixin;

import java.util.UUID;
import net.minecraft.class_1676;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1676.class})
public interface ProjectileEntityAccessor {
   @Accessor
   UUID getOwnerUuid();
}

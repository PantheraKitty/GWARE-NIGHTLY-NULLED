package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_1665;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1665.class})
public interface ProjectileInGroundAccessor {
   @Accessor("inGround")
   boolean getInGround();
}

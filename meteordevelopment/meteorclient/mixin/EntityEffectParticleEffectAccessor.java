package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_9381;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_9381.class})
public interface EntityEffectParticleEffectAccessor {
   @Accessor
   int getColor();
}

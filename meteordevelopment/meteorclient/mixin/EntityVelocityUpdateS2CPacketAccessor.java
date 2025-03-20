package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_2743;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2743.class})
public interface EntityVelocityUpdateS2CPacketAccessor {
   @Mutable
   @Accessor("velocityX")
   void setX(int var1);

   @Mutable
   @Accessor("velocityY")
   void setY(int var1);

   @Mutable
   @Accessor("velocityZ")
   void setZ(int var1);
}

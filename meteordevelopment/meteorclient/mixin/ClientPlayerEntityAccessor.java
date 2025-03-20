package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_746.class})
public interface ClientPlayerEntityAccessor {
   @Accessor("mountJumpStrength")
   void setMountJumpStrength(float var1);

   @Accessor("ticksSinceLastPositionPacketSent")
   void setTicksSinceLastPositionPacketSent(int var1);

   @Invoker("canSprint")
   boolean invokeCanSprint();
}

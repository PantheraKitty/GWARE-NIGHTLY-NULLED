package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import net.minecraft.class_9779.class_9781;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_9781.class})
public abstract class RenderTickCounterDynamicMixin {
   @Shadow
   private float field_51958;

   @Inject(
      method = {"beginRenderTick(J)I"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;prevTimeMillis:J",
   opcode = 181
)}
   )
   private void onBeingRenderTick(long a, CallbackInfoReturnable<Integer> info) {
      this.field_51958 *= (float)((Timer)Modules.get().get(Timer.class)).getMultiplier();
   }
}

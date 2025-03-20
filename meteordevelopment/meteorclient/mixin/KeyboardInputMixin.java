package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.input.KeyboardInputEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraFakeFly;
import meteordevelopment.meteorclient.systems.modules.movement.Sneak;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_743;
import net.minecraft.class_744;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_743.class})
public abstract class KeyboardInputMixin extends class_744 {
   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void isPressed(boolean slowDown, float f, CallbackInfo ci) {
      if (((Sneak)Modules.get().get(Sneak.class)).doVanilla()) {
         this.field_3903 = true;
      }

      if (((ElytraFakeFly)Modules.get().get(ElytraFakeFly.class)).isFlying()) {
         this.field_3903 = false;
      }

   }

   @Inject(
      method = {"tick"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/input/KeyboardInput;sneaking:Z",
   shift = Shift.AFTER
)},
      cancellable = true
   )
   private void onSneak(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
      KeyboardInputEvent event = new KeyboardInputEvent();
      MeteorClient.EVENT_BUS.post((ICancellable)event);
      if (event.isCancelled()) {
         ci.cancel();
      }

   }
}

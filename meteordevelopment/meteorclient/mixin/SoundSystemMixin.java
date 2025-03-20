package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.PlaySoundEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.SoundBlocker;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1113;
import net.minecraft.class_1117;
import net.minecraft.class_1140;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1140.class})
public abstract class SoundSystemMixin {
   @Shadow
   public abstract void method_19753(class_1113 var1);

   @Inject(
      method = {"play(Lnet/minecraft/client/sound/SoundInstance;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPlay(class_1113 soundInstance, CallbackInfo info) {
      PlaySoundEvent event = (PlaySoundEvent)MeteorClient.EVENT_BUS.post((ICancellable)PlaySoundEvent.get(soundInstance));
      if (event.isCancelled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"tick()V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/sound/TickableSoundInstance;tick()V",
   ordinal = 0
)}
   )
   private void onTick(CallbackInfo ci, @Local class_1117 tickableSoundInstance) {
      if (((SoundBlocker)Modules.get().get(SoundBlocker.class)).shouldBlock(tickableSoundInstance)) {
         this.method_19753(tickableSoundInstance);
      }

   }
}

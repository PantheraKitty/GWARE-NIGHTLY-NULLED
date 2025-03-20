package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.ParticleEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_2680;
import net.minecraft.class_702;
import net.minecraft.class_703;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_702.class})
public abstract class ParticleManagerMixin {
   @Shadow
   @Nullable
   protected abstract <T extends class_2394> class_703 method_3055(T var1, double var2, double var4, double var6, double var8, double var10, double var12);

   @Inject(
      method = {"addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAddParticle(class_2394 parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<class_703> info) {
      ParticleEvent event = (ParticleEvent)MeteorClient.EVENT_BUS.post((ICancellable)ParticleEvent.get(parameters));
      if (event.isCancelled()) {
         if (parameters.method_10295() == class_2398.field_17909) {
            info.setReturnValue(this.method_3055(parameters, x, y, z, velocityX, velocityY, velocityZ));
         } else {
            info.cancel();
         }
      }

   }

   @Inject(
      method = {"addBlockBreakParticles"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAddBlockBreakParticles(class_2338 blockPos, class_2680 state, CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBlockBreakParticles()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"addBlockBreakingParticles"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAddBlockBreakingParticles(class_2338 blockPos, class_2350 direction, CallbackInfo info) {
      if (((NoRender)Modules.get().get(NoRender.class)).noBlockBreakParticles()) {
         info.cancel();
      }

   }
}

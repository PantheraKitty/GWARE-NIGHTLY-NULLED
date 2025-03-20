package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.PotionSaver;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1293.class})
public abstract class StatusEffectInstanceMixin {
   @Shadow
   private int field_5895;

   @Inject(
      method = {"updateDuration"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void tick(CallbackInfoReturnable<Integer> info) {
      if (Utils.canUpdate()) {
         if (((PotionSaver)Modules.get().get(PotionSaver.class)).shouldFreeze((class_1291)((class_1293)this).method_5579().comp_349())) {
            info.setReturnValue(this.field_5895);
         }

      }
   }
}

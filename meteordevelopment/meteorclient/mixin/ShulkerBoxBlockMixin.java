package meteordevelopment.meteorclient.mixin;

import java.util.List;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2480;
import net.minecraft.class_2561;
import net.minecraft.class_1792.class_9635;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2480.class})
public abstract class ShulkerBoxBlockMixin {
   @Inject(
      method = {"appendTooltip"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onAppendTooltip(class_1799 stack, class_9635 context, List<class_2561> tooltip, class_1836 options, CallbackInfo ci) {
      if (Modules.get() != null) {
         BetterTooltips tooltips = (BetterTooltips)Modules.get().get(BetterTooltips.class);
         if (tooltips.isActive()) {
            if (tooltips.previewShulkers()) {
               ci.cancel();
            } else if (tooltips.shulkerCompactTooltip()) {
               ci.cancel();
               tooltips.applyCompactShulkerTooltip(stack, tooltip);
            }
         }

      }
   }
}

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.utils.tooltip.MeteorTooltipData;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5632;
import net.minecraft.class_5684;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({class_332.class})
public abstract class DrawContextMixin {
   @Inject(
      method = {"drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V",
   shift = Shift.BEFORE
)},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void onDrawTooltip(class_327 textRenderer, List<class_2561> text, Optional<class_5632> data, int x, int y, CallbackInfo ci, List<class_5684> list) {
      if (data.isPresent()) {
         Object var9 = data.get();
         if (var9 instanceof MeteorTooltipData) {
            MeteorTooltipData meteorTooltipData = (MeteorTooltipData)var9;
            list.add(meteorTooltipData.getComponent());
         }
      }

   }

   @ModifyReceiver(
      method = {"drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"
)}
   )
   private Optional<class_5632> onDrawTooltip_modifyIfPresentReceiver(Optional<class_5632> data, Consumer<class_5632> consumer) {
      return data.isPresent() && data.get() instanceof MeteorTooltipData ? Optional.empty() : data;
   }
}

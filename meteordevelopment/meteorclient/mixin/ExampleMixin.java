package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.utils.misc.TridentUtil;
import net.minecraft.class_310;
import net.minecraft.class_542;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_310.class})
public abstract class ExampleMixin {
   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onGameLoaded(class_542 args, CallbackInfo ci) {
      TridentUtil.LOG.info("meow");
   }
}

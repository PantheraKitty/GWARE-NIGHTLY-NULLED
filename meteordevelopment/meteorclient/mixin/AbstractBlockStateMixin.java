package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_4970.class_4971;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4971.class})
public abstract class AbstractBlockStateMixin {
   @Inject(
      method = {"getModelOffset"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void modifyPos(class_1922 world, class_2338 pos, CallbackInfoReturnable<class_243> cir) {
      if (Modules.get() != null) {
         if (((NoRender)Modules.get().get(NoRender.class)).noTextureRotations()) {
            cir.setReturnValue(class_243.field_1353);
         }

      }
   }
}

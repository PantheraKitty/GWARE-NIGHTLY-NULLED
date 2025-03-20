package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_572;
import net.minecraft.class_970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_970.class})
public abstract class ArmorFeatureRendererMixin<T extends class_1309, M extends class_572<T>, A extends class_572<T>> {
   @Inject(
      method = {"renderArmor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onRenderArmor(class_4587 matrices, class_4597 vertexConsumers, T livingEntity, class_1304 equipmentSlot, int i, A bipedEntityModel, CallbackInfo info) {
      if (livingEntity instanceof class_1657 && ((NoRender)Modules.get().get(NoRender.class)).noArmor()) {
         info.cancel();
      }

   }
}

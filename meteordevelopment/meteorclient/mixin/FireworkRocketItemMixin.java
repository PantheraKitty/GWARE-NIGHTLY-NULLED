package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraFakeFly;
import meteordevelopment.meteorclient.systems.modules.movement.VanillaFakeFly;
import net.minecraft.class_1781;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_1781.class})
public class FireworkRocketItemMixin {
   @ModifyExpressionValue(
      method = {"use"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/player/PlayerEntity;isFallFlying()Z"
)}
   )
   private boolean overrideIsFallFlying(boolean original) {
      ElytraFakeFly fakeFly = (ElytraFakeFly)Modules.get().get(ElytraFakeFly.class);
      VanillaFakeFly vanillaFakeFly = (VanillaFakeFly)Modules.get().get(VanillaFakeFly.class);
      return !fakeFly.isFlying() && !vanillaFakeFly.isFlying() ? original : true;
   }
}

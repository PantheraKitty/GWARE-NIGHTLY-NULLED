package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.class_5537;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_5537.class})
public abstract class BundleItemMixin {
   @ModifyExpressionValue(
      method = {"getTooltipData"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z",
   ordinal = 0
)}
   )
   private boolean modifyContains1(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return (!bt.isActive() || !(Boolean)bt.tooltip.get()) && original;
   }

   @ModifyExpressionValue(
      method = {"getTooltipData"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z",
   ordinal = 1
)}
   )
   private boolean modifyContains2(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return (!bt.isActive() || !(Boolean)bt.additional.get()) && original;
   }
}

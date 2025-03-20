package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.class_8053;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_8053.class})
public abstract class ArmorTrimMixin {
   @ModifyExpressionValue(
      method = {"appendTooltip"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/item/trim/ArmorTrim;showInTooltip:Z"
)}
   )
   private boolean modifyShowInTooltip(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return bt.isActive() && (Boolean)bt.upgrades.get() || original;
   }
}

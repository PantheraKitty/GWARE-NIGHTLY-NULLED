package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import net.minecraft.class_9300;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_9300.class})
public abstract class UnbreakableComponentMixin {
   @ModifyExpressionValue(
      method = {"appendTooltip"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/component/type/UnbreakableComponent;showInTooltip:Z"
)}
   )
   private boolean modifyShowInTooltip(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return bt.isActive() && (Boolean)bt.unbreakable.get() || original;
   }
}

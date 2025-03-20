package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.FinishUsingItemEvent;
import meteordevelopment.meteorclient.events.entity.player.StoppedUsingItemEvent;
import meteordevelopment.meteorclient.events.game.ItemStackTooltipEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1799.class})
public abstract class ItemStackMixin {
   @ModifyReturnValue(
      method = {"getTooltip"},
      at = {@At("RETURN")}
   )
   private List<class_2561> onGetTooltip(List<class_2561> original) {
      if (Utils.canUpdate()) {
         ItemStackTooltipEvent event = (ItemStackTooltipEvent)MeteorClient.EVENT_BUS.post((Object)(new ItemStackTooltipEvent((class_1799)this, original)));
         return event.list();
      } else {
         return original;
      }
   }

   @ModifyExpressionValue(
      method = {"getTooltip"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/BlockPredicatesChecker;showInTooltip()Z",
   ordinal = 0
)}
   )
   private boolean modifyCanBreakText(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return bt.isActive() && (Boolean)bt.canDestroy.get() || original;
   }

   @ModifyExpressionValue(
      method = {"getTooltip"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/BlockPredicatesChecker;showInTooltip()Z",
   ordinal = 1
)}
   )
   private boolean modifyCanPlaceText(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return bt.isActive() && (Boolean)bt.canPlaceOn.get() || original;
   }

   @ModifyExpressionValue(
      method = {"getTooltip"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z",
   ordinal = 0
)}
   )
   private boolean modifyContainsTooltip(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return (!bt.isActive() || !(Boolean)bt.tooltip.get()) && original;
   }

   @ModifyExpressionValue(
      method = {"getTooltip"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/item/ItemStack;contains(Lnet/minecraft/component/ComponentType;)Z",
   ordinal = 3
)}
   )
   private boolean modifyContainsAdditional(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return (!bt.isActive() || !(Boolean)bt.additional.get()) && original;
   }

   @Inject(
      method = {"finishUsing"},
      at = {@At("HEAD")}
   )
   private void onFinishUsing(class_1937 world, class_1309 user, CallbackInfoReturnable<class_1799> info) {
      if (user == MeteorClient.mc.field_1724) {
         MeteorClient.EVENT_BUS.post((Object)FinishUsingItemEvent.get((class_1799)this));
      }

   }

   @Inject(
      method = {"onStoppedUsing"},
      at = {@At("HEAD")}
   )
   private void onStoppedUsing(class_1937 world, class_1309 user, int remainingUseTicks, CallbackInfo info) {
      if (user == MeteorClient.mc.field_1724) {
         MeteorClient.EVENT_BUS.post((Object)StoppedUsingItemEvent.get((class_1799)this));
      }

   }

   @ModifyExpressionValue(
      method = {"appendAttributeModifiersTooltip"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/component/type/AttributeModifiersComponent;showInTooltip()Z"
)}
   )
   private boolean modifyShowInTooltip(boolean original) {
      BetterTooltips bt = (BetterTooltips)Modules.get().get(BetterTooltips.class);
      return bt.isActive() && (Boolean)bt.modifiers.get() || original;
   }
}

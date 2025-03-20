package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.PlaceBlockEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.NoGhostBlocks;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1747;
import net.minecraft.class_1750;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1747.class})
public abstract class BlockItemMixin {
   @Shadow
   protected abstract class_2680 method_7707(class_1750 var1);

   @Inject(
      method = {"place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPlace(class_1750 context, class_2680 state, CallbackInfoReturnable<Boolean> info) {
      if (context.method_8045().field_9236) {
         if (((PlaceBlockEvent)MeteorClient.EVENT_BUS.post((ICancellable)PlaceBlockEvent.get(context.method_8037(), state.method_26204()))).isCancelled()) {
            info.setReturnValue(true);
         }

      }
   }

   @ModifyVariable(
      method = {"place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;"},
      ordinal = 1,
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
)
   )
   private class_2680 modifyState(class_2680 state, class_1750 context) {
      NoGhostBlocks noGhostBlocks = (NoGhostBlocks)Modules.get().get(NoGhostBlocks.class);
      return noGhostBlocks.isActive() && (Boolean)noGhostBlocks.placing.get() ? this.method_7707(context) : state;
   }
}

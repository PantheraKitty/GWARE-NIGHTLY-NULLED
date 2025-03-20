package meteordevelopment.meteorclient.mixin.lithium;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.orbit.ICancellable;
import net.caffeinemc.mods.lithium.common.entity.movement.ChunkAwareBlockCollisionSweeper;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3726;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ChunkAwareBlockCollisionSweeper.class})
public abstract class ChunkAwareBlockCollisionSweeperMixin {
   @Redirect(
      method = {"computeNext()Lnet/minecraft/util/shape/VoxelShape;"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"
)
   )
   private class_265 onComputeNextCollisionBox(class_2680 state, class_1922 world, class_2338 pos, class_3726 context) {
      class_265 shape = state.method_26194(world, pos, context);
      class_310 client = class_310.method_1551();
      if (world != client.field_1687) {
         return shape;
      } else {
         CollisionShapeEvent event = (CollisionShapeEvent)MeteorClient.EVENT_BUS.post((ICancellable)CollisionShapeEvent.get(state, pos, shape));
         return event.isCancelled() ? class_259.method_1073() : event.shape;
      }
   }
}

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.orbit.ICancellable;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3726;
import net.minecraft.class_5329;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({class_5329.class})
public abstract class BlockCollisionSpliteratorMixin {
   @WrapOperation(
      method = {"computeNext"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"
)}
   )
   private class_265 onComputeNextCollisionBox(class_2680 state, class_1922 world, class_2338 pos, class_3726 context, Operation<class_265> original) {
      class_265 shape = (class_265)original.call(new Object[]{state, world, pos, context});
      class_310 instance = class_310.method_1551();
      if (world != instance.field_1687) {
         return shape;
      } else {
         CollisionShapeEvent event = (CollisionShapeEvent)MeteorClient.EVENT_BUS.post((ICancellable)CollisionShapeEvent.get(state, pos, shape));
         return event.isCancelled() ? class_259.method_1073() : event.shape;
      }
   }
}

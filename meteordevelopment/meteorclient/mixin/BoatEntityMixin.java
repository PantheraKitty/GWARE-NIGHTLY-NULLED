package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.BoatMoveEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.BoatFly;
import net.minecraft.class_1690;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1690.class})
public abstract class BoatEntityMixin {
   @Shadow
   private boolean field_7710;
   @Shadow
   private boolean field_7695;

   @Inject(
      method = {"tick"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/vehicle/BoatEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
)}
   )
   private void onTickInvokeMove(CallbackInfo info) {
      MeteorClient.EVENT_BUS.post((Object)BoatMoveEvent.get((class_1690)this));
   }

   @Redirect(
      method = {"updatePaddles"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/entity/vehicle/BoatEntity;pressingLeft:Z"
)
   )
   private boolean onUpdatePaddlesPressingLeft(class_1690 boat) {
      return Modules.get().isActive(BoatFly.class) ? false : this.field_7710;
   }

   @Redirect(
      method = {"updatePaddles"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/entity/vehicle/BoatEntity;pressingRight:Z"
)
   )
   private boolean onUpdatePaddlesPressingRight(class_1690 boat) {
      return Modules.get().isActive(BoatFly.class) ? false : this.field_7695;
   }
}

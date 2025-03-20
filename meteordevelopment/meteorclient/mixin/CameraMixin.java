package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ICamera;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.CameraTweaks;
import meteordevelopment.meteorclient.systems.modules.render.FreeLook;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_5636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_4184.class})
public abstract class CameraMixin implements ICamera {
   @Shadow
   private boolean field_18719;
   @Shadow
   private float field_18718;
   @Shadow
   private float field_18717;
   @Unique
   private float tickDelta;

   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Inject(
      method = {"getSubmersionType"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getSubmergedFluidState(CallbackInfoReturnable<class_5636> ci) {
      if (((NoRender)Modules.get().get(NoRender.class)).noLiquidOverlay()) {
         ci.setReturnValue(class_5636.field_27888);
      }

   }

   @ModifyVariable(
      method = {"clipToSpace"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private float modifyClipToSpace(float d) {
      return ((Freecam)Modules.get().get(Freecam.class)).isActive() ? 0.0F : (float)((CameraTweaks)Modules.get().get(CameraTweaks.class)).getDistance();
   }

   @Inject(
      method = {"clipToSpace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onClipToSpace(float desiredCameraDistance, CallbackInfoReturnable<Float> info) {
      if (((CameraTweaks)Modules.get().get(CameraTweaks.class)).clip()) {
         info.setReturnValue(desiredCameraDistance);
      }

   }

   @Inject(
      method = {"update"},
      at = {@At("HEAD")}
   )
   private void onUpdateHead(class_1922 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
      this.tickDelta = tickDelta;
   }

   @Inject(
      method = {"update"},
      at = {@At("TAIL")}
   )
   private void onUpdateTail(class_1922 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
      if (Modules.get().isActive(Freecam.class)) {
         this.field_18719 = true;
      }

   }

   @ModifyArgs(
      method = {"update"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"
)
   )
   private void onUpdateSetPosArgs(Args args) {
      Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
      if (freecam.isActive()) {
         args.set(0, freecam.getX(this.tickDelta));
         args.set(1, freecam.getY(this.tickDelta));
         args.set(2, freecam.getZ(this.tickDelta));
      }

   }

   @ModifyArgs(
      method = {"update"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"
)
   )
   private void onUpdateSetRotationArgs(Args args) {
      Freecam freecam = (Freecam)Modules.get().get(Freecam.class);
      FreeLook freeLook = (FreeLook)Modules.get().get(FreeLook.class);
      if (freecam.isActive()) {
         args.set(0, (float)freecam.getYaw(this.tickDelta));
         args.set(1, (float)freecam.getPitch(this.tickDelta));
      } else if (Modules.get().isActive(HighwayBuilder.class)) {
         args.set(0, this.field_18718);
         args.set(1, this.field_18717);
      } else if (freeLook.isActive()) {
         args.set(0, freeLook.cameraYaw);
         args.set(1, freeLook.cameraPitch);
      }

   }

   public void setRot(double yaw, double pitch) {
      this.method_19325((float)yaw, (float)class_3532.method_15350(pitch, -90.0D, 90.0D));
   }
}
